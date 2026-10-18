package pe.edu.upeu.PharmaBackend;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import java.time.LocalDate;
import java.util.List;
import pe.edu.upeu.PharmaBackend.repository.VentaRepository;
import pe.edu.upeu.PharmaBackend.enums.EstadoVenta;
import pe.edu.upeu.PharmaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.PharmaBackend.service.impl.ReporteServiceImpl;
@ExtendWith(MockitoExtension.class)
class ReporteServiceImplTest {
 @Mock VentaRepository ventas;
 @InjectMocks ReporteServiceImpl servicio;
 @Test void rechazaFechasInvertidas() {
  assertThrows(ReglaNegocioException.class,()->servicio.ventasPorCategoria(
    LocalDate.of(2026,10,9),LocalDate.of(2026,10,8)));
 }
 @Test void reportesExcluyenAnuladas() {
  when(ventas.productosMasVendidos(eq(EstadoVenta.REGISTRADA),any(),any())).thenReturn(List.of());
  assertTrue(servicio.productosMasVendidos(null,null).isEmpty());
  verify(ventas).productosMasVendidos(eq(EstadoVenta.REGISTRADA),any(),any());
 }
}
