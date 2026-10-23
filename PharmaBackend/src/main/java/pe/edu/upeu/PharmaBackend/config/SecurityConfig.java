
package pe.edu.upeu.PharmaBackend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.beans.factory.annotation.Qualifier;
import pe.edu.upeu.PharmaBackend.security.JwtAuthenticationFilter;
import pe.edu.upeu.PharmaBackend.security.JwtService;
import pe.edu.upeu.PharmaBackend.security.RestAccessDeniedHandler;
import pe.edu.upeu.PharmaBackend.security.RestAuthenticationEntryPoint;

import static pe.edu.upeu.PharmaBackend.enums.Permiso.*;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private static final String[] RUTAS_PUBLICAS = {
            "/api/v1/auth/login",
            "/api/health",
            "/swagger-ui.html",
            "/swagger-ui/**",
            "/v3/api-docs/**",
            "/error"
    };

    @Bean
    public SecurityFilterChain filterChain(
            HttpSecurity http,
            JwtService jwtService,
            UserDetailsService usuarios,
            @Qualifier("corsConfigurationSource") CorsConfigurationSource cors,
            RestAuthenticationEntryPoint noAutenticado,
            RestAccessDeniedHandler sinPermiso) throws Exception {

        JwtAuthenticationFilter filtro =
                new JwtAuthenticationFilter(jwtService, usuarios);

        http
            .csrf(AbstractHttpConfigurer::disable)
            .logout(AbstractHttpConfigurer::disable)
            .cors(config -> config.configurationSource(cors))
            .sessionManagement(sesion ->
                    sesion.sessionCreationPolicy(
                            SessionCreationPolicy.STATELESS
                    )
            )
            .authorizeHttpRequests(reglas -> reglas

                .requestMatchers(RUTAS_PUBLICAS).permitAll()

                .requestMatchers(HttpMethod.OPTIONS, "/**")
                .permitAll()

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/categorias/**",
                        "/api/v1/productos/**"
                ).hasAuthority(CATALOGO_LEER.name())

                .requestMatchers(
                        "/api/v1/categorias/**",
                        "/api/v1/productos/**"
                ).hasAuthority(CATALOGO_ESCRIBIR.name())

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/clientes/**"
                ).hasAuthority(CLIENTES_LEER.name())

                .requestMatchers(
                        "/api/v1/clientes/**"
                ).hasAuthority(CLIENTES_ESCRIBIR.name())

                .requestMatchers(
                        HttpMethod.POST,
                        "/api/v1/ventas"
                ).hasAuthority(VENTAS_REGISTRAR.name())

                .requestMatchers(
                        HttpMethod.GET,
                        "/api/v1/ventas/**"
                ).hasAuthority(VENTAS_LEER.name())

                .requestMatchers(
                        "/api/v1/ventas/**"
                ).hasAuthority(VENTAS_REGISTRAR.name())

                .requestMatchers(
                        "/api/v1/usuarios/**",
                        "/api/v1/empleados/**",
                        "/api/v1/roles/**"
                ).hasAuthority(USUARIOS_ADMINISTRAR.name())

                .anyRequest().authenticated()
            )
            .exceptionHandling(errores -> errores
                    .authenticationEntryPoint(noAutenticado)
                    .accessDeniedHandler(sinPermiso)
            )
            .addFilterBefore(
                    filtro,
                    UsernamePasswordAuthenticationFilter.class
            );

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuracion) throws Exception {

        return configuracion.getAuthenticationManager();
    }
}
