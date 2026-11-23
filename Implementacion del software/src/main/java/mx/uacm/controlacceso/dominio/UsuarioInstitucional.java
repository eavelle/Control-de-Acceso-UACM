package mx.uacm.controlacceso.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(name = "usuario_institucional", uniqueConstraints = {
        @UniqueConstraint(name = "uk_usuario_institucional_matricula", columnNames = "matricula")
})
public class UsuarioInstitucional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 30)
    private String matricula;

    @Column(nullable = false, length = 80)
    private String nombre;

    @Column(nullable = false, length = 80)
    private String apellidos;

    @Column(length = 120)
    private String correo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private TipoUsuario tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoUsuarioInstitucional estado = EstadoUsuarioInstitucional.ACTIVO;

    @Column(name = "fotografia_url", length = 300)
    private String fotografiaUrl;

    @Column(name = "fecha_registro", nullable = false)
    private LocalDateTime fechaRegistro;

    public UsuarioInstitucional() {
    }

    @PrePersist
    void prePersist() {
        if (fechaRegistro == null) {
            fechaRegistro = LocalDateTime.now();
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public TipoUsuario getTipo() {
        return tipo;
    }

    public void setTipo(TipoUsuario tipo) {
        this.tipo = tipo;
    }

    public EstadoUsuarioInstitucional getEstado() {
        return estado;
    }

    public void setEstado(EstadoUsuarioInstitucional estado) {
        this.estado = estado;
    }

    public String getFotografiaUrl() {
        return fotografiaUrl;
    }

    public void setFotografiaUrl(String fotografiaUrl) {
        this.fotografiaUrl = fotografiaUrl;
    }

    public LocalDateTime getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDateTime fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public String getNombreCompleto() {
        return nombre + " " + apellidos;
    }

    public String getIniciales() {
        String inicialNombre = (nombre == null || nombre.isBlank()) ? "" : nombre.substring(0, 1);
        String inicialApellido = (apellidos == null || apellidos.isBlank()) ? "" : apellidos.substring(0, 1);
        return (inicialNombre + inicialApellido).toUpperCase();
    }
}
