package mx.uacm.controlacceso.seguridad;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import mx.uacm.controlacceso.servicio.CuentaService;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomAuthenticationFailureHandler implements AuthenticationFailureHandler {

    private final CuentaService cuentaService;

    public CustomAuthenticationFailureHandler(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response,
                                        AuthenticationException exception) throws IOException, ServletException {
        String username = request.getParameter("username");
        cuentaService.registrarIntentoFallido(username, request.getRemoteAddr());
        response.sendRedirect(request.getContextPath() + "/login?error");
    }
}
