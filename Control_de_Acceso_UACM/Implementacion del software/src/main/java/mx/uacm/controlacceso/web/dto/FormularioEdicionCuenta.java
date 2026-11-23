package mx.uacm.controlacceso.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import mx.uacm.controlacceso.dominio.CuentaUsuario;
import mx.uacm.controlacceso.dominio.Rol;

public class FormularioEdicionCuenta {

    @NotBlank(message = "El nombre es obligatorio.")
    @Size(max = 120, message = "El nombre no puede exceder 120 caracteres.")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio.")
    @Email(message = "El correo no tiene un formato válido.")
    @Size(max = 120, message = "El correo no puede exceder 120 caracteres.")
    private String correo;

    @NotNull(message = "Selecciona un rol.")
    private Rol rol;

    public FormularioEdicionCuenta() {
    }

    public FormularioEdicionCuenta(CuentaUsuario cuenta) {
        this.nombre = cuenta.getNombre();
        this.correo = cuenta.getCorreo();
        this.rol = cuenta.getRol();
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
}
