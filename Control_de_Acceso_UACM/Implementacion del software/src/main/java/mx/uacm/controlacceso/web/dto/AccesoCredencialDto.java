package mx.uacm.controlacceso.web.dto;

import mx.uacm.controlacceso.dominio.CredencialInstitucional;
import mx.uacm.controlacceso.dominio.EstadoCredencial;
import mx.uacm.controlacceso.dominio.EstadoUsuarioInstitucional;
import mx.uacm.controlacceso.dominio.MetodoAcceso;
import mx.uacm.controlacceso.dominio.UsuarioInstitucional;

import java.time.LocalDateTime;

/**
 * Resultado del escaneo de una credencial. El sistema decide y registra el
 * acceso automáticamente; además del resultado permitido/denegado, la vista
 * necesita los datos de identidad y la posibilidad de autorizar un caso
 * restringido con advertencia.
 */
public class AccesoCredencialDto {

    private boolean encontrada;
    private boolean permitido;
    private boolean puedeAutorizar;
    private boolean autorizadoConAdvertencia;
    private String nombre;
    private String matricula;
    private String iniciales;
    private String fotografiaUrl;
    private String tipoUsuario;
    private String estadoUsuario;
    private String estadoUsuarioCss;
    private String estadoCredencial;
    private String estadoCredencialCss;
    private String mensaje;
    private String motivo;
    private String metodo;
    private String puntoAcceso;
    private LocalDateTime fechaHora;
    private Long accesoId;

    public static AccesoCredencialDto permitido(UsuarioInstitucional usuario,
                                                CredencialInstitucional credencial,
                                                MetodoAcceso metodo,
                                                String puntoAcceso,
                                                Long accesoId) {
        AccesoCredencialDto dto = base(usuario, credencial, metodo, puntoAcceso, accesoId);
        dto.permitido = true;
        dto.puedeAutorizar = false;
        dto.autorizadoConAdvertencia = false;
        dto.mensaje = "Acceso permitido. Da indicaciones para ingresar al plantel.";
        return dto;
    }

    public static AccesoCredencialDto permitidoConAdvertencia(UsuarioInstitucional usuario,
                                                              CredencialInstitucional credencial,
                                                              MetodoAcceso metodo,
                                                              String puntoAcceso,
                                                              Long accesoId,
                                                              String motivoOriginal) {
        AccesoCredencialDto dto = base(usuario, credencial, metodo, puntoAcceso, accesoId);
        dto.permitido = true;
        dto.puedeAutorizar = false;
        dto.autorizadoConAdvertencia = true;
        dto.motivo = motivoOriginal;
        dto.mensaje = "Acceso permitido con advertencia. El Guardia autorizó el ingreso a pesar de la restricción.";
        return dto;
    }

    public static AccesoCredencialDto denegado(String mensaje,
                                               UsuarioInstitucional usuario,
                                               CredencialInstitucional credencial,
                                               MetodoAcceso metodo,
                                               String puntoAcceso,
                                               Long accesoId) {
        AccesoCredencialDto dto = base(usuario, credencial, metodo, puntoAcceso, accesoId);
        dto.permitido = false;
        dto.puedeAutorizar = true;
        dto.autorizadoConAdvertencia = false;
        dto.mensaje = mensaje;
        dto.motivo = mensaje;
        return dto;
    }

    public static AccesoCredencialDto noEncontrada(String mensaje,
                                                   MetodoAcceso metodo,
                                                   String puntoAcceso,
                                                   Long accesoId) {
        AccesoCredencialDto dto = new AccesoCredencialDto();
        dto.encontrada = false;
        dto.permitido = false;
        dto.puedeAutorizar = false;
        dto.autorizadoConAdvertencia = false;
        dto.mensaje = mensaje;
        dto.motivo = mensaje;
        dto.metodo = metodo.getDescripcion();
        dto.puntoAcceso = puntoAcceso;
        dto.fechaHora = LocalDateTime.now();
        dto.accesoId = accesoId;
        return dto;
    }

    private static AccesoCredencialDto base(UsuarioInstitucional usuario,
                                            CredencialInstitucional credencial,
                                            MetodoAcceso metodo,
                                            String puntoAcceso,
                                            Long accesoId) {
        AccesoCredencialDto dto = new AccesoCredencialDto();
        dto.encontrada = true;
        dto.nombre = usuario.getNombreCompleto();
        dto.matricula = usuario.getMatricula();
        dto.iniciales = usuario.getIniciales();
        dto.fotografiaUrl = usuario.getFotografiaUrl();
        dto.tipoUsuario = usuario.getTipo().getDescripcion();
        dto.estadoUsuario = usuario.getEstado().getDescripcion();
        dto.estadoUsuarioCss = cssEstadoUsuario(usuario.getEstado());

        EstadoCredencial estadoCredencial = estadoEfectivoCredencial(credencial);
        dto.estadoCredencial = estadoCredencial.getDescripcion();
        dto.estadoCredencialCss = cssEstadoCredencial(estadoCredencial);
        dto.metodo = metodo.getDescripcion();
        dto.puntoAcceso = puntoAcceso;
        dto.fechaHora = LocalDateTime.now();
        dto.accesoId = accesoId;
        return dto;
    }

    private static EstadoCredencial estadoEfectivoCredencial(CredencialInstitucional credencial) {
        if (credencial.getEstado() == EstadoCredencial.ACTIVA && !credencial.isVigente()) {
            return EstadoCredencial.VENCIDA;
        }
        return credencial.getEstado();
    }

    private static String cssEstadoUsuario(EstadoUsuarioInstitucional estado) {
        return switch (estado) {
            case ACTIVO -> "success";
            case PROHIBIDO -> "danger";
            case INACTIVO -> "secondary";
        };
    }

    private static String cssEstadoCredencial(EstadoCredencial estado) {
        return switch (estado) {
            case ACTIVA -> "success";
            case VENCIDA -> "warning";
            case BLOQUEADA -> "danger";
        };
    }

    public boolean isEncontrada() {
        return encontrada;
    }

    public boolean isPermitido() {
        return permitido;
    }

    public boolean isPuedeAutorizar() {
        return puedeAutorizar;
    }

    public boolean isAutorizadoConAdvertencia() {
        return autorizadoConAdvertencia;
    }

    public String getNombre() {
        return nombre;
    }

    public String getMatricula() {
        return matricula;
    }

    public String getIniciales() {
        return iniciales;
    }

    public String getFotografiaUrl() {
        return fotografiaUrl;
    }

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public String getEstadoUsuario() {
        return estadoUsuario;
    }

    public String getEstadoUsuarioCss() {
        return estadoUsuarioCss;
    }

    public String getEstadoCredencial() {
        return estadoCredencial;
    }

    public String getEstadoCredencialCss() {
        return estadoCredencialCss;
    }

    public String getMensaje() {
        return mensaje;
    }

    public String getMotivo() {
        return motivo;
    }

    public String getMetodo() {
        return metodo;
    }

    public String getPuntoAcceso() {
        return puntoAcceso;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public Long getAccesoId() {
        return accesoId;
    }
}
