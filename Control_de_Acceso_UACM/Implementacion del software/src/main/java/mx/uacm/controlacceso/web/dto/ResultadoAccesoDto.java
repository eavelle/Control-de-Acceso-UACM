package mx.uacm.controlacceso.web.dto;

import mx.uacm.controlacceso.dominio.MetodoAcceso;
import mx.uacm.controlacceso.dominio.UsuarioInstitucional;

import java.time.LocalDateTime;

/**
 * Resultado que se muestra al guardia después de validar una credencial o una
 * matrícula. No es una entidad: es la respuesta de la capa de servicio hacia la
 * vista.
 */
public class ResultadoAccesoDto {

    private boolean permitido;
    private boolean requiereConfirmacion;
    private String mensaje;
    private String nombre;
    private String matricula;
    private String iniciales;
    private String tipoUsuario;
    private String estadoUsuario;
    private String metodo;
    private String puntoAcceso;
    private LocalDateTime fechaHora;
    private Long usuarioId;
    private Long accesoId;

    public static ResultadoAccesoDto permitido(UsuarioInstitucional usuario, MetodoAcceso metodo,
                                               String puntoAcceso, Long accesoId) {
        ResultadoAccesoDto dto = new ResultadoAccesoDto();
        dto.permitido = true;
        dto.requiereConfirmacion = false;
        dto.mensaje = "Acceso permitido. Da indicaciones para ingresar al plantel.";
        dto.nombre = usuario.getNombreCompleto();
        dto.matricula = usuario.getMatricula();
        dto.iniciales = usuario.getIniciales();
        dto.tipoUsuario = usuario.getTipo().getDescripcion();
        dto.estadoUsuario = usuario.getEstado().getDescripcion();
        dto.metodo = metodo.getDescripcion();
        dto.puntoAcceso = puntoAcceso;
        dto.fechaHora = LocalDateTime.now();
        dto.usuarioId = usuario.getId();
        dto.accesoId = accesoId;
        return dto;
    }

    public static ResultadoAccesoDto denegado(String mensaje, String nombre, String matricula,
                                               String iniciales, MetodoAcceso metodo,
                                               String puntoAcceso, Long accesoId) {
        ResultadoAccesoDto dto = new ResultadoAccesoDto();
        dto.permitido = false;
        dto.requiereConfirmacion = false;
        dto.mensaje = mensaje;
        dto.nombre = nombre;
        dto.matricula = matricula;
        dto.iniciales = iniciales;
        dto.metodo = metodo.getDescripcion();
        dto.puntoAcceso = puntoAcceso;
        dto.fechaHora = LocalDateTime.now();
        dto.accesoId = accesoId;
        return dto;
    }

    public static ResultadoAccesoDto confirmacion(UsuarioInstitucional usuario, String puntoAcceso) {
        ResultadoAccesoDto dto = new ResultadoAccesoDto();
        dto.permitido = true;
        dto.requiereConfirmacion = true;
        dto.mensaje = "Verifica que la persona coincida con los datos mostrados antes de confirmar.";
        dto.nombre = usuario.getNombreCompleto();
        dto.matricula = usuario.getMatricula();
        dto.iniciales = usuario.getIniciales();
        dto.tipoUsuario = usuario.getTipo().getDescripcion();
        dto.estadoUsuario = usuario.getEstado().getDescripcion();
        dto.metodo = MetodoAcceso.MANUAL_MATRICULA.getDescripcion();
        dto.puntoAcceso = puntoAcceso;
        dto.fechaHora = LocalDateTime.now();
        dto.usuarioId = usuario.getId();
        return dto;
    }

    public boolean isPermitido() {
        return permitido;
    }

    public boolean isRequiereConfirmacion() {
        return requiereConfirmacion;
    }

    public String getMensaje() {
        return mensaje;
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

    public String getTipoUsuario() {
        return tipoUsuario;
    }

    public String getEstadoUsuario() {
        return estadoUsuario;
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

    public Long getUsuarioId() {
        return usuarioId;
    }

    public Long getAccesoId() {
        return accesoId;
    }
}
