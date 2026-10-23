package pe.edu.upeu.PharmaBackend.repository;
import pe.edu.upeu.PharmaBackend.entity.Venta;
import pe.edu.upeu.PharmaBackend.enums.EstadoVenta;
import pe.edu.upeu.PharmaBackend.dto.reporte.*;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.domain.*;
import org.springframework.data.repository.query.Param;
import jakarta.persistence.LockModeType;
import java.time.LocalDateTime;
import java.util.*;
public interface VentaRepository extends JpaRepository<Venta,Long> {
 boolean existsByClienteId(Long clienteId);
 @EntityGraph(attributePaths = {"cliente"})
 @Query("SELECT v FROM Venta v WHERE (:clienteId IS NULL OR v.cliente.id = :clienteId) " +
    "AND (:estado IS NULL OR v.estado = :estado) " +
    "AND (:desde IS NULL OR v.fechaRegistro >= :desde) " +
    "AND (:hasta IS NULL OR v.fechaRegistro < :hasta)")
 List<Venta> buscar(@Param("clienteId") Long clienteId, @Param("estado") EstadoVenta estado,
   @Param("desde") LocalDateTime desde, @Param("hasta") LocalDateTime hasta, Sort orden);
 @EntityGraph(attributePaths = {"cliente"})
 @Query(value = "SELECT v FROM Venta v WHERE (:clienteId IS NULL OR v.cliente.id = :clienteId) " +
    "AND (:estado IS NULL OR v.estado = :estado) " +
    "AND (:desde IS NULL OR v.fechaRegistro >= :desde) " +
    "AND (:hasta IS NULL OR v.fechaRegistro < :hasta)",
   countQuery = "SELECT count(v) FROM Venta v WHERE (:clienteId IS NULL OR v.cliente.id = :clienteId) " +
    "AND (:estado IS NULL OR v.estado = :estado) " +
    "AND (:desde IS NULL OR v.fechaRegistro >= :desde) " +
    "AND (:hasta IS NULL OR v.fechaRegistro < :hasta)")
 Page<Venta> buscarPagina(@Param("clienteId") Long clienteId, @Param("estado") EstadoVenta estado,
  @Param("desde") LocalDateTime desde,@Param("hasta") LocalDateTime hasta, Pageable pagina);
 @Lock(LockModeType.PESSIMISTIC_WRITE)
 @Query("SELECT v FROM Venta v WHERE v.id = :id")
 Optional<Venta> bloquearPorId(@Param("id") Long id);
 @Query("SELECT new pe.edu.upeu.PharmaBackend.dto.reporte.VentaPorCategoriaDTO("+
    "d.producto.categoria.id,d.producto.categoria.nombre,SUM(d.cantidad),SUM(d.subtotal)) " +
    "FROM DetalleVenta d WHERE d.venta.estado = :estado " +
    "AND d.venta.fechaRegistro >= :desde AND d.venta.fechaRegistro < :hasta " +
    "GROUP BY d.producto.categoria.id,d.producto.categoria.nombre ORDER BY SUM(d.subtotal) DESC")
 List<VentaPorCategoriaDTO> ventasPorCategoria(@Param("estado") EstadoVenta estado,
     @Param("desde") LocalDateTime desde,@Param("hasta") LocalDateTime hasta);
 @Query("SELECT new pe.edu.upeu.PharmaBackend.dto.reporte.ProductoMasVendidoDTO("+
    "d.producto.id,d.producto.nombre,SUM(d.cantidad),SUM(d.subtotal)) " +
    "FROM DetalleVenta d WHERE d.venta.estado = :estado " +
    "AND d.venta.fechaRegistro >= :desde AND d.venta.fechaRegistro < :hasta " +
    "GROUP BY d.producto.id,d.producto.nombre ORDER BY SUM(d.cantidad) DESC")
 List<ProductoMasVendidoDTO> productosMasVendidos(@Param("estado") EstadoVenta estado,
     @Param("desde") LocalDateTime desde,@Param("hasta") LocalDateTime hasta);
}
