package pe.edu.upeu.PharmaBackend.controller;
import pe.edu.upeu.PharmaBackend.dto.*;
import pe.edu.upeu.PharmaBackend.service.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
@RestController @RequestMapping("/api/v1/productos") @RequiredArgsConstructor
public class ProductoController {
 private final ProductoService servicio;

 @GetMapping public ResponseEntity<PaginaResponseDTO<ProductoResponseDTO>> listar(
   @RequestParam(defaultValue="0") int pagina,@RequestParam(defaultValue="20") int tamanio,
   @RequestParam(defaultValue="nombre") String ordenarPor,@RequestParam(defaultValue="asc") String direccion) {
  return ResponseEntity.ok(servicio.listar(pagina,tamanio,ordenarPor,direccion));
 }
 @GetMapping("/{id}") public ResponseEntity<ProductoResponseDTO> obtener(@PathVariable Long id) {
  return ResponseEntity.ok(servicio.obtener(id));
 }
 @PostMapping public ResponseEntity<ProductoResponseDTO> registrar(@Valid @RequestBody ProductoRequestDTO solicitud) {
  return ResponseEntity.status(HttpStatus.CREATED).body(servicio.registrar(solicitud));
 }
 @PutMapping("/{id}") public ResponseEntity<ProductoResponseDTO> actualizar(
   @PathVariable Long id,@Valid @RequestBody ProductoRequestDTO solicitud) {
  return ResponseEntity.ok(servicio.actualizar(id,solicitud));
 }
 @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id) {
  servicio.eliminar(id);return ResponseEntity.noContent().build();
 }

}
