package pe.edu.upeu.PharmaBackend.dto;
 import jakarta.validation.Valid;
 import jakarta.validation.constraints.*;
 import java.util.List;
 public record VentaRequestDTO(@NotNull @Positive Long clienteId, @NotEmpty List<@Valid DetalleVentaRequestDTO> detalles) {}
