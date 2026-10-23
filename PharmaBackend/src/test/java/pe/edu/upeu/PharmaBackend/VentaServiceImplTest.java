package pe.edu.upeu.PharmaBackend;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import pe.edu.upeu.PharmaBackend.dto.*;
import pe.edu.upeu.PharmaBackend.entity.*;
import pe.edu.upeu.PharmaBackend.repository.*;
import pe.edu.upeu.PharmaBackend.exception.*;
import pe.edu.upeu.PharmaBackend.service.impl.VentaServiceImpl;
import jakarta.persistence.EntityManager;
@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {
 @Mock ClienteRepository clientes;
 @Mock ProductoRepository productos;
 @Mock VentaRepository ventas;
 @Mock EntityManager entidad;
 @InjectMocks VentaServiceImpl servicio;
 private Cliente clienteActivo() {
  Cliente c=new Cliente();c.setId(1L);c.setEstado(true);c.setNombres("Ana");c.setApellidos("Pérez");return c;
 }
 private Producto producto(Long id,int stock) {
  Producto p=new Producto();p.setId(id);p.setNombre("Producto "+id);
  p.setEstado(true);p.setPrecio(new BigDecimal("12.50"));p.setStock(stock);return p;
 }
 @Test void rechazaClienteInactivo() {
  Cliente cliente=clienteActivo();cliente.setEstado(false);
  when(clientes.findById(1L)).thenReturn(Optional.of(cliente));
  VentaRequestDTO solicitud=new VentaRequestDTO(1L,List.of(new DetalleVentaRequestDTO(1L,1)));
  assertThrows(ReglaNegocioException.class,()->servicio.registrar(solicitud));
  verify(ventas,never()).saveAndFlush(any());
 }
 @Test void rechazaClienteInexistente() {
  when(clientes.findById(1L)).thenReturn(Optional.empty());
  assertThrows(RecursoNoEncontradoException.class,()->servicio.registrar(
   new VentaRequestDTO(1L,List.of(new DetalleVentaRequestDTO(1L,1)))));
 }
 @Test void rechazaProductosRepetidos() {
  when(clientes.findById(1L)).thenReturn(Optional.of(clienteActivo()));
  when(productos.findById(3L)).thenReturn(Optional.of(producto(3L,20)));
  when(productos.descontarStock(3L,1)).thenReturn(1);
  VentaRequestDTO solicitud=new VentaRequestDTO(1L,List.of(
   new DetalleVentaRequestDTO(3L,1),new DetalleVentaRequestDTO(3L,1)));
  assertThrows(ReglaNegocioException.class,()->servicio.registrar(solicitud));
  verify(ventas,never()).saveAndFlush(any());
 }
 @Test void rechazaStockInsuficiente() {
  when(clientes.findById(1L)).thenReturn(Optional.of(clienteActivo()));
  when(productos.findById(5L)).thenReturn(Optional.of(producto(5L,0)));
  when(productos.descontarStock(5L,2)).thenReturn(0);
  assertThrows(ReglaNegocioException.class,()->servicio.registrar(
   new VentaRequestDTO(1L,List.of(new DetalleVentaRequestDTO(5L,2)))));
  verify(ventas,never()).saveAndFlush(any());
 }
 @Test void fallaEnSegundoDetalleAntesDeGuardarVenta() {
  when(clientes.findById(1L)).thenReturn(Optional.of(clienteActivo()));
  when(productos.findById(1L)).thenReturn(Optional.of(producto(1L,20)));
  when(productos.findById(2L)).thenReturn(Optional.of(producto(2L,0)));
  when(productos.descontarStock(1L,2)).thenReturn(1);
  when(productos.descontarStock(2L,1)).thenReturn(0);
  assertThrows(ReglaNegocioException.class,()->servicio.registrar(new VentaRequestDTO(1L,
    List.of(new DetalleVentaRequestDTO(1L,2),new DetalleVentaRequestDTO(2L,1)))));
  verify(ventas,never()).saveAndFlush(any());
  // El rollback de la actualización ejecutada por repositorio debe probarse además con Oracle real.
 }

 @Test void calculaSubtotalYTotalEnServidor() {
  when(clientes.findById(1L)).thenReturn(Optional.of(clienteActivo()));
  when(productos.findById(1L)).thenReturn(Optional.of(producto(1L,20)));
  when(productos.descontarStock(1L,2)).thenReturn(1);
  when(ventas.saveAndFlush(any(Venta.class))).thenAnswer(llamada -> {
   Venta venta=llamada.getArgument(0);
   venta.setId(90L);
   venta.setFechaRegistro(java.time.LocalDateTime.of(2026,10,9,11,0));
   return venta;
  });
  VentaResponseDTO respuesta=servicio.registrar(
   new VentaRequestDTO(1L,List.of(new DetalleVentaRequestDTO(1L,2))));
  assertEquals(new BigDecimal("25.00"),respuesta.total());
  assertEquals(new BigDecimal("25.00"),respuesta.detalles().get(0).subtotal());
  verify(productos).descontarStock(1L,2);
 }
 @Test void anulaYReponeStock() {
  Venta venta=new Venta();venta.setId(8L);venta.setCliente(clienteActivo());
  venta.setEstado(pe.edu.upeu.PharmaBackend.enums.EstadoVenta.REGISTRADA);
  venta.setFechaRegistro(java.time.LocalDateTime.now());
  venta.setTotal(new BigDecimal("25.00"));
  DetalleVenta detalle=new DetalleVenta();detalle.setProducto(producto(1L,2));
  detalle.setCantidad(2);detalle.setPrecio(new BigDecimal("12.50"));
  detalle.setSubtotal(new BigDecimal("25.00"));venta.agregarDetalle(detalle);
  when(ventas.bloquearPorId(8L)).thenReturn(Optional.of(venta));
  when(productos.reponerStock(1L,2)).thenReturn(1);
  VentaResponseDTO respuesta=servicio.anular(8L);
  assertEquals("ANULADA",respuesta.estado());
  verify(productos).reponerStock(1L,2);
 }
}
