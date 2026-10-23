package pe.edu.upeu.PharmaBackend.dto;
 import jakarta.validation.Valid;
 import jakarta.validation.constraints.*;
 import java.math.BigDecimal; import java.time.LocalDateTime; import java.util.List;
 public record VentaResponseDTO(Long id, LocalDateTime fecha, LocalDateTime fechaRegistro, Long clienteId, String clienteNombre, String estado, BigDecimal total, List<DetalleVentaResponseDTO> detalles) {}
