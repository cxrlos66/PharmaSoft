package pe.edu.upeu.PharmaBackend.service.impl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
import java.util.*;
import pe.edu.upeu.PharmaBackend.dto.*;
import pe.edu.upeu.PharmaBackend.entity.*;
import pe.edu.upeu.PharmaBackend.repository.*;
import pe.edu.upeu.PharmaBackend.exception.*;
import pe.edu.upeu.PharmaBackend.service.service.ProductoService;
@Slf4j @Service @RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {
 private final ProductoRepository productos;
 private final CategoriaRepository categorias;
 private final DetalleVentaRepository detalles;
 private ProductoResponseDTO convertir(Producto p) {
  return new ProductoResponseDTO(p.getId(),p.getCodigo(),p.getNombre(),p.getDescripcion(),p.getPrecio(),
   p.getStock(),p.getEstado(),p.getCategoria().getId(),p.getCategoria().getNombre());
 }
 private Producto recuperar(Long id) {return productos.findById(id).orElseThrow(() ->
   new RecursoNoEncontradoException("Producto no encontrado: " + id));}
 private void codigoUnico(String codigo,Long id) {
  productos.findByCodigoIgnoreCase(codigo).ifPresent(actual -> {
   if (!actual.getId().equals(id)) throw new ReglaNegocioException("El código del producto ya existe");
  });
 }
 private void copiar(Producto p,ProductoRequestDTO s) {
  Categoria categoria=categorias.findById(s.categoriaId()).orElseThrow(() ->
   new RecursoNoEncontradoException("Categoría no encontrada: " + s.categoriaId()));
  p.setCodigo(s.codigo().trim().toUpperCase(java.util.Locale.ROOT));p.setNombre(s.nombre().trim());
  p.setDescripcion(s.descripcion());p.setPrecio(s.precio().setScale(2,java.math.RoundingMode.HALF_UP));
  p.setStock(s.stock());p.setEstado(s.estado());p.setCategoria(categoria);
 }
 @Override @Transactional public ProductoResponseDTO registrar(ProductoRequestDTO solicitud) {
  codigoUnico(solicitud.codigo().trim(),null);Producto p=new Producto();copiar(p,solicitud);
  ProductoResponseDTO resultado=convertir(productos.save(p));log.info("Producto registrado id={}",resultado.id());return resultado;
 }
 @Override @Transactional(readOnly=true) public ProductoResponseDTO obtener(Long id) {return convertir(recuperar(id));}
 @Override @Transactional(readOnly=true)
 public PaginaResponseDTO<ProductoResponseDTO> listar(int pagina,int tamanio,String ordenarPor,String direccion) {
  long inicio=System.currentTimeMillis();
  Page<ProductoResponseDTO> resultado=productos.findAll(Ordenamiento.pagina(pagina,tamanio,
    Ordenamiento.crear(ordenarPor,direccion,Set.of("id","codigo","nombre","precio","stock","estado")))).map(this::convertir);
  log.info("Consulta productos pagina={} filas={} duracionMs={}",pagina,resultado.getNumberOfElements(),System.currentTimeMillis()-inicio);
  return PaginaResponseDTO.desde(resultado);
 }
 @Override @Transactional(readOnly=true)
 public List<ProductoResponseDTO> listarPorCategoria(Long categoriaId) {
  if(!categorias.existsById(categoriaId)) throw new RecursoNoEncontradoException("Categoría no encontrada: " + categoriaId);
  return productos.findByCategoriaId(categoriaId).stream().map(this::convertir).toList();
 }
 @Override @Transactional public ProductoResponseDTO actualizar(Long id,ProductoRequestDTO solicitud) {
  Producto p=recuperar(id);codigoUnico(solicitud.codigo().trim(),id);copiar(p,solicitud);
  log.info("Producto actualizado id={}",id);return convertir(p);
 }
 @Override @Transactional public void eliminar(Long id) {
  Producto p=recuperar(id);
  if(detalles.existsByProductoId(id)) throw new ReglaNegocioException("El producto aparece en ventas registradas");
  productos.delete(p);log.info("Producto eliminado id={}",id);
 }
}
