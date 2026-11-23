package mx.uacm.controlacceso.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mx.uacm.controlacceso.seguridad.UsuarioPrincipal;
import mx.uacm.controlacceso.servicio.CuentaService;
import mx.uacm.controlacceso.servicio.ReglaNegocioException;
import mx.uacm.controlacceso.web.dto.FormularioPassword;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/cuenta")
public class CuentaController {

    private final CuentaService cuentaService;

    public CuentaController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @GetMapping
    public String cuenta() {
        return "redirect:/cuenta/perfil";
    }

    @GetMapping("/perfil")
    public String perfil(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        model.addAttribute("cuenta", cuentaService.obtenerPorId(principal.getId()));
        return "cuenta/perfil";
    }

    @GetMapping("/password")
    public String password(@RequestParam(required = false) String primerIngreso, Model model) {
        model.addAttribute("formulario", new FormularioPassword());
        model.addAttribute("primerIngreso", primerIngreso != null);
        return "cuenta/password";
    }

    @PostMapping("/password")
    public String cambiarPassword(@Valid @ModelAttribute("formulario") FormularioPassword formulario,
                                  BindingResult bindingResult,
                                  @AuthenticationPrincipal UsuarioPrincipal principal,
                                  HttpServletRequest request,
                                  Model model) {
        if (formulario.getNueva() != null && formulario.getConfirmar() != null
                && !formulario.getNueva().equals(formulario.getConfirmar())) {
            bindingResult.rejectValue("confirmar", "password.noCoincide",
                    "La nueva contraseña y su confirmación no coinciden.");
        }
        if (bindingResult.hasErrors()) {
            return "cuenta/password";
        }

        try {
            cuentaService.cambiarPassword(principal.getId(), formulario, request.getRemoteAddr());
        } catch (ReglaNegocioException ex) {
            bindingResult.reject("negocio", ex.getMessage());
            return "cuenta/password";
        }

        SecurityContextHolder.clearContext();
        request.getSession().invalidate();
        return "redirect:/login?passwordCambiada";
    }
}
