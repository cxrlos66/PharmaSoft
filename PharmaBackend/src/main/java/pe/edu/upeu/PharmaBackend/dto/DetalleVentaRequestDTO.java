package pe.edu.upeu.PharmaBackend.dto;
 import jakarta.validation.Valid;
 import jakarta.validation.constraints.*;

 public record DetalleVentaRequestDTO(@NotNull @Positive Long productoId, @NotNull @Min(1) Integer cantidad) {}
