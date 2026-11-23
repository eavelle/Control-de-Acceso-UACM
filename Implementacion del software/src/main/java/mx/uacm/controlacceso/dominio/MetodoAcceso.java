package mx.uacm.controlacceso.dominio;

public enum MetodoAcceso {
    QR("QR"),
    CODIGO_BARRAS("Código de barras"),
    MANUAL_MATRICULA("Manual por matrícula");

    private final String descripcion;

    MetodoAcceso(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
