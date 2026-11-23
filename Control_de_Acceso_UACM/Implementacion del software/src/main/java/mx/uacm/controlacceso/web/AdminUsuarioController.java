package mx.uacm.controlacceso.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import mx.uacm.controlacceso.dominio.CuentaUsuario;
import mx.uacm.controlacceso.dominio.EstadoCuenta;
import mx.uacm.controlacceso.seguridad.UsuarioPrincipal;
import mx.uacm.controlacceso.servicio.CuentaService;
import mx.uacm.controlacceso.servicio.ReglaNegocioException;
import mx.uacm.controlacceso.web.dto.FormularioCuenta;
import mx.uacm.controlacceso.web.dto.FormularioEdicionCuenta;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/admin")
@PreAuthorize("hasRole('DIRECCION_ENLACE')")
public class AdminUsuarioController {

    private final CuentaService cuentaService;

    public AdminUsuarioController(CuentaService cuentaService) {
        this.cuentaService = cuentaService;
    }

    @GetMapping
    public String index(Model model) {
        model.addAttribute("totalCuentas", cuentaService.listar().size());
        model.addAttribute("cuentasActivas", cuentaService.contarPorEstado(EstadoCuenta.ACTIVA));
        model.addAttribute("cuentasBloqueadas", cuentaService.contarPorEstado(EstadoCuenta.BLOQUEADA));
        model.addAttribute("cuentasDesactivadas", cuentaService.contarPorEstado(EstadoCuenta.DESACTIVADA));
        return "admin/index";
    }

    @GetMapping("/usuarios")
    public String lista(Model model) {
        model.addAttribute("cuentas", cuentaService.listar());
        return "admin/usuarios/lista";
    }

    @GetMapping("/usuarios/nuevo")
    public String nuevo(Model model) {
        model.addAttribute("formulario", new FormularioCuenta());
        model.addAttribute("edicion", false);
        return "admin/usuarios/formulario";
    }

    @PostMapping("/usuarios/nuevo")
    public String crear(@Valid @ModelAttribute("formulario") FormularioCuenta formulario,
                        BindingResult bindingResult,
                        @AuthenticationPrincipal UsuarioPrincipal principal,
                        HttpServletRequest request,
                        Model model,
                        RedirectAttributes redirectAttributes) {
        if (formulario.getPassword() != null && formulario.getConfirmarPassword() != null
                && !formulario.getPassword().equals(formulario.getConfirmarPassword())) {
            bindingResult.rejectValue("confirmarPassword", "password.noCoincide",
                    "Las contraseñas temporales no coinciden.");
        }
        if (bindingResult.hasErrors()) {
            model.addAttribute("edicion", false);
            return "admin/usuarios/formulario";
        }

        try {
            cuentaService.crear(formulario, cuentaService.obtenerPorId(principal.getId()),
                    request.getRemoteAddr());
            redirectAttributes.addFlashAttribute("mensajeExito", "Cuenta creada correctamente.");
            return "redirect:/admin/usuarios";
        } catch (ReglaNegocioException ex) {
            bindingResult.reject("negocio", ex.getMessage());
            model.addAttribute("edicion", false);
            return "admin/usuarios/formulario";
        }
    }

    @GetMapping("/usuarios/{id}/editar")
    public String editar(@PathVariable Long id, Model model) {
        CuentaUsuario cuenta = cuentaService.obtenerPorId(id);
        model.addAttribute("formulario", new FormularioEdicionCuenta(cuenta));
        model.addAttribute("cuenta", cuenta);
        model.addAttribute("edicion", true);
        return "admin/usuarios/formulario";
    }

    @PostMapping("/usuarios/{id}/editar")
    public String actualizar(@PathVariable Long id,
                             @Valid @ModelAttribute("formulario") FormularioEdicionCuenta formulario,
                             BindingResult bindingResult,
                             @AuthenticationPrincipal UsuarioPrincipal principal,
                             HttpServletRequest request,
                             Model model,
                             RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("cuenta", cuentaService.obtenerPorId(id));
            model.addAttribute("edicion", true);
            return "admin/usuarios/formulario";
        }

        try {
            cuentaService.actualizar(id, formulario,
                    cuentaService.obtenerPorId(principal.getId()), request.getRemoteAddr());
            redirectAttributes.addFlashAttribute("mensajeExito", "Cuenta actualizada correctamente.");
            return "redirect:/admin/usuarios";
        } catch (ReglaNegocioException ex) {
            bindingResult.reject("negocio", ex.getMessage());
            model.addAttribute("cuenta", cuentaService.obtenerPorId(id));
            model.addAttribute("edicion", true);
            return "admin/usuarios/formulario";
        }
    }

    @PostMapping("/usuarios/{id}/desactivar")
    public String desactivar(@PathVariable Long id,
                             @AuthenticationPrincipal UsuarioPrincipal principal,
                             HttpServletRequest request,
                             RedirectAttributes redirectAttributes) {
        try {
            cuentaService.desactivar(id, cuentaService.obtenerPorId(principal.getId()),
                    request.getRemoteAddr());
            redirectAttributes.addFlashAttribute("mensajeExito", "Cuenta desactivada.");
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/desbloquear")
    public String desbloquear(@PathVariable Long id,
                              @AuthenticationPrincipal UsuarioPrincipal principal,
                              HttpServletRequest request,
                              RedirectAttributes redirectAttributes) {
        try {
            cuentaService.desbloquear(id, cuentaService.obtenerPorId(principal.getId()),
                    request.getRemoteAddr());
            redirectAttributes.addFlashAttribute("mensajeExito",
                    "Cuenta desbloqueada. El usuario deberá cambiar su contraseña.");
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/admin/usuarios";
    }

    @PostMapping("/usuarios/{id}/reactivar")
    public String reactivar(@PathVariable Long id,
                            @AuthenticationPrincipal UsuarioPrincipal principal,
                            HttpServletRequest request,
                            RedirectAttributes redirectAttributes) {
        try {
            cuentaService.reactivar(id, cuentaService.obtenerPorId(principal.getId()),
                    request.getRemoteAddr());
            redirectAttributes.addFlashAttribute("mensajeExito", "Cuenta reactivada.");
        } catch (ReglaNegocioException ex) {
            redirectAttributes.addFlashAttribute("mensajeError", ex.getMessage());
        }
        return "redirect:/admin/usuarios";
    }
}
