package pe.edu.upeu.PharmaBackend;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import pe.edu.upeu.PharmaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.PharmaBackend.service.impl.Ordenamiento;
import java.util.Set;
class OrdenamientoTest {
 @Test void rechazaCampoDesconocido() {
  assertThrows(ReglaNegocioException.class,()->Ordenamiento.crear("clave","asc",Set.of("fechaRegistro")));
 }
 @Test void rechazaPaginacionInvalida() {
  assertThrows(ReglaNegocioException.class,()->Ordenamiento.pagina(0,101,
   Ordenamiento.crear("fechaRegistro","desc",Set.of("fechaRegistro"))));
 }
}
