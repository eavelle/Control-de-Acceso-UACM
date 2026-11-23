package mx.uacm.controlacceso.config;

import mx.uacm.controlacceso.seguridad.AuditoriaLogoutHandler;
import mx.uacm.controlacceso.seguridad.CustomAuthenticationFailureHandler;
import mx.uacm.controlacceso.seguridad.CustomAuthenticationSuccessHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http,
                                                   CustomAuthenticationSuccessHandler successHandler,
                                                   CustomAuthenticationFailureHandler failureHandler,
                                                   AuditoriaLogoutHandler logoutHandler) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/css/**", "/js/**", "/img/**", "/login", "/error", "/favicon.ico").permitAll()
                        .requestMatchers("/admin/**").hasRole("DIRECCION_ENLACE")
                        .requestMatchers("/guardia/**").hasRole("GUARDIA")
                        .requestMatchers("/bitacora/**").hasAnyRole("DIRECCION_ENLACE", "GUARDIA")
                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler)
                        .failureHandler(failureHandler)
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .addLogoutHandler(logoutHandler)
                        .logoutSuccessUrl("/login?logout")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .sessionManagement(session -> session
                        .sessionFixation().migrateSession())
                .exceptionHandling(ex -> ex.accessDeniedPage("/acceso-denegado"));

        return http.build();
    }
}
