package mx.uacm.controlacceso.seguridad;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.io.IOException;

/**
 * Obliga a cambiar la contraseña temporal antes de usar cualquier módulo.
 */
@Component
public class PasswordChangeInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
            throws IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !(authentication.getPrincipal() instanceof UsuarioPrincipal principal)) {
            return true;
        }
        if (!principal.isRequiereCambioPassword()) {
            return true;
        }

        String path = request.getRequestURI().substring(request.getContextPath().length());
        boolean permitido = path.startsWith("/cuenta/password")
                || path.startsWith("/logout")
                || path.startsWith("/css/")
                || path.startsWith("/js/")
                || path.startsWith("/img/")
                || path.equals("/error")
                || path.equals("/favicon.ico");

        if (permitido) {
            return true;
        }

        response.sendRedirect(request.getContextPath() + "/cuenta/password?primerIngreso");
        return false;
    }
}
