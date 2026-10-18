package pe.edu.upeu.PharmaBackend.service.impl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
import java.math.*;
import java.time.*;
import java.util.*;
import pe.edu.upeu.PharmaBackend.dto.*;
import pe.edu.upeu.PharmaBackend.entity.*;
import pe.edu.upeu.PharmaBackend.enums.EstadoVenta;
import pe.edu.upeu.PharmaBackend.repository.*;
import pe.edu.upeu.PharmaBackend.exception.*;
import pe.edu.upeu.PharmaBackend.service.service.VentaService;
@Slf4j @Service @RequiredArgsConstructor
public class VentaServiceImpl implements VentaService {
 private final ClienteRepository clientes;
 private final ProductoRepository productos;
 private final VentaRepository ventas;
 private Venta recuperar(Long id) {
  return ventas.findById(id).orElseThrow(() -> new RecursoNoEncontradoException("Venta no encontrada: " + id));
 }
 private VentaResponseDTO convertir(Venta venta) {
  List<DetalleVentaResponseDTO> lista=venta.getDetalles().stream()
    .map(detalle -> new DetalleVentaResponseDTO(detalle.getProducto().getId(),
      detalle.getProducto().getNombre(),detalle.getCantidad(),detalle.getPrecio(),detalle.getSubtotal()))
    .toList();
  String nombre=venta.getCliente().getNombres()+" "+venta.getCliente().getApellidos();
  return new VentaResponseDTO(venta.getId(),venta.getFechaRegistro(),venta.getFechaRegistro(),
   venta.getCliente().getId(),nombre,venta.getEstado().name(),venta.getTotal(),lista);
 }
 @Override @Transactional
 public VentaResponseDTO registrar(VentaRequestDTO solicitud) {
  long inicio=System.currentTimeMillis();
  log.info("Registro de venta cliente={} lineas={}",solicitud.clienteId(),solicitud.detalles().size());
  Cliente cliente=clientes.findById(solicitud.clienteId()).orElseThrow(() ->
   new RecursoNoEncontradoException("Cliente no encontrado: " + solicitud.clienteId()));
  if(!Boolean.TRUE.equals(cliente.getEstado())) throw new ReglaNegocioException("El cliente está inactivo");
  Set<Long> usados=new HashSet<>();
  Venta venta=new Venta();venta.setCliente(cliente);venta.setEstado(EstadoVenta.REGISTRADA);
  BigDecimal total=BigDecimal.ZERO;
  for(DetalleVentaRequestDTO linea:solicitud.detalles()) {
   if(!usados.add(linea.productoId())) throw new ReglaNegocioException("La venta repite un producto");
   Producto producto=productos.findById(linea.productoId()).orElseThrow(() ->
     new RecursoNoEncontradoException("Producto no encontrado: " + linea.productoId()));
   if(!Boolean.TRUE.equals(producto.getEstado())) throw new ReglaNegocioException("Producto inactivo: " + producto.getNombre());
   // El UPDATE condicionado evita que dos cajas vendan las mismas existencias.
   int filas=productos.descontarStock(producto.getId(),linea.cantidad());
   if(filas!=1) throw new ReglaNegocioException("Stock insuficiente para " + producto.getNombre());
   BigDecimal precio=producto.getPrecio().setScale(2,RoundingMode.HALF_UP);
   BigDecimal subtotal=precio.multiply(BigDecimal.valueOf(linea.cantidad())).setScale(2,RoundingMode.HALF_UP);
   DetalleVenta detalle=new DetalleVenta();
   detalle.setProducto(producto);detalle.setCantidad(linea.cantidad());
   detalle.setPrecio(precio);detalle.setSubtotal(subtotal);
   venta.agregarDetalle(detalle);
   total=total.add(subtotal);
  }
  venta.setTotal(total.setScale(2,RoundingMode.HALF_UP));
  Venta guardada=ventas.saveAndFlush(venta);
  VentaResponseDTO resultado=convertir(guardada);
  log.info("Venta registrada id={} filas={} total={} duracionMs={}",resultado.id(),resultado.detalles().size(),total,System.currentTimeMillis()-inicio);
  return resultado;
 }
 @Override @Transactional(readOnly=true)
 public VentaResponseDTO obtener(Long id) {return convertir(recuperar(id));}
 @Override @Transactional(readOnly=true)
 public List<VentaResponseDTO> listar() {
  long inicio=System.currentTimeMillis();
  List<VentaResponseDTO> resultado=ventas.findAll(Sort.by(Sort.Direction.DESC,"fechaRegistro"))
    .stream().map(this::convertir).toList();
  log.info("Listado de ventas filas={} duracionMs={}",resultado.size(),System.currentTimeMillis()-inicio);
  return resultado;
 }
 @Override @Transactional(readOnly=true)
 public PaginaResponseDTO<VentaResponseDTO> buscar(Long clienteId,EstadoVenta estado,LocalDate desde,LocalDate hasta,
    int pagina,int tamanio,String ordenarPor,String direccion) {
  long inicio=System.currentTimeMillis();
  if(desde!=null && hasta!=null && desde.isAfter(hasta))
   throw new ReglaNegocioException("La fecha inicial es posterior a la final");
  Sort orden=Ordenamiento.crear(ordenarPor,direccion,Set.of("id","fechaRegistro","total","estado"));
  Pageable solicitud=Ordenamiento.pagina(pagina,tamanio,orden);
  LocalDateTime fechaDesde=desde==null?null:desde.atStartOfDay();
  LocalDateTime fechaHasta=hasta==null?null:hasta.plusDays(1).atStartOfDay();
  log.info("Búsqueda cliente={} estado={} desde={} hasta={} pagina={} orden={} direccion={}",
    clienteId,estado,desde,hasta,pagina,ordenarPor,direccion);
  Page<VentaResponseDTO> resultado=ventas.buscarPagina(clienteId,estado,fechaDesde,fechaHasta,solicitud).map(this::convertir);
  log.info("Búsqueda finalizada filas={} total={} duracionMs={}",resultado.getNumberOfElements(),
   resultado.getTotalElements(),System.currentTimeMillis()-inicio);
  return PaginaResponseDTO.desde(resultado);
 }
 @Override @Transactional
 public VentaResponseDTO anular(Long id) {
  long inicio=System.currentTimeMillis();
  Venta venta=ventas.bloquearPorId(id).orElseThrow(() ->
   new RecursoNoEncontradoException("Venta no encontrada: " + id));
  if(venta.getEstado()==EstadoVenta.ANULADA) throw new ReglaNegocioException("La venta ya está anulada");
  for(DetalleVenta detalle:venta.getDetalles()) {
   int filas=productos.reponerStock(detalle.getProducto().getId(),detalle.getCantidad());
   if(filas!=1) throw new ReglaNegocioException("No se pudo reponer el stock");
  }
  venta.setEstado(EstadoVenta.ANULADA);
  VentaResponseDTO resultado=convertir(venta);
  log.info("Venta anulada id={} lineas={} duracionMs={}",id,venta.getDetalles().size(),System.currentTimeMillis()-inicio);
  return resultado;
 }
}
