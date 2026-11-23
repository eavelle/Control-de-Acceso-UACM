package mx.uacm.controlacceso.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class FormularioPassword {

    @NotBlank(message = "La contraseña actual es obligatoria.")
    private String actual;

    @NotBlank(message = "La nueva contraseña es obligatoria.")
    @Size(min = 8, max = 72, message = "La nueva contraseña debe tener entre 8 y 72 caracteres.")
    private String nueva;

    @NotBlank(message = "Confirma la nueva contraseña.")
    private String confirmar;

    public String getActual() {
        return actual;
    }

    public void setActual(String actual) {
        this.actual = actual;
    }

    public String getNueva() {
        return nueva;
    }

    public void setNueva(String nueva) {
        this.nueva = nueva;
    }

    public String getConfirmar() {
        return confirmar;
    }

    public void setConfirmar(String confirmar) {
        this.confirmar = confirmar;
    }
}
