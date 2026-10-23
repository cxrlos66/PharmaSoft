package pe.edu.upeu.PharmaBackend.service.impl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.*;
import java.util.*;
import pe.edu.upeu.PharmaBackend.dto.reporte.*;
import pe.edu.upeu.PharmaBackend.enums.EstadoVenta;
import pe.edu.upeu.PharmaBackend.repository.VentaRepository;
import pe.edu.upeu.PharmaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.PharmaBackend.service.service.ReporteService;
@Slf4j @Service @RequiredArgsConstructor
public class ReporteServiceImpl implements ReporteService {
 private final VentaRepository ventas;
 private LocalDateTime inicio(LocalDate desde,LocalDate hasta) {
  if(desde!=null && hasta!=null && desde.isAfter(hasta))
   throw new ReglaNegocioException("La fecha inicial es posterior a la final");
  return (desde==null?LocalDate.of(1900,1,1):desde).atStartOfDay();
 }
 private LocalDateTime fin(LocalDate hasta) {
  return hasta==null ? LocalDate.of(9999,12,30).atStartOfDay() : hasta.plusDays(1).atStartOfDay();
 }
 @Override @Transactional(readOnly=true)
 public List<VentaPorCategoriaDTO> ventasPorCategoria(LocalDate desde,LocalDate hasta) {
  long tiempo=System.currentTimeMillis();
  log.info("Reporte ventas por categoría desde={} hasta={}",desde,hasta);
  List<VentaPorCategoriaDTO> resultado=ventas.ventasPorCategoria(EstadoVenta.REGISTRADA,inicio(desde,hasta),fin(hasta));
  log.info("Reporte por categoría filas={} duracionMs={}",resultado.size(),System.currentTimeMillis()-tiempo);
  return resultado;
 }
 @Override @Transactional(readOnly=true)
 public List<ProductoMasVendidoDTO> productosMasVendidos(LocalDate desde,LocalDate hasta) {
  long tiempo=System.currentTimeMillis();
  log.info("Reporte productos más vendidos desde={} hasta={}",desde,hasta);
  List<ProductoMasVendidoDTO> resultado=ventas.productosMasVendidos(EstadoVenta.REGISTRADA,inicio(desde,hasta),fin(hasta));
  log.info("Reporte productos filas={} duracionMs={}",resultado.size(),System.currentTimeMillis()-tiempo);
  return resultado;
 }
}
