package mx.uacm.controlacceso.repositorio;

import mx.uacm.controlacceso.dominio.AccesoInstitucional;
import mx.uacm.controlacceso.dominio.ResultadoAcceso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface AccesoInstitucionalRepository extends JpaRepository<AccesoInstitucional, Long> {

    List<AccesoInstitucional> findTop10ByOrderByFechaHoraDesc();

    List<AccesoInstitucional> findTop10ByGuardiaIdOrderByFechaHoraDesc(Long guardiaId);

    boolean existsByAccesoOrigenId(Long accesoOrigenId);

    long countByResultadoAndFechaHoraAfter(ResultadoAcceso resultado, LocalDateTime fechaHora);
}
