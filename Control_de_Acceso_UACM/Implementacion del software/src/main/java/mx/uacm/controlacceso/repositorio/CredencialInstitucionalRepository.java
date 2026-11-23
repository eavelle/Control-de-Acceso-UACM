package mx.uacm.controlacceso.repositorio;

import mx.uacm.controlacceso.dominio.CredencialInstitucional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CredencialInstitucionalRepository extends JpaRepository<CredencialInstitucional, Long> {

    Optional<CredencialInstitucional> findByCodigoQr(String codigoQr);

    Optional<CredencialInstitucional> findFirstByUsuarioIdOrderByFechaVencimientoDesc(Long usuarioId);

    boolean existsByCodigoQr(String codigoQr);
}
