package pe.edu.upeu.PharmaBackend.dto;
 import jakarta.validation.Valid;
 import jakarta.validation.constraints.*;
 import java.math.BigDecimal;
 public record ProductoRequestDTO(@NotBlank String codigo, @NotBlank String nombre, String descripcion, @NotNull @DecimalMin("0.01") BigDecimal precio, @NotNull @Min(0) Integer stock, @NotNull Boolean estado, @NotNull @Positive Long categoriaId) {}
