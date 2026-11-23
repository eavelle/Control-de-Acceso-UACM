package mx.uacm.controlacceso.web;

import mx.uacm.controlacceso.dominio.EntradaBitacora;
import mx.uacm.controlacceso.dominio.TipoEvento;
import mx.uacm.controlacceso.servicio.BitacoraService;
import org.springframework.data.domain.Page;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Controller
@RequestMapping("/bitacora")
@PreAuthorize("hasAnyRole('DIRECCION_ENLACE','GUARDIA')")
public class BitacoraController {

    private final BitacoraService bitacoraService;

    public BitacoraController(BitacoraService bitacoraService) {
        this.bitacoraService = bitacoraService;
    }

    @GetMapping
    public String lista(@RequestParam(required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate desde,
                        @RequestParam(required = false)
                        @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate hasta,
                        @RequestParam(required = false) TipoEvento tipo,
                        @RequestParam(required = false) String texto,
                        @RequestParam(defaultValue = "0") int page,
                        Model model) {
        LocalDateTime desdeFechaHora = desde != null ? desde.atStartOfDay() : null;
        LocalDateTime hastaFechaHora = hasta != null ? hasta.atTime(LocalTime.MAX) : null;

        Page<EntradaBitacora> entradas = bitacoraService.buscar(
                desdeFechaHora, hastaFechaHora, tipo, texto, page);

        model.addAttribute("entradas", entradas);
        model.addAttribute("desde", desde);
        model.addAttribute("hasta", hasta);
        model.addAttribute("tipoSeleccionado", tipo);
        model.addAttribute("texto", texto);
        return "bitacora/lista";
    }
}
