package mx.uacm.controlacceso.seguridad;

import mx.uacm.controlacceso.dominio.CuentaUsuario;
import mx.uacm.controlacceso.dominio.EstadoCuenta;
import mx.uacm.controlacceso.dominio.Rol;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UsuarioPrincipal implements UserDetails {

    private final Long id;
    private final String username;
    private final String password;
    private final String nombre;
    private final Rol rol;
    private final EstadoCuenta estado;
    private final boolean requiereCambioPassword;

    public UsuarioPrincipal(CuentaUsuario cuenta) {
        this.id = cuenta.getId();
        this.username = cuenta.getUsername();
        this.password = cuenta.getPasswordHash();
        this.nombre = cuenta.getNombre();
        this.rol = cuenta.getRol();
        this.estado = cuenta.getEstado();
        this.requiereCambioPassword = cuenta.isRequiereCambioPassword();
    }

    public Long getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public Rol getRol() {
        return rol;
    }

    public EstadoCuenta getEstado() {
        return estado;
    }

    public boolean isRequiereCambioPassword() {
        return requiereCambioPassword;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + rol.name()));
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return estado != EstadoCuenta.BLOQUEADA;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return estado == EstadoCuenta.ACTIVA;
    }
}
