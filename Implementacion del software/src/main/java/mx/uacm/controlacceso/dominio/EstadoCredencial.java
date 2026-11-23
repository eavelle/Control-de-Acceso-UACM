package mx.uacm.controlacceso.dominio;

public enum EstadoCredencial {
    ACTIVA("Activa"),
    VENCIDA("Vencida"),
    BLOQUEADA("Bloqueada");

    private final String descripcion;

    EstadoCredencial(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
