package pe.edu.upeu.PharmaBackend.dto;
 import jakarta.validation.Valid;
 import jakarta.validation.constraints.*;

 public record CategoriaResponseDTO(Long id, String nombre, String descripcion, Boolean estado) {}
