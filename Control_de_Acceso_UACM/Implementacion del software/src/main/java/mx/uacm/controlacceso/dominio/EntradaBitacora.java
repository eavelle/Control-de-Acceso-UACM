package mx.uacm.controlacceso.dominio;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

/**
 * Registro de auditoría de solo inserción. No se exponen setters para los datos
 * del evento: una vez creado, la bitácora no debe modificarse.
 */
@Entity
@Table(name = "entrada_bitacora")
public class EntradaBitacora {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "actor_id")
    private CuentaUsuario actor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_evento", nullable = false, length = 40)
    private TipoEvento tipoEvento;

    @Column(nullable = false, length = 500)
    private String descripcion;

    @Column(length = 60)
    private String entidad;

    @Column(name = "entidad_id")
    private Long entidadId;

    @Column(length = 30)
    private String resultado;

    @Column(length = 45)
    private String ip;

    protected EntradaBitacora() {
    }

    public EntradaBitacora(LocalDateTime fechaHora, CuentaUsuario actor, TipoEvento tipoEvento,
                           String descripcion, String entidad, Long entidadId,
                           String resultado, String ip) {
        this.fechaHora = fechaHora;
        this.actor = actor;
        this.tipoEvento = tipoEvento;
        this.descripcion = descripcion;
        this.entidad = entidad;
        this.entidadId = entidadId;
        this.resultado = resultado;
        this.ip = ip;
    }

    public Long getId() {
        return id;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public CuentaUsuario getActor() {
        return actor;
    }

    public TipoEvento getTipoEvento() {
        return tipoEvento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public String getEntidad() {
        return entidad;
    }

    public Long getEntidadId() {
        return entidadId;
    }

    public String getResultado() {
        return resultado;
    }

    public String getIp() {
        return ip;
    }
}
