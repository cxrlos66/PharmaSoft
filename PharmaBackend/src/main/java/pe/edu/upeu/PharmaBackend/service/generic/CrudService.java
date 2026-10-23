package pe.edu.upeu.PharmaBackend.service.generic;
import pe.edu.upeu.PharmaBackend.dto.PaginaResponseDTO;
public interface CrudService<Solicitud, Respuesta, Identificador> {
 Respuesta registrar(Solicitud solicitud);
 Respuesta obtener(Identificador id);
 PaginaResponseDTO<Respuesta> listar(int pagina,int tamanio,String ordenarPor,String direccion);
 Respuesta actualizar(Identificador id,Solicitud solicitud);
 void eliminar(Identificador id);
}
