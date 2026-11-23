package mx.uacm.controlacceso.servicio;

import mx.uacm.controlacceso.dominio.AccesoInstitucional;
import mx.uacm.controlacceso.dominio.CredencialInstitucional;
import mx.uacm.controlacceso.dominio.CuentaUsuario;
import mx.uacm.controlacceso.dominio.EstadoCredencial;
import mx.uacm.controlacceso.dominio.EstadoCuenta;
import mx.uacm.controlacceso.dominio.EstadoUsuarioInstitucional;
import mx.uacm.controlacceso.dominio.MetodoAcceso;
import mx.uacm.controlacceso.dominio.ResultadoAcceso;
import mx.uacm.controlacceso.dominio.Rol;
import mx.uacm.controlacceso.dominio.TipoEvento;
import mx.uacm.controlacceso.dominio.TipoUsuario;
import mx.uacm.controlacceso.dominio.UsuarioInstitucional;
import mx.uacm.controlacceso.repositorio.AccesoInstitucionalRepository;
import mx.uacm.controlacceso.repositorio.CredencialInstitucionalRepository;
import mx.uacm.controlacceso.repositorio.UsuarioInstitucionalRepository;
import mx.uacm.controlacceso.web.dto.AccesoCredencialDto;
import mx.uacm.controlacceso.web.dto.ResultadoAccesoDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AccesoServiceTest {

    @Mock
    private CredencialInstitucionalRepository credencialRepository;

    @Mock
    private UsuarioInstitucionalRepository usuarioRepository;

    @Mock
    private AccesoInstitucionalRepository accesoRepository;

    @Mock
    private BitacoraService bitacoraService;

    @InjectMocks
    private AccesoService accesoService;

    private CuentaUsuario guardia;

    @BeforeEach
    void setUp() {
        guardia = new CuentaUsuario();
        guardia.setId(10L);
        guardia.setUsername("guardia");
        guardia.setNombre("Guardia de Seguridad");
        guardia.setRol(Rol.GUARDIA);
        guardia.setEstado(EstadoCuenta.ACTIVA);
    }

    @Test
    void registrarAccesoPorCredencialPermitidoRegistraElAcceso() {
        UsuarioInstitucional usuario = crearUsuario(1L, "20260001", EstadoUsuarioInstitucional.ACTIVO);
        CredencialInstitucional credencial = crearCredencial(usuario, "UACM-20260001",
                LocalDate.now().plusYears(1), EstadoCredencial.ACTIVA);

        when(credencialRepository.findByCodigoQr("UACM-20260001")).thenReturn(Optional.of(credencial));
        when(accesoRepository.save(any(AccesoInstitucional.class))).thenAnswer(invocation -> {
            AccesoInstitucional acceso = invocation.getArgument(0);
            acceso.setId(100L);
            return acceso;
        });

        AccesoCredencialDto resultado = accesoService.registrarAccesoPorCredencial(
                "UACM-20260001", "Acceso principal", MetodoAcceso.QR, guardia, "127.0.0.1");

        assertThat(resultado.isEncontrada()).isTrue();
        assertThat(resultado.isPermitido()).isTrue();
        assertThat(resultado.isPuedeAutorizar()).isFalse();
        assertThat(resultado.isAutorizadoConAdvertencia()).isFalse();
        assertThat(resultado.getNombre()).isEqualTo("Ana López");
        assertThat(resultado.getMatricula()).isEqualTo("20260001");
        assertThat(resultado.getTipoUsuario()).isEqualTo("Estudiante");
        assertThat(resultado.getEstadoUsuario()).isEqualTo("Activo");
        assertThat(resultado.getEstadoCredencial()).isEqualTo("Activa");
        assertThat(resultado.getAccesoId()).isEqualTo(100L);
        verify(bitacoraService).registrar(eq(TipoEvento.ACCESO_PERMITIDO), anyString(), eq(guardia),
                eq("AccesoInstitucional"), eq(100L), eq("PERMITIDO"), eq("127.0.0.1"));
    }

    @Test
    void registrarAccesoPorCredencialVencidaDeniegaElAcceso() {
        UsuarioInstitucional usuario = crearUsuario(1L, "20260004", EstadoUsuarioInstitucional.ACTIVO);
        CredencialInstitucional credencial = crearCredencial(usuario, "UACM-20260004",
                LocalDate.now().minusDays(1), EstadoCredencial.ACTIVA);

        when(credencialRepository.findByCodigoQr("UACM-20260004")).thenReturn(Optional.of(credencial));
        when(accesoRepository.save(any(AccesoInstitucional.class))).thenAnswer(invocation -> {
            AccesoInstitucional acceso = invocation.getArgument(0);
            acceso.setId(101L);
            return acceso;
        });

        AccesoCredencialDto resultado = accesoService.registrarAccesoPorCredencial(
                "UACM-20260004", "Acceso principal", MetodoAcceso.QR, guardia, "127.0.0.1");

        assertThat(resultado.isPermitido()).isFalse();
        assertThat(resultado.isPuedeAutorizar()).isTrue();
        assertThat(resultado.getMensaje()).contains("vencida");
        assertThat(resultado.getEstadoCredencial()).isEqualTo("Vencida");
        verify(bitacoraService).registrar(eq(TipoEvento.ACCESO_DENEGADO), anyString(), eq(guardia),
                eq("AccesoInstitucional"), eq(101L), eq("DENEGADO"), eq("127.0.0.1"));
    }

    @Test
    void registrarAccesoPorCredencialDeUsuarioProhibidoDeniegaElAcceso() {
        UsuarioInstitucional usuario = crearUsuario(1L, "20260003", EstadoUsuarioInstitucional.PROHIBIDO);
        CredencialInstitucional credencial = crearCredencial(usuario, "UACM-20260003",
                LocalDate.now().plusMonths(6), EstadoCredencial.ACTIVA);

        when(credencialRepository.findByCodigoQr("UACM-20260003")).thenReturn(Optional.of(credencial));
        when(accesoRepository.save(any(AccesoInstitucional.class))).thenAnswer(invocation -> {
            AccesoInstitucional acceso = invocation.getArgument(0);
            acceso.setId(102L);
            return acceso;
        });

        AccesoCredencialDto resultado = accesoService.registrarAccesoPorCredencial(
                "UACM-20260003", "Acceso principal", MetodoAcceso.QR, guardia, "127.0.0.1");

        assertThat(resultado.isPermitido()).isFalse();
        assertThat(resultado.isPuedeAutorizar()).isTrue();
        assertThat(resultado.getEstadoUsuario()).isEqualTo("Con prohibición de acceso");
        assertThat(resultado.getMensaje()).contains("prohibición");
    }

    @Test
    void registrarAccesoPorCredencialBloqueadaDeniegaElAcceso() {
        UsuarioInstitucional usuario = crearUsuario(1L, "20260005", EstadoUsuarioInstitucional.ACTIVO);
        CredencialInstitucional credencial = crearCredencial(usuario, "UACM-20260005",
                LocalDate.now().plusMonths(6), EstadoCredencial.BLOQUEADA);

        when(credencialRepository.findByCodigoQr("UACM-20260005")).thenReturn(Optional.of(credencial));
        when(accesoRepository.save(any(AccesoInstitucional.class))).thenAnswer(invocation -> {
            AccesoInstitucional acceso = invocation.getArgument(0);
            acceso.setId(105L);
            return acceso;
        });

        AccesoCredencialDto resultado = accesoService.registrarAccesoPorCredencial(
                "UACM-20260005", "Acceso principal", MetodoAcceso.QR, guardia, "127.0.0.1");

        assertThat(resultado.isPermitido()).isFalse();
        assertThat(resultado.isPuedeAutorizar()).isTrue();
        assertThat(resultado.getEstadoCredencial()).isEqualTo("Bloqueada");
        assertThat(resultado.getMensaje()).contains("bloqueada");
    }

    @Test
    void registrarAccesoPorCredencialInexistenteDeniegaYGuardaElCodigoIngresado() {
        when(credencialRepository.findByCodigoQr("NO-EXISTE")).thenReturn(Optional.empty());
        when(accesoRepository.save(any(AccesoInstitucional.class))).thenAnswer(invocation -> {
            AccesoInstitucional acceso = invocation.getArgument(0);
            acceso.setId(103L);
            return acceso;
        });

        AccesoCredencialDto resultado = accesoService.registrarAccesoPorCredencial(
                "NO-EXISTE", "Acceso principal", MetodoAcceso.QR, guardia, "127.0.0.1");

        assertThat(resultado.isEncontrada()).isFalse();
        assertThat(resultado.isPermitido()).isFalse();
        assertThat(resultado.isPuedeAutorizar()).isFalse();
        assertThat(resultado.getMensaje()).contains("no existe");
        verify(accesoRepository).save(any(AccesoInstitucional.class));
        verify(bitacoraService).registrar(eq(TipoEvento.ACCESO_DENEGADO), anyString(), eq(guardia),
                eq("AccesoInstitucional"), eq(103L), eq("DENEGADO"), eq("127.0.0.1"));
    }

    @Test
    void autorizarConAdvertenciaRegistraAccesoPermitidoYBitacora() {
        UsuarioInstitucional usuario = crearUsuario(1L, "20260003", EstadoUsuarioInstitucional.PROHIBIDO);
        CredencialInstitucional credencial = crearCredencial(usuario, "UACM-20260003",
                LocalDate.now().plusMonths(6), EstadoCredencial.ACTIVA);
        AccesoInstitucional denegado = crearAccesoDenegado(102L, usuario, credencial,
                "El usuario tiene prohibición de acceso al plantel.");

        when(accesoRepository.findById(102L)).thenReturn(Optional.of(denegado));
        when(accesoRepository.existsByAccesoOrigenId(102L)).thenReturn(false);
        when(accesoRepository.save(any(AccesoInstitucional.class))).thenAnswer(invocation -> {
            AccesoInstitucional acceso = invocation.getArgument(0);
            acceso.setId(200L);
            return acceso;
        });

        AccesoCredencialDto resultado = accesoService.autorizarConAdvertencia(
                102L, guardia, "127.0.0.1");

        assertThat(resultado.isPermitido()).isTrue();
        assertThat(resultado.isAutorizadoConAdvertencia()).isTrue();
        assertThat(resultado.isPuedeAutorizar()).isFalse();
        assertThat(resultado.getMotivo()).contains("prohibición");
        assertThat(resultado.getAccesoId()).isEqualTo(200L);

        ArgumentCaptor<AccesoInstitucional> captor = ArgumentCaptor.forClass(AccesoInstitucional.class);
        verify(accesoRepository).save(captor.capture());
        AccesoInstitucional guardado = captor.getValue();
        assertThat(guardado.getResultado()).isEqualTo(ResultadoAcceso.PERMITIDO);
        assertThat(guardado.getAccesoOrigenId()).isEqualTo(102L);
        assertThat(guardado.getMotivo()).contains("advertencia").contains("prohibición");

        verify(bitacoraService).registrar(eq(TipoEvento.ACCESO_PERMITIDO), contains("advertencia"),
                eq(guardia), eq("AccesoInstitucional"), eq(200L), eq("PERMITIDO"), eq("127.0.0.1"));
    }

    @Test
    void autorizarConAdvertenciaRechazaUnAccesoQueNoFueDenegado() {
        UsuarioInstitucional usuario = crearUsuario(1L, "20260001", EstadoUsuarioInstitucional.ACTIVO);
        CredencialInstitucional credencial = crearCredencial(usuario, "UACM-20260001",
                LocalDate.now().plusYears(1), EstadoCredencial.ACTIVA);
        AccesoInstitucional permitido = crearAccesoDenegado(103L, usuario, credencial, null);
        permitido.setResultado(ResultadoAcceso.PERMITIDO);

        when(accesoRepository.findById(103L)).thenReturn(Optional.of(permitido));

        assertThatThrownBy(() -> accesoService.autorizarConAdvertencia(103L, guardia, "127.0.0.1"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("denegado");
        verify(accesoRepository, never()).save(any(AccesoInstitucional.class));
    }

    @Test
    void autorizarConAdvertenciaRechazaUnaCredencialNoReconocida() {
        AccesoInstitucional denegado = new AccesoInstitucional();
        denegado.setId(104L);
        denegado.setResultado(ResultadoAcceso.DENEGADO);
        denegado.setUsuario(null);
        denegado.setCredencial(null);

        when(accesoRepository.findById(104L)).thenReturn(Optional.of(denegado));

        assertThatThrownBy(() -> accesoService.autorizarConAdvertencia(104L, guardia, "127.0.0.1"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("no reconocida");
        verify(accesoRepository, never()).save(any(AccesoInstitucional.class));
    }

    @Test
    void autorizarConAdvertenciaRechazaUnaAutorizacionDuplicada() {
        UsuarioInstitucional usuario = crearUsuario(1L, "20260003", EstadoUsuarioInstitucional.PROHIBIDO);
        CredencialInstitucional credencial = crearCredencial(usuario, "UACM-20260003",
                LocalDate.now().plusMonths(6), EstadoCredencial.ACTIVA);
        AccesoInstitucional denegado = crearAccesoDenegado(102L, usuario, credencial,
                "El usuario tiene prohibición de acceso al plantel.");

        when(accesoRepository.findById(102L)).thenReturn(Optional.of(denegado));
        when(accesoRepository.existsByAccesoOrigenId(102L)).thenReturn(true);

        assertThatThrownBy(() -> accesoService.autorizarConAdvertencia(102L, guardia, "127.0.0.1"))
                .isInstanceOf(ReglaNegocioException.class)
                .hasMessageContaining("ya fue autorizado");
        verify(accesoRepository, never()).save(any(AccesoInstitucional.class));
    }

    @Test
    void validarManualValidoSolicitaConfirmacionSinRegistrarAcceso() {
        UsuarioInstitucional usuario = crearUsuario(8L, "20260008", EstadoUsuarioInstitucional.ACTIVO);
        when(usuarioRepository.findByMatriculaIgnoreCase("20260008")).thenReturn(Optional.of(usuario));

        ResultadoAccesoDto resultado = accesoService.validarManual(
                "20260008", "Acceso principal", guardia, "127.0.0.1");

        assertThat(resultado.isPermitido()).isTrue();
        assertThat(resultado.isRequiereConfirmacion()).isTrue();
        assertThat(resultado.getNombre()).isEqualTo("Ana López");
        verify(accesoRepository, never()).save(any(AccesoInstitucional.class));
    }

    @Test
    void confirmarManualRegistraAccesoYBitacora() {
        UsuarioInstitucional usuario = crearUsuario(8L, "20260008", EstadoUsuarioInstitucional.ACTIVO);
        when(usuarioRepository.findById(8L)).thenReturn(Optional.of(usuario));
        when(credencialRepository.findFirstByUsuarioIdOrderByFechaVencimientoDesc(8L))
                .thenReturn(Optional.empty());
        when(accesoRepository.save(any(AccesoInstitucional.class))).thenAnswer(invocation -> {
            AccesoInstitucional acceso = invocation.getArgument(0);
            acceso.setId(104L);
            return acceso;
        });

        ResultadoAccesoDto resultado = accesoService.confirmarManual(
                8L, "Acceso principal", guardia, "127.0.0.1");

        assertThat(resultado.isPermitido()).isTrue();
        assertThat(resultado.getMetodo()).isEqualTo(MetodoAcceso.MANUAL_MATRICULA.getDescripcion());
        verify(bitacoraService).registrar(eq(TipoEvento.VALIDACION_MANUAL), anyString(), eq(guardia),
                eq("AccesoInstitucional"), eq(104L), eq("PERMITIDO"), eq("127.0.0.1"));
        verify(bitacoraService).registrar(eq(TipoEvento.ACCESO_PERMITIDO), anyString(), eq(guardia),
                eq("AccesoInstitucional"), eq(104L), eq("PERMITIDO"), eq("127.0.0.1"));
    }

    private UsuarioInstitucional crearUsuario(Long id, String matricula, EstadoUsuarioInstitucional estado) {
        UsuarioInstitucional usuario = new UsuarioInstitucional();
        usuario.setId(id);
        usuario.setMatricula(matricula);
        usuario.setNombre("Ana");
        usuario.setApellidos("López");
        usuario.setTipo(TipoUsuario.ESTUDIANTE);
        usuario.setEstado(estado);
        return usuario;
    }

    private CredencialInstitucional crearCredencial(UsuarioInstitucional usuario, String codigo,
                                                    LocalDate vencimiento, EstadoCredencial estado) {
        CredencialInstitucional credencial = new CredencialInstitucional();
        credencial.setId(1L);
        credencial.setUsuario(usuario);
        credencial.setCodigoQr(codigo);
        credencial.setFechaEmision(LocalDate.now().minusMonths(1));
        credencial.setFechaVencimiento(vencimiento);
        credencial.setEstado(estado);
        return credencial;
    }

    private AccesoInstitucional crearAccesoDenegado(Long id, UsuarioInstitucional usuario,
                                                    CredencialInstitucional credencial, String motivo) {
        AccesoInstitucional acceso = new AccesoInstitucional();
        acceso.setId(id);
        acceso.setUsuario(usuario);
        acceso.setCredencial(credencial);
        acceso.setCodigoIngresado(credencial.getCodigoQr());
        acceso.setFechaHora(LocalDateTime.now());
        acceso.setMetodo(MetodoAcceso.QR);
        acceso.setResultado(ResultadoAcceso.DENEGADO);
        acceso.setMotivo(motivo);
        acceso.setPuntoAcceso("Acceso principal");
        acceso.setGuardia(guardia);
        return acceso;
    }
}
