package mx.uacm.controlacceso.web;

import mx.uacm.controlacceso.dominio.ResultadoAcceso;
import mx.uacm.controlacceso.dominio.Rol;
import mx.uacm.controlacceso.seguridad.UsuarioPrincipal;
import mx.uacm.controlacceso.servicio.AccesoService;
import mx.uacm.controlacceso.servicio.CuentaService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    private final AccesoService accesoService;
    private final CuentaService cuentaService;

    public InicioController(AccesoService accesoService, CuentaService cuentaService) {
        this.accesoService = accesoService;
        this.cuentaService = cuentaService;
    }

    @GetMapping("/")
    public String raiz() {
        return "redirect:/inicio";
    }

    @GetMapping("/inicio")
    public String inicio(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        boolean esDireccion = principal.getRol() == Rol.DIRECCION_ENLACE;
        boolean esGuardia = principal.getRol() == Rol.GUARDIA;

        model.addAttribute("esDireccion", esDireccion);
        model.addAttribute("esGuardia", esGuardia);
        model.addAttribute("esEmisor", principal.getRol() == Rol.PERSONAL_EMISOR);
        model.addAttribute("accesosRecientes", accesoService.listarRecientes(principal.getId(), esDireccion));
        model.addAttribute("permitidosHoy", accesoService.contarHoy(ResultadoAcceso.PERMITIDO));
        model.addAttribute("denegadosHoy", accesoService.contarHoy(ResultadoAcceso.DENEGADO));
        model.addAttribute("cuentasBloqueadas", cuentaService.contarPorEstado(mx.uacm.controlacceso.dominio.EstadoCuenta.BLOQUEADA));
        return "inicio";
    }

    @GetMapping("/acceso-denegado")
    public String accesoDenegado() {
        return "acceso-denegado";
    }
}
