package pe.edu.upeu.PharmaBackend.service.impl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;
import java.util.Set;
import pe.edu.upeu.PharmaBackend.dto.*;
import pe.edu.upeu.PharmaBackend.entity.Cliente;
import pe.edu.upeu.PharmaBackend.repository.*;
import pe.edu.upeu.PharmaBackend.exception.*;
import pe.edu.upeu.PharmaBackend.service.service.ClienteService;
@Slf4j @Service @RequiredArgsConstructor
public class ClienteServiceImpl implements ClienteService {
 private final ClienteRepository clientes;
 private final VentaRepository ventas;
 private ClienteResponseDTO convertir(Cliente cliente) {
  return new ClienteResponseDTO(cliente.getId(),cliente.getDni(),cliente.getNombres(),cliente.getApellidos(),
   cliente.getEmail(),cliente.getTelefono(),cliente.getDireccion(),cliente.getEstado());
 }
 private Cliente recuperar(Long id) {
  return clientes.findById(id).orElseThrow(() ->
   new RecursoNoEncontradoException("Cliente no encontrado: " + id));
 }
 private void dniUnico(String dni,Long id) {
  clientes.findByDni(dni).ifPresent(actual -> {
   if (!actual.getId().equals(id)) throw new ReglaNegocioException("El DNI ya está registrado");
  });
 }
 private void copiar(Cliente cliente,ClienteRequestDTO s) {
  cliente.setDni(s.dni());cliente.setNombres(s.nombres().trim());cliente.setApellidos(s.apellidos().trim());
  cliente.setEmail(s.email());cliente.setTelefono(s.telefono());cliente.setDireccion(s.direccion());
  cliente.setEstado(s.estado());
 }
 @Override @Transactional public ClienteResponseDTO registrar(ClienteRequestDTO solicitud) {
  dniUnico(solicitud.dni(),null);Cliente cliente = new Cliente();copiar(cliente,solicitud);
  ClienteResponseDTO resultado=convertir(clientes.save(cliente));log.info("Cliente registrado id={}",resultado.id());return resultado;
 }
 @Override @Transactional(readOnly=true) public ClienteResponseDTO obtener(Long id) {return convertir(recuperar(id));}
 @Override @Transactional(readOnly=true)
 public PaginaResponseDTO<ClienteResponseDTO> listar(int pagina,int tamanio,String ordenarPor,String direccion) {
  long inicio=System.currentTimeMillis();
  Page<ClienteResponseDTO> resultado=clientes.findAll(Ordenamiento.pagina(pagina,tamanio,
   Ordenamiento.crear(ordenarPor,direccion,Set.of("id","dni","nombres","apellidos","estado")))).map(this::convertir);
  log.info("Consulta clientes pagina={} filas={} duracionMs={}",pagina,resultado.getNumberOfElements(),System.currentTimeMillis()-inicio);
  return PaginaResponseDTO.desde(resultado);
 }
 @Override @Transactional public ClienteResponseDTO actualizar(Long id,ClienteRequestDTO solicitud) {
  Cliente cliente=recuperar(id);dniUnico(solicitud.dni(),id);copiar(cliente,solicitud);
  log.info("Cliente actualizado id={}",id);return convertir(cliente);
 }
 @Override @Transactional public void eliminar(Long id) {
  Cliente cliente=recuperar(id);
  if(ventas.existsByClienteId(id)) throw new ReglaNegocioException("El cliente tiene ventas registradas");
  clientes.delete(cliente);log.info("Cliente eliminado id={}",id);
 }
}
