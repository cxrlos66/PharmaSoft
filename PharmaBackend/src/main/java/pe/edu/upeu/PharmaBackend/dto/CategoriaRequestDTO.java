package pe.edu.upeu.PharmaBackend.dto;
 import jakarta.validation.Valid;
 import jakarta.validation.constraints.*;

 public record CategoriaRequestDTO(@NotBlank @Size(min=2,max=100) String nombre, @Size(max=250) String descripcion, @NotNull Boolean estado) {}
