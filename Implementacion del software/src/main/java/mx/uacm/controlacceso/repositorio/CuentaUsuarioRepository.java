package mx.uacm.controlacceso.repositorio;

import mx.uacm.controlacceso.dominio.CuentaUsuario;
import mx.uacm.controlacceso.dominio.EstadoCuenta;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CuentaUsuarioRepository extends JpaRepository<CuentaUsuario, Long> {

    Optional<CuentaUsuario> findByUsernameIgnoreCase(String username);

    boolean existsByUsernameIgnoreCase(String username);

    boolean existsByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCaseAndIdNot(String correo, Long id);

    List<CuentaUsuario> findAllByOrderByNombreAsc();

    long countByEstado(EstadoCuenta estado);
}
