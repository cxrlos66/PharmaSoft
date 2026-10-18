package pe.edu.upeu.PharmaBackend.controller;
import pe.edu.upeu.PharmaBackend.dto.*;
import pe.edu.upeu.PharmaBackend.service.service.*;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
@RestController @RequestMapping("/api/v1/clientes") @RequiredArgsConstructor
public class ClienteController {
 private final ClienteService servicio;

 @GetMapping public ResponseEntity<PaginaResponseDTO<ClienteResponseDTO>> listar(
   @RequestParam(defaultValue="0") int pagina,@RequestParam(defaultValue="20") int tamanio,
   @RequestParam(defaultValue="apellidos") String ordenarPor,@RequestParam(defaultValue="asc") String direccion) {
  return ResponseEntity.ok(servicio.listar(pagina,tamanio,ordenarPor,direccion));
 }
 @GetMapping("/{id}") public ResponseEntity<ClienteResponseDTO> obtener(@PathVariable Long id) {
  return ResponseEntity.ok(servicio.obtener(id));
 }
 @PostMapping public ResponseEntity<ClienteResponseDTO> registrar(@Valid @RequestBody ClienteRequestDTO solicitud) {
  return ResponseEntity.status(HttpStatus.CREATED).body(servicio.registrar(solicitud));
 }
 @PutMapping("/{id}") public ResponseEntity<ClienteResponseDTO> actualizar(
   @PathVariable Long id,@Valid @RequestBody ClienteRequestDTO solicitud) {
  return ResponseEntity.ok(servicio.actualizar(id,solicitud));
 }
 @DeleteMapping("/{id}") public ResponseEntity<Void> eliminar(@PathVariable Long id) {
  servicio.eliminar(id);return ResponseEntity.noContent().build();
 }

}
