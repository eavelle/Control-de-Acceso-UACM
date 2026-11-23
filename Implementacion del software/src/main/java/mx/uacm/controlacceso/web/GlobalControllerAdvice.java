package mx.uacm.controlacceso.web;

import mx.uacm.controlacceso.dominio.MetodoAcceso;
import mx.uacm.controlacceso.dominio.Rol;
import mx.uacm.controlacceso.dominio.TipoEvento;
import mx.uacm.controlacceso.seguridad.UsuarioPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.Arrays;
import java.util.List;

@ControllerAdvice
public class GlobalControllerAdvice {

    @ModelAttribute("principal")
    public UsuarioPrincipal principal() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UsuarioPrincipal principal) {
            return principal;
        }
        return null;
    }

    @ModelAttribute("roles")
    public Rol[] roles() {
        return Rol.values();
    }

    @ModelAttribute("tiposEvento")
    public TipoEvento[] tiposEvento() {
        // CONSULTA_CREDENCIAL se conserva por compatibilidad con registros históricos,
        // pero el flujo actual ya no lo genera: no se ofrece como filtro.
        return Arrays.stream(TipoEvento.values())
                .filter(tipo -> tipo != TipoEvento.CONSULTA_CREDENCIAL)
                .toArray(TipoEvento[]::new);
    }

    @ModelAttribute("metodosAcceso")
    public MetodoAcceso[] metodosAcceso() {
        return MetodoAcceso.values();
    }

    @ModelAttribute("puntosAcceso")
    public List<String> puntosAcceso() {
        return List.of("Acceso principal", "Caseta vehicular", "Entrada posterior");
    }
}
