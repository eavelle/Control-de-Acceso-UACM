package mx.uacm.controlacceso.web;

import mx.uacm.controlacceso.servicio.ReglaNegocioException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ReglaNegocioException.class)
    public String manejarReglaNegocio(ReglaNegocioException ex, Model model) {
        model.addAttribute("mensajeError", ex.getMessage());
        return "error";
    }
}
