package pe.edu.upeu.PharmaBackend.controller;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.format.annotation.DateTimeFormat;
import pe.edu.upeu.PharmaBackend.dto.*;
import pe.edu.upeu.PharmaBackend.enums.EstadoVenta;
import pe.edu.upeu.PharmaBackend.service.service.VentaService;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
@RestController @RequestMapping("/api/v1/ventas") @RequiredArgsConstructor
public class VentaController {
 private final VentaService servicio;
 @PostMapping public ResponseEntity<VentaResponseDTO> registrar(@Valid @RequestBody VentaRequestDTO solicitud) {
  return ResponseEntity.status(HttpStatus.CREATED).body(servicio.registrar(solicitud));
 }
 @GetMapping("/{id}") public ResponseEntity<VentaResponseDTO> obtener(@PathVariable Long id) {
  return ResponseEntity.ok(servicio.obtener(id));
 }
 @GetMapping public ResponseEntity<List<VentaResponseDTO>> listar() {
  return ResponseEntity.ok(servicio.listar());
 }
 @GetMapping("/buscar")
 public ResponseEntity<PaginaResponseDTO<VentaResponseDTO>> buscar(
   @RequestParam(required=false) Long clienteId,
   @RequestParam(required=false) EstadoVenta estado,
   @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate desde,
   @RequestParam(required=false) @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate hasta,
   @RequestParam(defaultValue="0") int pagina,
   @RequestParam(defaultValue="20") int tamanio,
   @RequestParam(defaultValue="fechaRegistro") String ordenarPor,
   @RequestParam(defaultValue="desc") String direccion) {
  // En el frontend de la sesión 9 se envía ordenarPor=fecha.
  if("fecha".equals(ordenarPor)) ordenarPor="fechaRegistro";
  return ResponseEntity.ok(servicio.buscar(clienteId,estado,desde,hasta,pagina,tamanio,ordenarPor,direccion));
 }
 @PatchMapping("/{id}/anular") public ResponseEntity<VentaResponseDTO> anular(@PathVariable Long id) {
  return ResponseEntity.ok(servicio.anular(id));
 }
}
