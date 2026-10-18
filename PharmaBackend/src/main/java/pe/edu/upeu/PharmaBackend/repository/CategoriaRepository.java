package pe.edu.upeu.PharmaBackend.repository;
import pe.edu.upeu.PharmaBackend.entity.Categoria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface CategoriaRepository extends JpaRepository<Categoria,Long> {
 Optional<Categoria> findByNombreIgnoreCase(String nombre);
}
