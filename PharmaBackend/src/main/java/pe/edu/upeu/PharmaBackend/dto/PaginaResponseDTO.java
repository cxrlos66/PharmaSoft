package pe.edu.upeu.PharmaBackend.dto;
import java.util.List;
import org.springframework.data.domain.Page;
public record PaginaResponseDTO<T>(List<T> contenido, int pagina, int tamanio,
 long totalElementos, int totalPaginas, boolean primera, boolean ultima) {
 public static <T> PaginaResponseDTO<T> desde(Page<T> datos) {
  return new PaginaResponseDTO<>(datos.getContent(), datos.getNumber(), datos.getSize(),
   datos.getTotalElements(), datos.getTotalPages(), datos.isFirst(), datos.isLast());
 }
}
