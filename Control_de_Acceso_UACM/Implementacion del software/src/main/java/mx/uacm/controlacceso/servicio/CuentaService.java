package mx.uacm.controlacceso.servicio;

import mx.uacm.controlacceso.dominio.CuentaUsuario;
import mx.uacm.controlacceso.dominio.EstadoCuenta;
import mx.uacm.controlacceso.dominio.Rol;
import mx.uacm.controlacceso.dominio.TipoEvento;
import mx.uacm.controlacceso.repositorio.CuentaUsuarioRepository;
import mx.uacm.controlacceso.web.dto.FormularioCuenta;
import mx.uacm.controlacceso.web.dto.FormularioEdicionCuenta;
import mx.uacm.controlacceso.web.dto.FormularioPassword;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class CuentaService {

    private final CuentaUsuarioRepository repositorio;
    private final PasswordEncoder passwordEncoder;
    private final BitacoraService bitacoraService;
    private final int maxIntentos;

    public CuentaService(CuentaUsuarioRepository repositorio,
                         PasswordEncoder passwordEncoder,
                         BitacoraService bitacoraService,
                         @Value("${app.seguridad.max-intentos:5}") int maxIntentos) {
        this.repositorio = repositorio;
        this.passwordEncoder = passwordEncoder;
        this.bitacoraService = bitacoraService;
        this.maxIntentos = maxIntentos;
    }

    @Transactional(readOnly = true)
    public CuentaUsuario obtenerPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(() -> new ReglaNegocioException("La cuenta no existe."));
    }

    @Transactional(readOnly = true)
    public List<CuentaUsuario> listar() {
        return repositorio.findAllByOrderByNombreAsc();
    }

    @Transactional(readOnly = true)
    public long contarPorEstado(EstadoCuenta estado) {
        return repositorio.countByEstado(estado);
    }

    @Transactional
    public CuentaUsuario crear(FormularioCuenta formulario, CuentaUsuario actor, String ip) {
        String username = formulario.getUsername().trim().toLowerCase();
        String correo = formulario.getCorreo().trim().toLowerCase();

        if (!formulario.getPassword().equals(formulario.getConfirmarPassword())) {
            throw new ReglaNegocioException("Las contraseñas temporales no coinciden.");
        }
        if (repositorio.existsByUsernameIgnoreCase(username)) {
            throw new ReglaNegocioException("Ya existe una cuenta con el usuario " + username + ".");
        }
        if (repositorio.existsByCorreoIgnoreCase(correo)) {
            throw new ReglaNegocioException("Ya existe una cuenta con el correo " + correo + ".");
        }

        CuentaUsuario cuenta = new CuentaUsuario();
        cuenta.setUsername(username);
        cuenta.setNombre(formulario.getNombre().trim());
        cuenta.setCorreo(correo);
        cuenta.setRol(formulario.getRol());
        cuenta.setPasswordHash(passwordEncoder.encode(formulario.getPassword()));
        cuenta.setEstado(EstadoCuenta.ACTIVA);
        cuenta.setIntentosFallidos(0);
        cuenta.setRequiereCambioPassword(true);
        cuenta.setFechaCreacion(LocalDateTime.now());

        CuentaUsuario guardada = repositorio.save(cuenta);
        bitacoraService.registrar(TipoEvento.CUENTA_CREADA,
                "Cuenta creada para " + guardada.getNombre() + " con rol " + guardada.getRol().getDescripcion() + ".",
                actor, "CuentaUsuario", guardada.getId(), "EXITOSO", ip);
        return guardada;
    }

    @Transactional
    public CuentaUsuario actualizar(Long id, FormularioEdicionCuenta formulario,
                                    CuentaUsuario actor, String ip) {
        CuentaUsuario cuenta = obtenerPorId(id);
        String correo = formulario.getCorreo().trim().toLowerCase();

        if (cuenta.getId().equals(actor.getId()) && formulario.getRol() != Rol.DIRECCION_ENLACE) {
            throw new ReglaNegocioException("No puedes quitarte tu propio rol de Dirección de Enlace.");
        }
        if (repositorio.existsByCorreoIgnoreCaseAndIdNot(correo, id)) {
            throw new ReglaNegocioException("Ya existe otra cuenta con el correo " + correo + ".");
        }

        cuenta.setNombre(formulario.getNombre().trim());
        cuenta.setCorreo(correo);
        cuenta.setRol(formulario.getRol());

        CuentaUsuario guardada = repositorio.save(cuenta);
        bitacoraService.registrar(TipoEvento.CUENTA_MODIFICADA,
                "Cuenta modificada: " + guardada.getNombre() + ", rol " + guardada.getRol().getDescripcion() + ".",
                actor, "CuentaUsuario", guardada.getId(), "EXITOSO", ip);
        return guardada;
    }

    @Transactional
    public void desactivar(Long id, CuentaUsuario actor, String ip) {
        if (id.equals(actor.getId())) {
            throw new ReglaNegocioException("No puedes desactivar tu propia cuenta.");
        }
        CuentaUsuario cuenta = obtenerPorId(id);
        cuenta.setEstado(EstadoCuenta.DESACTIVADA);
        repositorio.save(cuenta);
        bitacoraService.registrar(TipoEvento.CUENTA_DESACTIVADA,
                "Cuenta desactivada: " + cuenta.getUsername() + ". El historial se conserva.",
                actor, "CuentaUsuario", cuenta.getId(), "EXITOSO", ip);
    }

    @Transactional
    public void reactivar(Long id, CuentaUsuario actor, String ip) {
        CuentaUsuario cuenta = obtenerPorId(id);
        cuenta.setEstado(EstadoCuenta.ACTIVA);
        cuenta.setIntentosFallidos(0);
        repositorio.save(cuenta);
        bitacoraService.registrar(TipoEvento.CUENTA_REACTIVADA,
                "Cuenta reactivada: " + cuenta.getUsername() + ".",
                actor, "CuentaUsuario", cuenta.getId(), "EXITOSO", ip);
    }

    @Transactional
    public void desbloquear(Long id, CuentaUsuario actor, String ip) {
        CuentaUsuario cuenta = obtenerPorId(id);
        cuenta.setEstado(EstadoCuenta.ACTIVA);
        cuenta.setIntentosFallidos(0);
        cuenta.setRequiereCambioPassword(true);
        repositorio.save(cuenta);
        bitacoraService.registrar(TipoEvento.CUENTA_DESBLOQUEADA,
                "Cuenta desbloqueada: " + cuenta.getUsername() + ". Se solicita cambio de contraseña.",
                actor, "CuentaUsuario", cuenta.getId(), "EXITOSO", ip);
    }

    @Transactional
    public void cambiarPassword(Long id, FormularioPassword formulario, String ip) {
        CuentaUsuario cuenta = obtenerPorId(id);

        if (!passwordEncoder.matches(formulario.getActual(), cuenta.getPasswordHash())) {
            throw new ReglaNegocioException("La contraseña actual no es correcta.");
        }
        if (!formulario.getNueva().equals(formulario.getConfirmar())) {
            throw new ReglaNegocioException("La nueva contraseña y su confirmación no coinciden.");
        }
        if (formulario.getNueva().equals(formulario.getActual())) {
            throw new ReglaNegocioException("La nueva contraseña debe ser diferente a la actual.");
        }

        cuenta.setPasswordHash(passwordEncoder.encode(formulario.getNueva()));
        cuenta.setRequiereCambioPassword(false);
        repositorio.save(cuenta);
        bitacoraService.registrar(TipoEvento.CAMBIO_PASSWORD,
                "Contraseña actualizada por el propio usuario.",
                cuenta, "CuentaUsuario", cuenta.getId(), "EXITOSO", ip);
    }

    @Transactional
    public void registrarIntentoFallido(String username, String ip) {
        if (username == null || username.isBlank()) {
            bitacoraService.registrar(TipoEvento.INTENTO_FALLIDO,
                    "Intento de inicio de sesión sin usuario.",
                    null, "CuentaUsuario", null, "FALLIDO", ip);
            return;
        }

        Optional<CuentaUsuario> optional = repositorio.findByUsernameIgnoreCase(username.trim());
        if (optional.isEmpty()) {
            bitacoraService.registrar(TipoEvento.INTENTO_FALLIDO,
                    "Intento fallido para un usuario no registrado: " + username.trim() + ".",
                    null, "CuentaUsuario", null, "FALLIDO", ip);
            return;
        }

        CuentaUsuario cuenta = optional.get();
        if (cuenta.getEstado() == EstadoCuenta.BLOQUEADA) {
            bitacoraService.registrar(TipoEvento.INTENTO_FALLIDO,
                    "Intento de acceso a una cuenta bloqueada: " + cuenta.getUsername() + ".",
                    cuenta, "CuentaUsuario", cuenta.getId(), "FALLIDO", ip);
            return;
        }
        if (cuenta.getEstado() == EstadoCuenta.DESACTIVADA) {
            bitacoraService.registrar(TipoEvento.INTENTO_FALLIDO,
                    "Intento de acceso a una cuenta desactivada: " + cuenta.getUsername() + ".",
                    cuenta, "CuentaUsuario", cuenta.getId(), "FALLIDO", ip);
            return;
        }

        int intentos = cuenta.getIntentosFallidos() + 1;
        cuenta.setIntentosFallidos(intentos);

        if (intentos >= maxIntentos) {
            cuenta.setEstado(EstadoCuenta.BLOQUEADA);
            repositorio.save(cuenta);
            bitacoraService.registrar(TipoEvento.CUENTA_BLOQUEADA,
                    "Cuenta bloqueada tras " + intentos + " intentos fallidos consecutivos.",
                    cuenta, "CuentaUsuario", cuenta.getId(), "BLOQUEADA", ip);
        } else {
            repositorio.save(cuenta);
            bitacoraService.registrar(TipoEvento.INTENTO_FALLIDO,
                    "Intento fallido " + intentos + " de " + maxIntentos + " para " + cuenta.getUsername() + ".",
                    cuenta, "CuentaUsuario", cuenta.getId(), "FALLIDO", ip);
        }
    }

    @Transactional
    public void registrarInicioSesion(Long id, String ip) {
        CuentaUsuario cuenta = obtenerPorId(id);
        cuenta.setIntentosFallidos(0);
        cuenta.setUltimoAcceso(LocalDateTime.now());
        repositorio.save(cuenta);
        bitacoraService.registrar(TipoEvento.INICIO_SESION,
                "Inicio de sesión correcto.",
                cuenta, "CuentaUsuario", cuenta.getId(), "EXITOSO", ip);
    }

    @Transactional
    public void registrarCierreSesion(Long id, String ip) {
        CuentaUsuario cuenta = obtenerPorId(id);
        bitacoraService.registrar(TipoEvento.CIERRE_SESION,
                "Cierre de sesión.",
                cuenta, "CuentaUsuario", cuenta.getId(), "EXITOSO", ip);
    }
}
