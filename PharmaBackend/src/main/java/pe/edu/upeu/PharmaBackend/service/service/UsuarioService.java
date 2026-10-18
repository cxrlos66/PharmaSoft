
package pe.edu.upeu.PharmaBackend.service.service;

import java.util.List;

import pe.edu.upeu.PharmaBackend.dto.auth.EmpleadoResponseDTO;
import pe.edu.upeu.PharmaBackend.dto.auth.RolResponseDTO;
import pe.edu.upeu.PharmaBackend.dto.auth.UsuarioRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.auth.UsuarioResponseDTO;

public interface UsuarioService {

    UsuarioResponseDTO crear(UsuarioRequestDTO datos);

    List<UsuarioResponseDTO> listar();

    List<EmpleadoResponseDTO> listarEmpleados();

    List<RolResponseDTO> listarRoles();
}
