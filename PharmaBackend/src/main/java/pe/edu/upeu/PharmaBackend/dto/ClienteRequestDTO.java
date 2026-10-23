package pe.edu.upeu.PharmaBackend.dto;
 import jakarta.validation.Valid;
 import jakarta.validation.constraints.*;

 public record ClienteRequestDTO(@NotBlank @Pattern(regexp="\\d{8}") String dni, @NotBlank String nombres, @NotBlank String apellidos, @Email String email, String telefono, String direccion, @NotNull Boolean estado) {}
