package mx.uacm.controlacceso.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mx.uacm.controlacceso.dominio.Rol;

public class FormularioCuenta {

    @NotBlank(message = "El usuario institucional es obligatorio.")
    @Size(max = 60, message = "El usuario no puede exceder 60 caracteres.")
    private String username;

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 120, message = "El nombre no puede exceder 120 caracteres.")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "El correo no tiene un formato válido.")
    @Size(max = 120, message = "El correo no puede exceder 120 caracteres.")
    private String correo;

    @NotNull(message = "Selecciona un rol.")
    private Rol rol;

    @NotBlank(message = "La contraseña temporal es obligatoria.")
    @Size(min = 8, max = 72, message = "La contraseña debe tener entre 8 y 72 caracteres.")
    private String password;

    @NotBlank(message = "Confirma la contraseña temporal.")
    private String confirmarPassword;

    public FormularioCuenta() {
        this.rol = Rol.GUARDIA;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getConfirmarPassword() {
        return confirmarPassword;
    }

    public void setConfirmarPassword(String confirmarPassword) {
        this.confirmarPassword = confirmarPassword;
    }
}
