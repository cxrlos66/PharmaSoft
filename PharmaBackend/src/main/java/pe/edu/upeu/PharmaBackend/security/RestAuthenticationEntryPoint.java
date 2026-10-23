package pe.edu.upeu.PharmaBackend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(
            HttpServletRequest solicitud,
            HttpServletResponse respuesta,
            AuthenticationException error) throws IOException {

        Object motivo = solicitud.getAttribute(
                JwtAuthenticationFilter.ATRIBUTO_ERROR
        );

        String mensaje = motivo != null
                ? motivo.toString()
                : "Debe iniciar sesión para acceder a este recurso";

        respuesta.setHeader(
                HttpHeaders.WWW_AUTHENTICATE,
                "Bearer"
        );

        RespuestaErrorSeguridad.escribir(
                solicitud,
                respuesta,
                HttpStatus.UNAUTHORIZED.value(),
                "Unauthorized",
                mensaje
        );
    }
}
