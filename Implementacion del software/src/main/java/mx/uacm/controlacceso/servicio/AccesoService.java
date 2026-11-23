package mx.uacm.controlacceso.servicio;

import mx.uacm.controlacceso.dominio.AccesoInstitucional;
import mx.uacm.controlacceso.dominio.CredencialInstitucional;
import mx.uacm.controlacceso.dominio.CuentaUsuario;
import mx.uacm.controlacceso.dominio.EstadoCredencial;
import mx.uacm.controlacceso.dominio.EstadoUsuarioInstitucional;
import mx.uacm.controlacceso.dominio.MetodoAcceso;
import mx.uacm.controlacceso.dominio.ResultadoAcceso;
import mx.uacm.controlacceso.dominio.TipoEvento;
import mx.uacm.controlacceso.dominio.UsuarioInstitucional;
import mx.uacm.controlacceso.repositorio.AccesoInstitucionalRepository;
import mx.uacm.controlacceso.repositorio.CredencialInstitucionalRepository;
import mx.uacm.controlacceso.repositorio.UsuarioInstitucionalRepository;
import mx.uacm.controlacceso.web.dto.AccesoCredencialDto;
import mx.uacm.controlacceso.web.dto.ResultadoAccesoDto;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class AccesoService {

    private final CredencialInstitucionalRepository credencialRepository;
    private final UsuarioInstitucionalRepository usuarioRepository;
    private final AccesoInstitucionalRepository accesoRepository;
    private final BitacoraService bitacoraService;

    public AccesoService(CredencialInstitucionalRepository credencialRepository,
                         UsuarioInstitucionalRepository usuarioRepository,
                         AccesoInstitucionalRepository accesoRepository,
                         BitacoraService bitacoraService) {
        this.credencialRepository = credencialRepository;
        this.usuarioRepository = usuarioRepository;
        this.accesoRepository = accesoRepository;
        this.bitacoraService = bitacoraService;
    }

    /**
     * Registra automáticamente un acceso a partir del código escaneado. El
     * sistema decide si el acceso se permite o se deniega según el estado de la
     * persona y de la credencial. En ambos casos se guarda un
     * {@link AccesoInstitucional} y se agrega el evento correspondiente a la
     * bitácora.
     */
    @Transactional
    public AccesoCredencialDto registrarAccesoPorCredencial(String codigo, String puntoAcceso,
                                                            MetodoAcceso metodo,
                                                            CuentaUsuario guardia, String ip) {
        String codigoLimpio = codigo == null ? "" : codigo.trim();
        String punto = normalizarPunto(puntoAcceso);
        MetodoAcceso metodoFinal = metodo == null ? MetodoAcceso.QR : metodo;

        if (codigoLimpio.isEmpty()) {
            return registrarDenegadoCredencial(null, null, "", "Código de credencial vacío.",
                    metodoFinal, punto, guardia, ip);
        }

        Optional<CredencialInstitucional> optional = credencialRepository.findByCodigoQr(codigoLimpio);
        if (optional.isEmpty()) {
            return registrarDenegadoCredencial(null, null, codigoLimpio,
                    "La credencial no existe en el directorio institucional.",
                    metodoFinal, punto, guardia, ip);
        }

        CredencialInstitucional credencial = optional.get();
        UsuarioInstitucional usuario = credencial.getUsuario();

        String motivo = validarCredencialYUsuario(credencial, usuario);
        if (motivo != null) {
            return registrarDenegadoCredencial(credencial, usuario, codigoLimpio, motivo,
                    metodoFinal, punto, guardia, ip);
        }

        AccesoInstitucional acceso = registrarAcceso(credencial, usuario, codigoLimpio,
                metodoFinal, ResultadoAcceso.PERMITIDO, null, punto, guardia);

        bitacoraService.registrar(TipoEvento.ACCESO_PERMITIDO,
                "Acceso permitido a " + usuario.getNombreCompleto() + " (" + usuario.getMatricula()
                        + ") por " + metodoFinal.getDescripcion() + " en " + punto + ".",
                guardia, "AccesoInstitucional", acceso.getId(), "PERMITIDO", ip);

        return AccesoCredencialDto.permitido(usuario, credencial, metodoFinal, punto, acceso.getId());
    }

    /**
     * Autoriza con advertencia un acceso que el sistema había denegado. Se
     * conserva el registro denegado original y se crea uno nuevo permitido,
     * enlazado mediante {@code accesoOrigenId}, para que la bitácora refleje
     * tanto la decisión automática como la autorización del Guardia.
     */
    @Transactional
    public AccesoCredencialDto autorizarConAdvertencia(Long accesoId, CuentaUsuario guardia, String ip) {
        AccesoInstitucional denegado = accesoRepository.findById(accesoId)
                .orElseThrow(() -> new ReglaNegocioException("El acceso denegado ya no existe."));

        if (denegado.getResultado() != ResultadoAcceso.DENEGADO) {
            throw new ReglaNegocioException("Solo se puede autorizar un acceso que fue denegado.");
        }
        if (denegado.getUsuario() == null || denegado.getCredencial() == null) {
            throw new ReglaNegocioException("No se puede autorizar una credencial no reconocida.");
        }
        if (accesoRepository.existsByAccesoOrigenId(accesoId)) {
            throw new ReglaNegocioException("Este acceso ya fue autorizado con advertencia.");
        }

        UsuarioInstitucional usuario = denegado.getUsuario();
        CredencialInstitucional credencial = denegado.getCredencial();
        String motivoSistema = denegado.getMotivo() == null
                ? "Restricción institucional." : denegado.getMotivo();
        String motivo = "Autorizado por el Guardia con advertencia. Motivo del sistema: " + motivoSistema;
        String punto = normalizarPunto(denegado.getPuntoAcceso());

        AccesoInstitucional acceso = registrarAcceso(credencial, usuario, denegado.getCodigoIngresado(),
                denegado.getMetodo(), ResultadoAcceso.PERMITIDO, motivo, punto, guardia, accesoId);

        bitacoraService.registrar(TipoEvento.ACCESO_PERMITIDO,
                "Acceso permitido con advertencia a " + usuario.getNombreCompleto()
                        + " (" + usuario.getMatricula() + ") por "
                        + denegado.getMetodo().getDescripcion() + " en " + punto
                        + ". El Guardia autorizó el ingreso. Motivo del sistema: " + motivoSistema,
                guardia, "AccesoInstitucional", acceso.getId(), "PERMITIDO", ip);

        return AccesoCredencialDto.permitidoConAdvertencia(usuario, credencial,
                denegado.getMetodo(), punto, acceso.getId(), motivoSistema);
    }

    @Transactional
    public ResultadoAccesoDto validarManual(String matricula, String puntoAcceso,
                                            CuentaUsuario guardia, String ip) {
        String matriculaLimpia = matricula == null ? "" : matricula.trim();
        String punto = normalizarPunto(puntoAcceso);

        if (matriculaLimpia.isEmpty()) {
            return registrarDenegado(null, null, "", "Matrícula vacía.",
                    MetodoAcceso.MANUAL_MATRICULA, punto, guardia, ip);
        }

        Optional<UsuarioInstitucional> optional = usuarioRepository.findByMatriculaIgnoreCase(matriculaLimpia);
        if (optional.isEmpty()) {
            return registrarDenegado(null, null, matriculaLimpia,
                    "La matrícula no existe en el directorio institucional.",
                    MetodoAcceso.MANUAL_MATRICULA, punto, guardia, ip);
        }

        UsuarioInstitucional usuario = optional.get();
        String motivo = validarUsuario(usuario);
        if (motivo != null) {
            return registrarDenegado(null, usuario, matriculaLimpia, motivo,
                    MetodoAcceso.MANUAL_MATRICULA, punto, guardia, ip);
        }

        // La validación manual requiere que el guardia compare los datos y confirme.
        return ResultadoAccesoDto.confirmacion(usuario, punto);
    }

    @Transactional
    public ResultadoAccesoDto confirmarManual(Long usuarioId, String puntoAcceso,
                                              CuentaUsuario guardia, String ip) {
        String punto = normalizarPunto(puntoAcceso);
        UsuarioInstitucional usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new ReglaNegocioException("El usuario institucional ya no existe."));

        String motivo = validarUsuario(usuario);
        if (motivo != null) {
            return registrarDenegado(null, usuario, usuario.getMatricula(), motivo,
                    MetodoAcceso.MANUAL_MATRICULA, punto, guardia, ip);
        }

        CredencialInstitucional credencial = credencialRepository
                .findFirstByUsuarioIdOrderByFechaVencimientoDesc(usuarioId)
                .orElse(null);

        AccesoInstitucional acceso = registrarAcceso(credencial, usuario, usuario.getMatricula(),
                MetodoAcceso.MANUAL_MATRICULA, ResultadoAcceso.PERMITIDO, null, punto, guardia);

        bitacoraService.registrar(TipoEvento.VALIDACION_MANUAL,
                "Validación manual por matrícula para " + usuario.getNombreCompleto()
                        + " (" + usuario.getMatricula() + ") en " + punto + ".",
                guardia, "AccesoInstitucional", acceso.getId(), "PERMITIDO", ip);

        bitacoraService.registrar(TipoEvento.ACCESO_PERMITIDO,
                "Acceso permitido a " + usuario.getNombreCompleto() + " (" + usuario.getMatricula()
                        + ") por validación manual en " + punto + ".",
                guardia, "AccesoInstitucional", acceso.getId(), "PERMITIDO", ip);

        return ResultadoAccesoDto.permitido(usuario, MetodoAcceso.MANUAL_MATRICULA, punto, acceso.getId());
    }

    @Transactional(readOnly = true)
    public List<AccesoInstitucional> listarRecientes(Long guardiaId, boolean todos) {
        if (todos) {
            return accesoRepository.findTop10ByOrderByFechaHoraDesc();
        }
        return accesoRepository.findTop10ByGuardiaIdOrderByFechaHoraDesc(guardiaId);
    }

    @Transactional(readOnly = true)
    public long contarHoy(ResultadoAcceso resultado) {
        LocalDateTime inicioDia = LocalDate.now().atStartOfDay();
        return accesoRepository.countByResultadoAndFechaHoraAfter(resultado, inicioDia);
    }

    private String validarCredencialYUsuario(CredencialInstitucional credencial,
                                             UsuarioInstitucional usuario) {
        if (credencial.getEstado() == EstadoCredencial.BLOQUEADA) {
            return "La credencial está bloqueada.";
        }
        if (credencial.getEstado() == EstadoCredencial.VENCIDA
                || credencial.getFechaVencimiento() == null
                || credencial.getFechaVencimiento().isBefore(LocalDate.now())) {
            return "La credencial está vencida.";
        }
        if (credencial.getEstado() != EstadoCredencial.ACTIVA) {
            return "La credencial no está activa.";
        }
        return validarUsuario(usuario);
    }

    private String validarUsuario(UsuarioInstitucional usuario) {
        if (usuario.getEstado() == EstadoUsuarioInstitucional.PROHIBIDO) {
            return "El usuario tiene prohibición de acceso al plantel.";
        }
        if (usuario.getEstado() == EstadoUsuarioInstitucional.INACTIVO) {
            return "El usuario no está activo en el directorio institucional.";
        }
        if (usuario.getEstado() != EstadoUsuarioInstitucional.ACTIVO) {
            return "El estado del usuario no permite el acceso.";
        }
        return null;
    }

    private AccesoCredencialDto registrarDenegadoCredencial(CredencialInstitucional credencial,
                                                            UsuarioInstitucional usuario,
                                                            String codigoIngresado, String motivo,
                                                            MetodoAcceso metodo, String punto,
                                                            CuentaUsuario guardia, String ip) {
        AccesoInstitucional acceso = registrarAcceso(credencial, usuario, codigoIngresado,
                metodo, ResultadoAcceso.DENEGADO, motivo, punto, guardia);

        String nombre = usuario != null ? usuario.getNombreCompleto() : null;

        bitacoraService.registrar(TipoEvento.ACCESO_DENEGADO,
                "Acceso denegado" + (nombre != null ? " a " + nombre : "")
                        + " por " + metodo.getDescripcion() + " en " + punto + ": " + motivo,
                guardia, "AccesoInstitucional", acceso.getId(), "DENEGADO", ip);

        if (usuario == null || credencial == null) {
            return AccesoCredencialDto.noEncontrada(motivo, metodo, punto, acceso.getId());
        }
        return AccesoCredencialDto.denegado(motivo, usuario, credencial, metodo, punto, acceso.getId());
    }

    private ResultadoAccesoDto registrarDenegado(CredencialInstitucional credencial,
                                                 UsuarioInstitucional usuario,
                                                 String codigoIngresado, String motivo,
                                                 MetodoAcceso metodo, String punto,
                                                 CuentaUsuario guardia, String ip) {
        AccesoInstitucional acceso = registrarAcceso(credencial, usuario, codigoIngresado,
                metodo, ResultadoAcceso.DENEGADO, motivo, punto, guardia);

        String nombre = usuario != null ? usuario.getNombreCompleto() : null;
        String matricula = usuario != null ? usuario.getMatricula() : null;
        String iniciales = usuario != null ? usuario.getIniciales() : null;

        bitacoraService.registrar(TipoEvento.ACCESO_DENEGADO,
                "Acceso denegado" + (nombre != null ? " a " + nombre : "")
                        + " por " + metodo.getDescripcion() + " en " + punto + ": " + motivo,
                guardia, "AccesoInstitucional", acceso.getId(), "DENEGADO", ip);

        return ResultadoAccesoDto.denegado(motivo, nombre, matricula, iniciales,
                metodo, punto, acceso.getId());
    }

    private AccesoInstitucional registrarAcceso(CredencialInstitucional credencial,
                                                UsuarioInstitucional usuario,
                                                String codigoIngresado,
                                                MetodoAcceso metodo,
                                                ResultadoAcceso resultado,
                                                String motivo,
                                                String punto,
                                                CuentaUsuario guardia) {
        return registrarAcceso(credencial, usuario, codigoIngresado, metodo, resultado,
                motivo, punto, guardia, null);
    }

    private AccesoInstitucional registrarAcceso(CredencialInstitucional credencial,
                                                UsuarioInstitucional usuario,
                                                String codigoIngresado,
                                                MetodoAcceso metodo,
                                                ResultadoAcceso resultado,
                                                String motivo,
                                                String punto,
                                                CuentaUsuario guardia,
                                                Long accesoOrigenId) {
        AccesoInstitucional acceso = new AccesoInstitucional();
        acceso.setCredencial(credencial);
        acceso.setUsuario(usuario);
        acceso.setCodigoIngresado(codigoIngresado);
        acceso.setFechaHora(LocalDateTime.now());
        acceso.setMetodo(metodo);
        acceso.setResultado(resultado);
        acceso.setMotivo(motivo);
        acceso.setPuntoAcceso(punto);
        acceso.setGuardia(guardia);
        acceso.setAccesoOrigenId(accesoOrigenId);
        return accesoRepository.save(acceso);
    }

    private String normalizarPunto(String puntoAcceso) {
        return (puntoAcceso == null || puntoAcceso.isBlank()) ? "Acceso principal" : puntoAcceso.trim();
    }
}
