
package pe.edu.upeu.PharmaBackend.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.upeu.PharmaBackend.dto.auth.EmpleadoResponseDTO;
import pe.edu.upeu.PharmaBackend.dto.auth.RolResponseDTO;
import pe.edu.upeu.PharmaBackend.dto.auth.UsuarioRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.auth.UsuarioResponseDTO;
import pe.edu.upeu.PharmaBackend.entity.Empleado;
import pe.edu.upeu.PharmaBackend.entity.Rol;
import pe.edu.upeu.PharmaBackend.entity.Usuario;
import pe.edu.upeu.PharmaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.PharmaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.PharmaBackend.mapper.UsuarioMapper;
import pe.edu.upeu.PharmaBackend.repository.EmpleadoRepository;
import pe.edu.upeu.PharmaBackend.repository.RolRepository;
import pe.edu.upeu.PharmaBackend.repository.UsuarioRepository;
import pe.edu.upeu.PharmaBackend.service.service.UsuarioService;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final EmpleadoRepository empleadoRepository;
    private final RolRepository rolRepository;
    private final PasswordEncoder codificador;
    private final UsuarioMapper usuarioMapper;

    public UsuarioServiceImpl(
            UsuarioRepository usuarioRepository,
            EmpleadoRepository empleadoRepository,
            RolRepository rolRepository,
            PasswordEncoder codificador,
            UsuarioMapper usuarioMapper) {

        this.usuarioRepository = usuarioRepository;
        this.empleadoRepository = empleadoRepository;
        this.rolRepository = rolRepository;
        this.codificador = codificador;
        this.usuarioMapper = usuarioMapper;
    }

    @Override
    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO datos) {

        String nombre = datos.getUsername()
                .trim()
                .toLowerCase(Locale.ROOT);

        Empleado empleado = empleadoRepository
                .findById(datos.getEmpleadoId())
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Empleado no encontrado con id: "
                                        + datos.getEmpleadoId()
                        )
                );

        if (!Boolean.TRUE.equals(empleado.getEstado())) {
            throw new ReglaNegocioException(
                    "El empleado " + empleado.getNombreCompleto()
                            + " está inactivo"
            );
        }

        if (usuarioRepository.existsByEmpleadoId(empleado.getId())) {
            throw new ReglaNegocioException(
                    "El empleado " + empleado.getNombreCompleto()
                            + " ya tiene un usuario"
            );
        }

        if (usuarioRepository.existsByUsernameIgnoreCase(nombre)) {
            throw new ReglaNegocioException(
                    "Ya existe un usuario con el nombre: " + nombre
            );
        }

        List<Rol> roles = rolRepository.findAllById(
                datos.getRolIds()
        );

        if (roles.size() != datos.getRolIds().size()) {

            Set<Long> encontrados = roles.stream()
                    .map(Rol::getId)
                    .collect(Collectors.toSet());

            Set<Long> faltantes = new HashSet<>(
                    datos.getRolIds()
            );

            faltantes.removeAll(encontrados);

            throw new RecursoNoEncontradoException(
                    "Roles no encontrados: " + faltantes
            );
        }

        String clave = datos.getPassword();

        if (clave.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ReglaNegocioException(
                    "La contraseña no puede superar 72 bytes"
            );
        }

        Usuario usuario = new Usuario();

        usuario.setUsername(nombre);
        usuario.setPassword(codificador.encode(clave));
        usuario.setEstado(true);
        usuario.setEmpleado(empleado);
        usuario.setRoles(new HashSet<>(roles));

        Usuario guardado = usuarioRepository.save(usuario);

        return usuarioMapper.toUsuarioResponse(guardado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar() {

        return usuarioRepository
                .findAll(Sort.by("username"))
                .stream()
                .map(usuarioMapper::toUsuarioResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<EmpleadoResponseDTO> listarEmpleados() {

        Set<Long> conUsuario =
                usuarioRepository.findEmpleadoIdsConUsuario();

        return empleadoRepository
                .findAll(Sort.by("apellidos", "nombres"))
                .stream()
                .map(empleado ->
                        usuarioMapper.toEmpleadoResponse(
                                empleado,
                                conUsuario.contains(empleado.getId())
                        )
                )
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<RolResponseDTO> listarRoles() {

        return rolRepository
                .findAll(Sort.by("nombre"))
                .stream()
                .map(usuarioMapper::toRolResponse)
                .toList();
    }
}
