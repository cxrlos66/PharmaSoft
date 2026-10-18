package pe.edu.upeu.PharmaBackend.repository;
import pe.edu.upeu.PharmaBackend.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
public interface ClienteRepository extends JpaRepository<Cliente,Long> {
 Optional<Cliente> findByDni(String dni);
}
