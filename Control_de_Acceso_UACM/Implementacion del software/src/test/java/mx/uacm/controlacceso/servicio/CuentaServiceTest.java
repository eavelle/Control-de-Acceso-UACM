package mx.uacm.controlacceso.servicio;

import mx.uacm.controlacceso.dominio.CuentaUsuario;
import mx.uacm.controlacceso.dominio.EstadoCuenta;
import mx.uacm.controlacceso.dominio.Rol;
import mx.uacm.controlacceso.dominio.TipoEvento;
import mx.uacm.controlacceso.repositorio.CuentaUsuarioRepository;
import mx.uacm.controlacceso.web.dto.FormularioCuenta;
import mx.uacm.controlacceso.web.dto.FormularioPassword;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CuentaServiceTest {

    @Mock
    private CuentaUsuarioRepository repositorio;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private BitacoraService bitacoraService;

    private CuentaService cuentaService;

    @BeforeEach
    void setUp() {
        cuentaService = new CuentaService(repositorio, passwordEncoder, bitacoraService, 5);
    }

    @Test
    void bloqueaLaCuentaAlQuintoIntentoFallido() {
        CuentaUsuario cuenta = crearCuenta(1L, "guardia", EstadoCuenta.ACTIVA, 4);
        when(repositorio.findByUsernameIgnoreCase("guardia")).thenReturn(Optional.of(cuenta));
        when(repositorio.save(any(CuentaUsuario.class))).thenReturn(cuenta);

        cuentaService.registrarIntentoFallido("guardia", "127.0.0.1");

        assertThat(cuenta.getIntentosFallidos()).isEqualTo(5);
        assertThat(cuenta.getEstado()).isEqualTo(EstadoCuenta.BLOQUEADA);
        verify(bitacoraService).registrar(eq(TipoEvento.CUENTA_BLOQUEADA), anyString(), eq(cuenta),
                eq("CuentaUsuario"), eq(1L), eq("BLOQUEADA"), eq("127.0.0.1"));
    }

    @Test
    void noBloqueaAntesDelQuintoIntento() {
        CuentaUsuario cuenta = crearCuenta(1L, "guardia", EstadoCuenta.ACTIVA, 2);
        when(repositorio.findByUsernameIgnoreCase("guardia")).thenReturn(Optional.of(cuenta));
        when(repositorio.save(any(CuentaUsuario.class))).thenReturn(cuenta);

        cuentaService.registrarIntentoFallido("guardia", "127.0.0.1");

        assertThat(cuenta.getIntentosFallidos()).isEqualTo(3);
        assertThat(cuenta.getEstado()).isEqualTo(EstadoCuenta.ACTIVA);
        verify(bitacoraService).registrar(eq(TipoEvento.INTENTO_FALLIDO), anyString(), eq(cuenta),
                eq("CuentaUsuario"), eq(1L), eq("FALLIDO"), eq("127.0.0.1"));
    }

    @Test
    void noPermiteDesactivarLaPropiaCuenta() {
        CuentaUsuario actor = crearCuenta(1L, "direccion", EstadoCuenta.ACTIVA, 0);
        actor.setRol(Rol.DIRECCION_ENLACE);

        assertThatThrownBy(() -> cuentaService.desactivar(1L, actor, "127.0.0.1"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("propia cuenta");
    }

    @Test
    void noPermiteCambiarLaContrasenaConLaActualIncorrecta() {
        CuentaUsuario cuenta = crearCuenta(1L, "guardia", EstadoCuenta.ACTIVA, 0);
        cuenta.setPasswordHash("hashActual");
        when(repositorio.findById(1L)).thenReturn(Optional.of(cuenta));
        when(passwordEncoder.matches("incorrecta", "hashActual")).thenReturn(false);

        FormularioPassword formulario = new FormularioPassword();
        formulario.setActual("incorrecta");
        formulario.setNueva("Nueva123!");
        formulario.setConfirmar("Nueva123!");

        assertThatThrownBy(() -> cuentaService.cambiarPassword(1L, formulario, "127.0.0.1"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("actual no es correcta");
        verify(repositorio, never()).save(any(CuentaUsuario.class));
    }

    @Test
    void cambiaLaContrasenaCorrectamente() {
        CuentaUsuario cuenta = crearCuenta(1L, "guardia", EstadoCuenta.ACTIVA, 0);
        cuenta.setPasswordHash("hashActual");
        cuenta.setRequiereCambioPassword(true);
        when(repositorio.findById(1L)).thenReturn(Optional.of(cuenta));
        when(passwordEncoder.matches("Actual123!", "hashActual")).thenReturn(true);
        when(passwordEncoder.encode("Nueva123!")).thenReturn("hashNuevo");
        when(repositorio.save(any(CuentaUsuario.class))).thenReturn(cuenta);

        FormularioPassword formulario = new FormularioPassword();
        formulario.setActual("Actual123!");
        formulario.setNueva("Nueva123!");
        formulario.setConfirmar("Nueva123!");

        cuentaService.cambiarPassword(1L, formulario, "127.0.0.1");

        assertThat(cuenta.getPasswordHash()).isEqualTo("hashNuevo");
        assertThat(cuenta.isRequiereCambioPassword()).isFalse();
        verify(bitacoraService).registrar(eq(TipoEvento.CAMBIO_PASSWORD), anyString(), eq(cuenta),
                eq("CuentaUsuario"), eq(1L), eq("EXITOSO"), eq("127.0.0.1"));
    }

    @Test
    void noCreaCuentasDuplicadas() {
        FormularioCuenta formulario = new FormularioCuenta();
        formulario.setUsername("guardia");
        formulario.setNombre("Guardia");
        formulario.setCorreo("guardia@uacm.edu.mx");
        formulario.setPassword("Guardia123!");
        formulario.setConfirmarPassword("Guardia123!");

        when(repositorio.existsByUsernameIgnoreCase("guardia")).thenReturn(true);

        assertThatThrownBy(() -> cuentaService.crear(formulario, crearCuenta(99L, "direccion",
                EstadoCuenta.ACTIVA, 0), "127.0.0.1"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("Ya existe");
    }

    private CuentaUsuario crearCuenta(Long id, String username, EstadoCuenta estado, int intentos) {
        CuentaUsuario cuenta = new CuentaUsuario();
        cuenta.setId(id);
        cuenta.setUsername(username);
        cuenta.setNombre("Usuario de prueba");
        cuenta.setCorreo(username + "@uacm.edu.mx");
        cuenta.setRol(Rol.GUARDIA);
        cuenta.setEstado(estado);
        cuenta.setIntentosFallidos(intentos);
        cuenta.setPasswordHash("hash");
        return cuenta;
    }
}
