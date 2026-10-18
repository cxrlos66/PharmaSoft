package pe.edu.upeu.PharmaBackend.service.impl;
import pe.edu.upeu.PharmaBackend.exception.ReglaNegocioException;
import java.util.Set;
import org.springframework.data.domain.*;
public final class Ordenamiento {
 private Ordenamiento() {}
 public static Sort crear(String campo,String direccion,Set<String> permitidos) {
  if (!permitidos.contains(campo)) throw new ReglaNegocioException("Ordenamiento no permitido: " + campo);
  if (!"asc".equalsIgnoreCase(direccion) && !"desc".equalsIgnoreCase(direccion))
   throw new ReglaNegocioException("Dirección de ordenamiento no permitida: " + direccion);
  return Sort.by("desc".equalsIgnoreCase(direccion) ? Sort.Direction.DESC : Sort.Direction.ASC,campo);
 }
 public static Pageable pagina(int numero,int tamanio,Sort orden) {
  if (numero < 0 || tamanio < 1 || tamanio > 100)
    throw new ReglaNegocioException("La página debe ser positiva y el tamaño entre 1 y 100");
  return PageRequest.of(numero,tamanio,orden);
 }
}
