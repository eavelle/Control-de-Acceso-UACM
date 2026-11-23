package mx.uacm.controlacceso.web;

import jakarta.servlet.http.HttpServletRequest;
import mx.uacm.controlacceso.dominio.MetodoAcceso;
import mx.uacm.controlacceso.seguridad.UsuarioPrincipal;
import mx.uacm.controlacceso.servicio.AccesoService;
import mx.uacm.controlacceso.servicio.CuentaService;
import mx.uacm.controlacceso.servicio.ReglaNegocioException;
import mx.uacm.controlacceso.web.dto.AccesoCredencialDto;
import mx.uacm.controlacceso.web.dto.ResultadoAccesoDto;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/guardia")
@PreAuthorize("hasRole('GUARDIA')")
public class GuardiaController {

    private final AccesoService accesoService;
    private final CuentaService cuentaService;

    public GuardiaController(AccesoService accesoService, CuentaService cuentaService) {
        this.accesoService = accesoService;
        this.cuentaService = cuentaService;
    }

    @GetMapping
    public String index(@AuthenticationPrincipal UsuarioPrincipal principal, Model model) {
        model.addAttribute("accesosRecientes",
                accesoService.listarRecientes(principal.getId(), false));
        return "guardia/index";
    }

    @PostMapping("/acceso")
    public String registrarAcceso(@RequestParam String codigo,
                                  @RequestParam(defaultValue = "Acceso principal") String puntoAcceso,
                                  @AuthenticationPrincipal UsuarioPrincipal principal,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) {
        AccesoCredencialDto resultado = accesoService.registrarAccesoPorCredencial(
                codigo,
                puntoAcceso,
                MetodoAcceso.QR,
                cuentaService.obtenerPorId(principal.getId()),
                request.getRemoteAddr());
        redirectAttributes.addFlashAttribute("accesoCredencial", resultado);
        return "redirect:/guardia";
    }

    @PostMapping("/acceso/autorizar")
    public String autorizarConAdvertencia(@RequestParam Long accesoId,
                                          @AuthenticationPrincipal UsuarioPrincipal principal,
                                          HttpServletRequest request,
                                          RedirectAttributes redirectAttributes) {
        try {
            AccesoCredencialDto resultado = accesoService.autorizarConAdvertencia(
                    accesoId,
                    cuentaService.obtenerPorId(principal.getId()),
                    request.getRemoteAddr());
            redirectAttributes.addFlashAttribute("accesoCredencial", resultado);
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/guardia";
    }

    @PostMapping("/acceso/manual")
    public String validarManual(@RequestParam String matricula,
                                @RequestParam(defaultValue = "Acceso principal") String puntoAcceso,
                                @AuthenticationPrincipal UsuarioPrincipal principal,
                                HttpServletRequest request,
                                Model model,
                                RedirectAttributes redirectAttributes) {
        ResultadoAccesoDto resultado = accesoService.validarManual(
                matricula, puntoAcceso,
                cuentaService.obtenerPorId(principal.getId()),
                request.getRemoteAddr());

        if (resultado.isPermitido() && resultado.isRequiereConfirmacion()) {
            model.addAttribute("confirmacion", resultado);
            return "guardia/confirmar-manual";
        }

        redirectAttributes.addFlashAttribute("resultado", resultado);
        return "redirect:/guardia";
    }

    @PostMapping("/acceso/manual/confirmar")
    public String confirmarManual(@RequestParam Long usuarioId,
                                  @RequestParam(defaultValue = "Acceso principal") String puntoAcceso,
                                  @AuthenticationPrincipal UsuarioPrincipal principal,
                                  HttpServletRequest request,
                                  RedirectAttributes redirectAttributes) {
        ResultadoAccesoDto resultado = accesoService.confirmarManual(
                usuarioId, puntoAcceso,
                cuentaService.obtenerPorId(principal.getId()),
                request.getRemoteAddr());
        redirectAttributes.addFlashAttribute("resultado", resultado);
        return "redirect:/guardia";
    }
}
