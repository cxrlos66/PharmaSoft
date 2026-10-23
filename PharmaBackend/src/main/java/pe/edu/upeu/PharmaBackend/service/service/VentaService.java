package pe.edu.upeu.PharmaBackend.service.service;
import pe.edu.upeu.PharmaBackend.dto.*;
import pe.edu.upeu.PharmaBackend.enums.EstadoVenta;
import java.time.LocalDate;
import java.util.List;
public interface VentaService {
 VentaResponseDTO registrar(VentaRequestDTO solicitud);
 VentaResponseDTO obtener(Long id);
 List<VentaResponseDTO> listar();
 PaginaResponseDTO<VentaResponseDTO> buscar(Long clienteId,EstadoVenta estado,LocalDate desde,
   LocalDate hasta,int pagina,int tamanio,String ordenarPor,String direccion);
 VentaResponseDTO anular(Long id);
}
