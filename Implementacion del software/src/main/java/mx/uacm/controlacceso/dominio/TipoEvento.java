package mx.uacm.controlacceso.dominio;

public enum TipoEvento {
    INICIO_SESION("Inicio de sesión"),
    CIERRE_SESION("Cierre de sesión"),
    INTENTO_FALLIDO("Intento de sesión fallido"),
    CUENTA_BLOQUEADA("Cuenta bloqueada"),
    CUENTA_DESBLOQUEADA("Cuenta desbloqueada"),
    CUENTA_CREADA("Cuenta creada"),
    CUENTA_MODIFICADA("Cuenta modificada"),
    CUENTA_DESACTIVADA("Cuenta desactivada"),
    CUENTA_REACTIVADA("Cuenta reactivada"),
    CAMBIO_PASSWORD("Cambio de contraseña"),
    ACCESO_PERMITIDO("Acceso permitido"),
    ACCESO_DENEGADO("Acceso denegado"),
    VALIDACION_MANUAL("Validación manual"),
    CONSULTA_CREDENCIAL("Consulta de credencial");

    private final String descripcion;

    TipoEvento(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
