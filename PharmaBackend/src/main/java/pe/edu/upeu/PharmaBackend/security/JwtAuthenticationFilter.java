package pe.edu.upeu.PharmaBackend.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

    public static final String ATRIBUTO_ERROR = "jwt.error";

    private static final Logger log =
            LoggerFactory.getLogger(JwtAuthenticationFilter.class);

    private static final String PREFIJO = "Bearer ";

    private final JwtService jwtService;
    private final UserDetailsService servicioUsuarios;

    public JwtAuthenticationFilter(
            JwtService jwtService,
            UserDetailsService servicioUsuarios) {

        this.jwtService = jwtService;
        this.servicioUsuarios = servicioUsuarios;
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest solicitud,
            HttpServletResponse respuesta,
            FilterChain cadena) throws ServletException, IOException {

        String encabezado = solicitud.getHeader(
                HttpHeaders.AUTHORIZATION);

        if (encabezado == null || !encabezado.startsWith(PREFIJO)) {
            cadena.doFilter(solicitud, respuesta);
            return;
        }

        String token = encabezado.substring(PREFIJO.length()).trim();

        try {
            String nombre = jwtService.extraerUsername(token);

            if (nombre != null
                    && SecurityContextHolder.getContext()
                            .getAuthentication() == null) {

                UserDetails usuario =
                        servicioUsuarios.loadUserByUsername(nombre);

                if (usuario.isEnabled()) {

                    UsernamePasswordAuthenticationToken autenticacion =
                            new UsernamePasswordAuthenticationToken(
                                    usuario,
                                    null,
                                    usuario.getAuthorities()
                            );

                    autenticacion.setDetails(
                            new WebAuthenticationDetailsSource()
                                    .buildDetails(solicitud)
                    );

                    SecurityContextHolder.getContext()
                            .setAuthentication(autenticacion);

                } else {
                    solicitud.setAttribute(
                            ATRIBUTO_ERROR,
                            "El usuario está deshabilitado"
                    );
                }
            }

        } catch (ExpiredJwtException error) {

            log.debug("Token expirado en {}",
                    solicitud.getRequestURI());

            solicitud.setAttribute(
                    ATRIBUTO_ERROR,
                    "El token ha expirado. Inicie sesión nuevamente"
            );

        } catch (UsernameNotFoundException error) {

            log.warn("Usuario del token no encontrado");

            solicitud.setAttribute(
                    ATRIBUTO_ERROR,
                    "El usuario del token no existe"
            );

        } catch (JwtException | IllegalArgumentException error) {

            log.warn("Token inválido en {}",
                    solicitud.getRequestURI());

            solicitud.setAttribute(
                    ATRIBUTO_ERROR,
                    "El token no es válido"
            );
        }

        cadena.doFilter(solicitud, respuesta);
    }
}
