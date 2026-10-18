package pe.edu.upeu.PharmaBackend.dto;
 import jakarta.validation.Valid;
 import jakarta.validation.constraints.*;
 import java.math.BigDecimal;
 public record ProductoResponseDTO(Long id, String codigo, String nombre, String descripcion, BigDecimal precio, Integer stock, Boolean estado, Long categoriaId, String categoriaNombre) {}
