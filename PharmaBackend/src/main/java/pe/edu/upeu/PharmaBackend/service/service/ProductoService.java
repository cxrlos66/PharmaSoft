package pe.edu.upeu.PharmaBackend.service.service;
import pe.edu.upeu.PharmaBackend.dto.*;
import pe.edu.upeu.PharmaBackend.service.generic.CrudService;
public interface ProductoService extends CrudService<ProductoRequestDTO,ProductoResponseDTO,Long> {
java.util.List<ProductoResponseDTO> listarPorCategoria(Long categoriaId);
}
