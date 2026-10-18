
package pe.edu.upeu.PharmaBackend.exception;

import pe.edu.upeu.PharmaBackend.exception.dto.ErrorResponseDTO;

import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private ResponseEntity<ErrorResponseDTO> respuesta(
            HttpStatus estado,
            String mensaje,
            HttpServletRequest solicitud,
            Map<String, String> validaciones) {

        return ResponseEntity.status(estado).body(
                new ErrorResponseDTO(
                        LocalDateTime.now(),
                        estado.value(),
                        estado.getReasonPhrase(),
                        mensaje,
                        solicitud.getRequestURI(),
                        validaciones
                )
        );
    }

    @ExceptionHandler(RecursoNoEncontradoException.class)
    public ResponseEntity<ErrorResponseDTO> noEncontrado(
            RecursoNoEncontradoException error,
            HttpServletRequest solicitud) {

        return respuesta(
                HttpStatus.NOT_FOUND,
                error.getMessage(),
                solicitud,
                Map.of()
        );
    }

    @ExceptionHandler(ReglaNegocioException.class)
    public ResponseEntity<ErrorResponseDTO> conflicto(
            ReglaNegocioException error,
            HttpServletRequest solicitud) {

        log.warn("Regla incumplida: {}", error.getMessage());

        return respuesta(
                HttpStatus.CONFLICT,
                error.getMessage(),
                solicitud,
                Map.of()
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDTO> validacion(
            MethodArgumentNotValidException error,
            HttpServletRequest solicitud) {

        Map<String, String> errores = new LinkedHashMap<>();

        error.getBindingResult()
                .getFieldErrors()
                .forEach(campo -> errores.putIfAbsent(
                        campo.getField(),
                        campo.getDefaultMessage()
                ));

        return respuesta(
                HttpStatus.BAD_REQUEST,
                "Datos no válidos",
                solicitud,
                errores
        );
    }

    @ExceptionHandler({
            MethodArgumentTypeMismatchException.class,
            HttpMessageNotReadableException.class,
            IllegalArgumentException.class
    })
    public ResponseEntity<ErrorResponseDTO> entrada(
            Exception error,
            HttpServletRequest solicitud) {

        return respuesta(
                HttpStatus.BAD_REQUEST,
                "Formato o parámetro no válido",
                solicitud,
                Map.of()
        );
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponseDTO> integridad(
            DataIntegrityViolationException error,
            HttpServletRequest solicitud) {

        log.warn(
                "Conflicto de integridad en {}",
                solicitud.getRequestURI()
        );

        return respuesta(
                HttpStatus.CONFLICT,
                "La operación incumple una restricción de integridad",
                solicitud,
                Map.of()
        );
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<ErrorResponseDTO> autenticacion(
            AuthenticationException error,
            HttpServletRequest solicitud) {

        log.warn(
                "Autenticación fallida en {}",
                solicitud.getRequestURI()
        );

        return respuesta(
                HttpStatus.UNAUTHORIZED,
                "Usuario o contraseña incorrectos",
                solicitud,
                Map.of()
        );
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ErrorResponseDTO> accesoDenegado(
            AccessDeniedException error,
            HttpServletRequest solicitud) {

        log.warn(
                "Acceso denegado en {}",
                solicitud.getRequestURI()
        );

        return respuesta(
                HttpStatus.FORBIDDEN,
                "No tiene permisos para realizar esta operación",
                solicitud,
                Map.of()
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponseDTO> interno(
            Exception error,
            HttpServletRequest solicitud) {

        log.error(
                "Error interno en {}",
                solicitud.getRequestURI(),
                error
        );

        return respuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                solicitud,
                Map.of()
        );
    }
}
