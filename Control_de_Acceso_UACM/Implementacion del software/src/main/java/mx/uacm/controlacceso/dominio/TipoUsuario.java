package mx.uacm.controlacceso.dominio;

public enum TipoUsuario {
    ESTUDIANTE("Estudiante"),
    PROFESOR("Profesor"),
    ADMINISTRATIVO("Administrativo");

    private final String descripcion;

    TipoUsuario(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
