package pe.edu.upeu.PharmaBackend.exception.dto;
import java.time.LocalDateTime;
import java.util.Map;
public record ErrorResponseDTO(LocalDateTime timestamp, int status, String error,
 String mensaje, String path, Map<String,String> validationErrors) {}
