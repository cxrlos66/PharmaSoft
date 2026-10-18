package pe.edu.upeu.PharmaBackend.service.impl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
import java.util.Set;
import pe.edu.upeu.PharmaBackend.dto.*;
import pe.edu.upeu.PharmaBackend.entity.Categoria;
import pe.edu.upeu.PharmaBackend.repository.*;
import pe.edu.upeu.PharmaBackend.exception.*;
import pe.edu.upeu.PharmaBackend.service.service.CategoriaService;
@Slf4j @Service @RequiredArgsConstructor
public class CategoriaServiceImpl implements CategoriaService {
 private final CategoriaRepository categorias;
 private final ProductoRepository productos;
 private CategoriaResponseDTO convertir(Categoria categoria) {
  return new CategoriaResponseDTO(categoria.getId(),categoria.getNombre(),
   categoria.getDescripcion(),categoria.getEstado());
 }
 private Categoria recuperar(Long id) {
  return categorias.findById(id).orElseThrow(() ->
    new RecursoNoEncontradoException("Categoría no encontrada: " + id));
 }
 private String nombreLimpio(String nombre) { return nombre.trim().replaceAll("\\s+"," "); }
 private void nombreUnico(String nombre,Long id) {
  categorias.findByNombreIgnoreCase(nombre).ifPresent(actual -> {
   if (!actual.getId().equals(id)) throw new ReglaNegocioException("La categoría ya existe");
  });
 }
 @Override @Transactional
 public CategoriaResponseDTO registrar(CategoriaRequestDTO solicitud) {
  String nombre = nombreLimpio(solicitud.nombre());
  nombreUnico(nombre,null);
  Categoria categoria = new Categoria();
  categoria.setNombre(nombre);categoria.setDescripcion(solicitud.descripcion());categoria.setEstado(solicitud.estado());
  CategoriaResponseDTO resultado = convertir(categorias.save(categoria));
  log.info("Categoría registrada id={}",resultado.id());return resultado;
 }
 @Override @Transactional(readOnly=true)
 public CategoriaResponseDTO obtener(Long id) { return convertir(recuperar(id)); }
 @Override @Transactional(readOnly=true)
 public PaginaResponseDTO<CategoriaResponseDTO> listar(int pagina,int tamanio,String ordenarPor,String direccion) {
  long inicio = System.currentTimeMillis();
  Page<CategoriaResponseDTO> resultado = categorias.findAll(Ordenamiento.pagina(pagina,tamanio,
   Ordenamiento.crear(ordenarPor,direccion,Set.of("id","nombre","estado")))).map(this::convertir);
  log.info("Consulta categorías pagina={} filas={} duracionMs={}",pagina,resultado.getNumberOfElements(),System.currentTimeMillis()-inicio);
  return PaginaResponseDTO.desde(resultado);
 }
 @Override @Transactional
 public CategoriaResponseDTO actualizar(Long id,CategoriaRequestDTO solicitud) {
  Categoria categoria = recuperar(id);
  String nombre = nombreLimpio(solicitud.nombre());nombreUnico(nombre,id);
  categoria.setNombre(nombre);categoria.setDescripcion(solicitud.descripcion());categoria.setEstado(solicitud.estado());
  log.info("Categoría actualizada id={}",id);return convertir(categoria);
 }
 @Override @Transactional
 public void eliminar(Long id) {
  Categoria categoria = recuperar(id);
  if (productos.existsByCategoriaId(id)) throw new ReglaNegocioException("La categoría contiene productos");
  categorias.delete(categoria);log.info("Categoría eliminada id={}",id);
 }
}
