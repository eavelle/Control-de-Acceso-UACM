package mx.uacm.controlacceso.seguridad;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.uacm.controlacceso.servicio.CuentaService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final CuentaService cuentaService;

    public CustomAuthenticationSuccessHandler(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        UsuarioPrincipal principal = (UsuarioPrincipal) authentication.getPrincipal();
        cuentaService.registrarInicioSesion(principal.getId(), request.getRemoteAddr());

        if (principal.isRequiereCambioPassword()) {
            response.sendRedirect(request.getContextPath() + "/cuenta/password?primerIngreso");
        } else {
            response.sendRedirect(request.getContextPath() + "/inicio");
        }
    }
}
