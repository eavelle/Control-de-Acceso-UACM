package mx.uacm.controlacceso.dominio;

public enum Rol {
    GUARDIA("Guardia de Seguridad"),
    PERSONAL_EMISOR("Personal Administrativo (emisor de tarjetón)"),
    DIRECCION_ENLACE("Dirección de Enlace");

    private final String descripcion;

    Rol(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
