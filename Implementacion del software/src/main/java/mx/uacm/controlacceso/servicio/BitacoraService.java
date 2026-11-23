package mx.uacm.controlacceso.servicio;

import jakarta.persistence.criteria.Predicate;
import mx.uacm.controlacceso.dominio.CuentaUsuario;
import mx.uacm.controlacceso.dominio.EntradaBitacora;
import mx.uacm.controlacceso.dominio.TipoEvento;
import mx.uacm.controlacceso.repositorio.EntradaBitacoraRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class BitacoraService {

    private final EntradaBitacoraRepository repositorio;

    public BitacoraService(EntradaBitacoraRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Transactional
    public void registrar(TipoEvento tipoEvento, String descripcion, CuentaUsuario actor,
                          String entidad, Long entidadId, String resultado, String ip) {
        EntradaBitacora entrada = new EntradaBitacora(
                LocalDateTime.now(),
                actor,
                tipoEvento,
                descripcion,
                entidad,
                entidadId,
                resultado,
                ip
        );
        repositorio.save(entrada);
    }

    @Transactional
    public void registrar(TipoEvento tipoEvento, String descripcion, CuentaUsuario actor) {
        registrar(tipoEvento, descripcion, actor, null, null, null, null);
    }

    @Transactional(readOnly = true)
    public Page<EntradaBitacora> buscar(LocalDateTime desde, LocalDateTime hasta,
                                        TipoEvento tipo, String texto, int page) {
        Specification<EntradaBitacora> especificacion = (root, query, cb) -> {
            List<Predicate> predicados = new ArrayList<>();
            if (desde != null) {
                predicados.add(cb.greaterThanOrEqualTo(root.get("fechaHora"), desde));
            }
            if (hasta != null) {
                predicados.add(cb.lessThanOrEqualTo(root.get("fechaHora"), hasta));
            }
            if (tipo != null) {
                predicados.add(cb.equal(root.get("tipoEvento"), tipo));
            }
            if (texto != null && !texto.isBlank()) {
                predicados.add(cb.like(cb.lower(root.get("descripcion")),
                        "%" + texto.trim().toLowerCase() + "%"));
            }
            return cb.and(predicados.toArray(new Predicate[0]));
        };

        PageRequest pageable = PageRequest.of(Math.max(page, 0), 20,
                Sort.by(Sort.Direction.DESC, "fechaHora"));
        return repositorio.findAll(especificacion, pageable);
    }

    @Transactional(readOnly = true)
    public List<EntradaBitacora> ultimos(int limite) {
        return repositorio.findAll(PageRequest.of(0, Math.max(limite, 1),
                Sort.by(Sort.Direction.DESC, "fechaHora"))).getContent();
    }
}
