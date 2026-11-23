package mx.uacm.controlacceso.dominio;

public enum EstadoCuenta {
    ACTIVA("Activa"),
    BLOQUEADA("Bloqueada"),
    DESACTIVADA("Desactivada");

    private final String descripcion;

    EstadoCuenta(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
