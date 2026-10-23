package pe.edu.upeu.PharmaBackend.security;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.MediaType;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;

public final class RespuestaErrorSeguridad {

    private RespuestaErrorSeguridad() {
    }

    public static void escribir(
            HttpServletRequest solicitud,
            HttpServletResponse respuesta,
            int estado,
            String error,
            String mensaje) throws IOException {

        String json = "{"
                + "\"timestamp\":\"" + LocalDateTime.now() + "\","
                + "\"status\":" + estado + ","
                + "\"error\":\"" + escapar(error) + "\","
                + "\"message\":\"" + escapar(mensaje) + "\","
                + "\"path\":\"" + escapar(solicitud.getRequestURI()) + "\","
                + "\"validationErrors\":null"
                + "}";

        respuesta.setStatus(estado);
        respuesta.setCharacterEncoding(StandardCharsets.UTF_8.name());
        respuesta.setContentType(MediaType.APPLICATION_JSON_VALUE);
        respuesta.getWriter().write(json);
    }

    private static String escapar(String texto) {
        if (texto == null) {
            return "";
        }

        return texto.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }
}
