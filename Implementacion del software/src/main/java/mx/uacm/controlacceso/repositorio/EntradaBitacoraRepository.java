package mx.uacm.controlacceso.repositorio;

import mx.uacm.controlacceso.dominio.EntradaBitacora;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface EntradaBitacoraRepository extends JpaRepository<EntradaBitacora, Long>,
        JpaSpecificationExecutor<EntradaBitacora> {
}
