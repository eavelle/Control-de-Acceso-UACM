package mx.uacm.controlacceso.repositorio;

import mx.uacm.controlacceso.dominio.UsuarioInstitucional;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UsuarioInstitucionalRepository extends JpaRepository<UsuarioInstitucional, Long> {

    Optional<UsuarioInstitucional> findByMatriculaIgnoreCase(String matricula);

    boolean existsByMatriculaIgnoreCase(String matricula);

    List<UsuarioInstitucional> findAllByOrderByApellidosAscNombreAsc();
}
