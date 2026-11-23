package mx.uacm.controlacceso.dominio;

public enum EstadoUsuarioInstitucional {
    ACTIVO("Activo"),
    INACTIVO("Inactivo"),
    PROHIBIDO("Con prohibición de acceso");

    private final String descripcion;

    EstadoUsuarioInstitucional(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
