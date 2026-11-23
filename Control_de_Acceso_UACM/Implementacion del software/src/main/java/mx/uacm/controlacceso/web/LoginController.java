package mx.uacm.controlacceso.web;

import mx.uacm.controlacceso.seguridad.UsuarioPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LoginController {

    @GetMapping("/login")
    public String login(@AuthenticationPrincipal UsuarioPrincipal principal) {
        if (principal != null) {
            return "redirect:/inicio";
        }
        return "login";
    }
}
