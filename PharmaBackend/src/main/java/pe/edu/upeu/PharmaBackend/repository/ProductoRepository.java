package pe.edu.upeu.PharmaBackend.repository;
import pe.edu.upeu.PharmaBackend.entity.Producto;
import org.springframework.data.jpa.repository.*;
import org.springframework.data.repository.query.Param;
import java.util.*;
public interface ProductoRepository extends JpaRepository<Producto,Long> {
 Optional<Producto> findByCodigoIgnoreCase(String codigo);
 boolean existsByCategoriaId(Long categoriaId);
 List<Producto> findByCategoriaId(Long categoriaId);
 @Modifying(flushAutomatically = true)
 @Query("UPDATE Producto p SET p.stock = p.stock - :cantidad WHERE p.id = :productoId AND p.estado = true AND p.stock >= :cantidad")
 int descontarStock(@Param("productoId") Long productoId, @Param("cantidad") Integer cantidad);
 @Modifying(flushAutomatically = true)
 @Query("UPDATE Producto p SET p.stock = p.stock + :cantidad WHERE p.id = :productoId")
 int reponerStock(@Param("productoId") Long productoId, @Param("cantidad") Integer cantidad);
}
