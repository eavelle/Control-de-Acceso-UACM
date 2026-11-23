package mx.uacm.controlacceso.config;

import mx.uacm.controlacceso.dominio.CredencialInstitucional;
import mx.uacm.controlacceso.dominio.CuentaUsuario;
import mx.uacm.controlacceso.dominio.EstadoCredencial;
import mx.uacm.controlacceso.dominio.EstadoCuenta;
import mx.uacm.controlacceso.dominio.EstadoUsuarioInstitucional;
import mx.uacm.controlacceso.dominio.MetodoAcceso;
import mx.uacm.controlacceso.dominio.Rol;
import mx.uacm.controlacceso.dominio.TipoUsuario;
import mx.uacm.controlacceso.dominio.UsuarioInstitucional;
import mx.uacm.controlacceso.repositorio.CredencialInstitucionalRepository;
import mx.uacm.controlacceso.repositorio.CuentaUsuarioRepository;
import mx.uacm.controlacceso.repositorio.UsuarioInstitucionalRepository;
import mx.uacm.controlacceso.servicio.AccesoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Carga datos de demostración la primera vez que arranca la aplicación.
 * En producción debe deshabilitarse con APP_SEED=false.
 */
@Component
@ConditionalOnProperty(name = "app.seed.enabled", havingValue = "true", matchIfMissing = true)
public class DataInitializer implements CommandLineRunner {

    private static final Logger log = LoggerFactory.getLogger(DataInitializer.class);

    private final CuentaUsuarioRepository cuentaRepository;
    private final UsuarioInstitucionalRepository usuarioRepository;
    private final CredencialInstitucionalRepository credencialRepository;
    private final PasswordEncoder passwordEncoder;
    private final AccesoService accesoService;

    public DataInitializer(CuentaUsuarioRepository cuentaRepository,
                           UsuarioInstitucionalRepository usuarioRepository,
                           CredencialInstitucionalRepository credencialRepository,
                           PasswordEncoder passwordEncoder,
                           AccesoService accesoService) {
        this.cuentaRepository = cuentaRepository;
        this.usuarioRepository = usuarioRepository;
        this.credencialRepository = credencialRepository;
        this.passwordEncoder = passwordEncoder;
        this.accesoService = accesoService;
    }

    @Override
    @Transactional
    public void run(String... args) {
        if (cuentaRepository.count() > 0) {
            log.info("Los datos de demostración ya existen; no se vuelven a cargar.");
            return;
        }

        CuentaUsuario direccion = crearCuenta("direccion", "Direccion123!",
                "Dirección de Enlace", "direccion@uacm.edu.mx", Rol.DIRECCION_ENLACE);
        CuentaUsuario guardia = crearCuenta("guardia", "Guardia123!",
                "Guardia de Seguridad", "guardia@uacm.edu.mx", Rol.GUARDIA);
        crearCuenta("emisor", "Emisor123!",
                "Personal Emisor de Tarjetones", "emisor@uacm.edu.mx", Rol.PERSONAL_EMISOR);

        UsuarioInstitucional ana = crearUsuario("20260001", "Ana", "López García",
                "ana.lopez@uacm.edu.mx", TipoUsuario.ESTUDIANTE, EstadoUsuarioInstitucional.ACTIVO);
        ana.setFotografiaUrl("/img/UACM-20260001.jpg");
        usuarioRepository.save(ana);
        crearCredencial(ana, "UACM-20260001", LocalDate.now().minusMonths(3),
                LocalDate.now().plusYears(1), EstadoCredencial.ACTIVA);

        UsuarioInstitucional carlos = crearUsuario("20260002", "Carlos", "Pérez Ruiz",
                "carlos.perez@uacm.edu.mx", TipoUsuario.ESTUDIANTE, EstadoUsuarioInstitucional.ACTIVO);
        carlos.setFotografiaUrl("/img/UACM-20260002.jpg");
        usuarioRepository.save(carlos);
        crearCredencial(carlos, "UACM-20260002", LocalDate.now().minusMonths(2),
                LocalDate.now().plusYears(1), EstadoCredencial.ACTIVA);

        UsuarioInstitucional luis = crearUsuario("20260003", "Luis", "Hernández Soto",
                "luis.hernandez@uacm.edu.mx", TipoUsuario.ESTUDIANTE, EstadoUsuarioInstitucional.PROHIBIDO);
        luis.setFotografiaUrl("/img/UACM-20260003.jpg");
        usuarioRepository.save(luis);
        crearCredencial(luis, "UACM-20260003", LocalDate.now().minusMonths(4),
                LocalDate.now().plusMonths(8), EstadoCredencial.ACTIVA);

        UsuarioInstitucional sofia = crearUsuario("20260004", "Sofía", "Torres Vega",
                "sofia.torres@uacm.edu.mx", TipoUsuario.ESTUDIANTE, EstadoUsuarioInstitucional.ACTIVO);
        sofia.setFotografiaUrl("/img/UACM-20260004.png");
        usuarioRepository.save(sofia);
        crearCredencial(sofia, "UACM-20260004", LocalDate.now().minusYears(2),
                LocalDate.now().minusDays(10), EstadoCredencial.ACTIVA);

        UsuarioInstitucional pedro = crearUsuario("20260005", "Pedro", "Ramírez Luna",
                "pedro.ramirez@uacm.edu.mx", TipoUsuario.ESTUDIANTE, EstadoUsuarioInstitucional.ACTIVO);
        pedro.setFotografiaUrl("/img/UACM-20260005.jpg");
        usuarioRepository.save(pedro);
        crearCredencial(pedro, "UACM-20260005", LocalDate.now().minusMonths(6),
                LocalDate.now().plusMonths(6), EstadoCredencial.BLOQUEADA);

        UsuarioInstitucional maria = crearUsuario("DOC-001", "María", "Sánchez Díaz",
                "maria.sanchez@uacm.edu.mx", TipoUsuario.PROFESOR, EstadoUsuarioInstitucional.ACTIVO);
        maria.setFotografiaUrl("/img/UACM-DOC-001.jpg");
        usuarioRepository.save(maria);
        crearCredencial(maria, "UACM-DOC-001", LocalDate.now().minusMonths(5),
                LocalDate.now().plusYears(2), EstadoCredencial.ACTIVA);

        UsuarioInstitucional jose = crearUsuario("ADM-001", "José", "Ramírez Cruz",
                "jose.ramirez@uacm.edu.mx", TipoUsuario.ADMINISTRATIVO, EstadoUsuarioInstitucional.ACTIVO);
        jose.setFotografiaUrl("/img/UACM-ADM-001.jpg");
        usuarioRepository.save(jose);
        crearCredencial(jose, "UACM-ADM-001", LocalDate.now().minusMonths(1),
                LocalDate.now().plusYears(1), EstadoCredencial.ACTIVA);

        UsuarioInstitucional diego = crearUsuario("20260008", "Diego", "Flores Nava",
                "diego.flores@uacm.edu.mx", TipoUsuario.ESTUDIANTE, EstadoUsuarioInstitucional.ACTIVO);
        // Diego no tiene credencial ni fotografía: sirve para probar la validación manual por matrícula.

        accesoService.registrarAccesoPorCredencial("UACM-20260001", "Acceso principal",
                MetodoAcceso.QR, guardia, "127.0.0.1");
        accesoService.registrarAccesoPorCredencial("UACM-20260002", "Acceso principal",
                MetodoAcceso.CODIGO_BARRAS, guardia, "127.0.0.1");
        accesoService.registrarAccesoPorCredencial("UACM-20260003", "Acceso principal",
                MetodoAcceso.QR, guardia, "127.0.0.1");
        accesoService.registrarAccesoPorCredencial("UACM-20260004", "Acceso principal",
                MetodoAcceso.QR, guardia, "127.0.0.1");
        accesoService.registrarAccesoPorCredencial("UACM-20260005", "Acceso principal",
                MetodoAcceso.QR, guardia, "127.0.0.1");
        accesoService.confirmarManual(diego.getId(), "Acceso principal", guardia, "127.0.0.1");

        log.info("Datos de demostración cargados. Usuarios: direccion, guardia y emisor.");
        log.info("Credenciales de prueba: UACM-20260001, UACM-20260002, UACM-20260003, UACM-20260004, UACM-20260005.");
    }

    private CuentaUsuario crearCuenta(String username, String password, String nombre,
                                      String correo, Rol rol) {
        CuentaUsuario cuenta = new CuentaUsuario();
        cuenta.setUsername(username);
        cuenta.setPasswordHash(passwordEncoder.encode(password));
        cuenta.setNombre(nombre);
        cuenta.setCorreo(correo);
        cuenta.setRol(rol);
        cuenta.setEstado(EstadoCuenta.ACTIVA);
        cuenta.setIntentosFallidos(0);
        cuenta.setRequiereCambioPassword(false);
        cuenta.setFechaCreacion(LocalDateTime.now());
        return cuentaRepository.save(cuenta);
    }

    private UsuarioInstitucional crearUsuario(String matricula, String nombre, String apellidos,
                                              String correo, TipoUsuario tipo,
                                              EstadoUsuarioInstitucional estado) {
        UsuarioInstitucional usuario = new UsuarioInstitucional();
        usuario.setMatricula(matricula);
        usuario.setNombre(nombre);
        usuario.setApellidos(apellidos);
        usuario.setCorreo(correo);
        usuario.setTipo(tipo);
        usuario.setEstado(estado);
        usuario.setFechaRegistro(LocalDateTime.now());
        return usuarioRepository.save(usuario);
    }

    private CredencialInstitucional crearCredencial(UsuarioInstitucional usuario, String codigo,
                                                    LocalDate emision, LocalDate vencimiento,
                                                    EstadoCredencial estado) {
        CredencialInstitucional credencial = new CredencialInstitucional();
        credencial.setUsuario(usuario);
        credencial.setCodigoQr(codigo);
        credencial.setFechaEmision(emision);
        credencial.setFechaVencimiento(vencimiento);
        credencial.setEstado(estado);
        return credencialRepository.save(credencial);
    }
}
