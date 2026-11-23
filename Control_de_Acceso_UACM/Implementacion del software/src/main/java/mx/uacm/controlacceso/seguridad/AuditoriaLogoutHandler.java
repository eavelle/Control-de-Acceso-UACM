package mx.uacm.controlacceso.seguridad;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.uacm.controlacceso.servicio.CuentaService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.stereotype.Component;

@Component
public class AuditoriaLogoutHandler implements LogoutHandler {

    private final CuentaService cuentaService;

    public AuditoriaLogoutHandler(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @Override
    public void logout(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        if (authentication != null && authentication.getPrincipal() instanceof UsuarioPrincipal principal) {
            cuentaService.registrarCierreSesion(principal.getId(), request.getRemoteAddr());
        }
    }
}
