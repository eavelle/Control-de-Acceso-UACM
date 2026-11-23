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
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDate;

@Entity
@Table(name = "credencial_institucional", uniqueConstraints = {
        @UniqueConstraint(name = "uk_credencial_codigo_qr", columnNames = "codigo_qr")
})
public class CredencialInstitucional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_institucional_id", nullable = false)
    private UsuarioInstitucional usuario;

    @Column(name = "codigo_qr", nullable = false, length = 80)
    private String codigoQr;

    @Column(name = "fecha_emision", nullable = false)
    private LocalDate fechaEmision;

    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private EstadoCredencial estado = EstadoCredencial.ACTIVA;

    public CredencialInstitucional() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public UsuarioInstitucional getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioInstitucional usuario) {
        this.usuario = usuario;
    }

    public String getCodigoQr() {
        return codigoQr;
    }

    public void setCodigoQr(String codigoQr) {
        this.codigoQr = codigoQr;
    }

    public LocalDate getFechaEmision() {
        return fechaEmision;
    }

    public void setFechaEmision(LocalDate fechaEmision) {
        this.fechaEmision = fechaEmision;
    }

    public LocalDate getFechaVencimiento() {
        return fechaVencimiento;
    }

    public void setFechaVencimiento(LocalDate fechaVencimiento) {
        this.fechaVencimiento = fechaVencimiento;
    }

    public EstadoCredencial getEstado() {
        return estado;
    }

    public void setEstado(EstadoCredencial estado) {
        this.estado = estado;
    }

    public boolean isVigente() {
        return estado == EstadoCredencial.ACTIVA
                && fechaVencimiento != null
                && !fechaVencimiento.isBefore(LocalDate.now());
    }
}
