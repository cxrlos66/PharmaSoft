package pe.edu.upeu.PharmaBackend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {

    @Override
    public void handle(
            HttpServletRequest solicitud,
            HttpServletResponse respuesta,
            AccessDeniedException error) throws IOException {

        RespuestaErrorSeguridad.escribir(
                solicitud,
                respuesta,
                HttpStatus.FORBIDDEN.value(),
                "Forbidden",
                "No tiene permisos para acceder a este recurso"
        );
    }
}
