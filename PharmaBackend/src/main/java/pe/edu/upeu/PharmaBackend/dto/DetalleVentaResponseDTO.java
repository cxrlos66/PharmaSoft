package pe.edu.upeu.PharmaBackend.dto;
 import jakarta.validation.Valid;
 import jakarta.validation.constraints.*;
 import java.math.BigDecimal;
 public record DetalleVentaResponseDTO(Long productoId, String productoNombre, Integer cantidad, BigDecimal precio, BigDecimal subtotal) {}
