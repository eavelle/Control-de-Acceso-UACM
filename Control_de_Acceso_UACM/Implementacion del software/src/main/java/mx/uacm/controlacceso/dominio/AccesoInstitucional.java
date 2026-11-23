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

@Entity
@Table(name = "acceso_institucional")
public class AccesoInstitucional {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "credencial_id")
    private CredencialInstitucional credencial;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "usuario_institucional_id")
    private UsuarioInstitucional usuario;

    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "guardia_id", nullable = false)
    private CuentaUsuario guardia;

    @Column(name = "codigo_ingresado", length = 80)
    private String codigoIngresado;

    @Column(name = "fecha_hora", nullable = false)
    private LocalDateTime fechaHora;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private MetodoAcceso metodo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private ResultadoAcceso resultado;

    @Column(length = 250)
    private String motivo;

    @Column(name = "punto_acceso", length = 80)
    private String puntoAcceso;

    @Column(name = "acceso_origen_id")
    private Long accesoOrigenId;

    public AccesoInstitucional() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public CredencialInstitucional getCredencial() {
        return credencial;
    }

    public void setCredencial(CredencialInstitucional credencial) {
        this.credencial = credencial;
    }

    public UsuarioInstitucional getUsuario() {
        return usuario;
    }

    public void setUsuario(UsuarioInstitucional usuario) {
        this.usuario = usuario;
    }

    public CuentaUsuario getGuardia() {
        return guardia;
    }

    public void setGuardia(CuentaUsuario guardia) {
        this.guardia = guardia;
    }

    public String getCodigoIngresado() {
        return codigoIngresado;
    }

    public void setCodigoIngresado(String codigoIngresado) {
        this.codigoIngresado = codigoIngresado;
    }

    public LocalDateTime getFechaHora() {
        return fechaHora;
    }

    public void setFechaHora(LocalDateTime fechaHora) {
        this.fechaHora = fechaHora;
    }

    public MetodoAcceso getMetodo() {
        return metodo;
    }

    public void setMetodo(MetodoAcceso metodo) {
        this.metodo = metodo;
    }

    public ResultadoAcceso getResultado() {
        return resultado;
    }

    public void setResultado(ResultadoAcceso resultado) {
        this.resultado = resultado;
    }

    public String getMotivo() {
        return motivo;
    }

    public void setMotivo(String motivo) {
        this.motivo = motivo;
    }

    public String getPuntoAcceso() {
        return puntoAcceso;
    }

    public void setPuntoAcceso(String puntoAcceso) {
        this.puntoAcceso = puntoAcceso;
    }

    public Long getAccesoOrigenId() {
        return accesoOrigenId;
    }

    public void setAccesoOrigenId(Long accesoOrigenId) {
        this.accesoOrigenId = accesoOrigenId;
    }
}
