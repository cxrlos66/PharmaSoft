package pe.edu.upeu.PharmaBackend.repository;
import pe.edu.upeu.PharmaBackend.entity.DetalleVenta;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.*;
public interface DetalleVentaRepository extends JpaRepository<DetalleVenta,Long> {
 boolean existsByProductoId(Long productoId);
}
