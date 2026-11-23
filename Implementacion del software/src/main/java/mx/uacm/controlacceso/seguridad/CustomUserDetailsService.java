package mx.uacm.controlacceso.seguridad;

import mx.uacm.controlacceso.dominio.CuentaUsuario;
import mx.uacm.controlacceso.repositorio.CuentaUsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final CuentaUsuarioRepository repositorio;

    public CustomUserDetailsService(CuentaUsuarioRepository repositorio) {
        this.repositorio = repositorio;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        CuentaUsuario cuenta = repositorio.findByUsernameIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado."));
        return new UsuarioPrincipal(cuenta);
    }
}
