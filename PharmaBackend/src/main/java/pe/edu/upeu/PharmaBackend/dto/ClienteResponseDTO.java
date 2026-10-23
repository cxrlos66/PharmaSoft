package pe.edu.upeu.PharmaBackend.dto;
 import jakarta.validation.Valid;
 import jakarta.validation.constraints.*;

 public record ClienteResponseDTO(Long id, String dni, String nombres, String apellidos, String email, String telefono, String direccion, Boolean estado) {}
