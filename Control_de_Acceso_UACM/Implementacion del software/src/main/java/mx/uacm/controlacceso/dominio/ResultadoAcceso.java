package mx.uacm.controlacceso.dominio;

public enum ResultadoAcceso {
    PERMITIDO("Permitido"),
    DENEGADO("Denegado");

    private final String descripcion;

    ResultadoAcceso(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
