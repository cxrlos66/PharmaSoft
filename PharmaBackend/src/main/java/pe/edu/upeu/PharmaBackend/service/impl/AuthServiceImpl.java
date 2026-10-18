package pe.edu.upeu.PharmaBackend.service.impl;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.upeu.PharmaBackend.dto.auth.CambioContrasenaRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.auth.LoginRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.auth.LoginResponseDTO;
import pe.edu.upeu.PharmaBackend.dto.auth.UsuarioResponseDTO;
import pe.edu.upeu.PharmaBackend.entity.Usuario;
import pe.edu.upeu.PharmaBackend.exception.RecursoNoEncontradoException;
import pe.edu.upeu.PharmaBackend.exception.ReglaNegocioException;
import pe.edu.upeu.PharmaBackend.mapper.UsuarioMapper;
import pe.edu.upeu.PharmaBackend.repository.UsuarioRepository;
import pe.edu.upeu.PharmaBackend.security.JwtService;
import pe.edu.upeu.PharmaBackend.security.PermisosPorRol;
import pe.edu.upeu.PharmaBackend.service.service.AuthService;

@Service
public class AuthServiceImpl implements AuthService {

    private final AuthenticationManager autenticador;
    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioMapper usuarioMapper;
    private final PasswordEncoder codificador;

    public AuthServiceImpl(
            AuthenticationManager autenticador,
            JwtService jwtService,
            UsuarioRepository usuarioRepository,
            UsuarioMapper usuarioMapper,
            PasswordEncoder codificador) {

        this.autenticador = autenticador;
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
        this.usuarioMapper = usuarioMapper;
        this.codificador = codificador;
    }

    @Override
    @Transactional(readOnly = true)
    public LoginResponseDTO login(LoginRequestDTO datos) {

        String nombre = datos.getUsername().trim();

        Authentication autenticacion = autenticador.authenticate(
                new UsernamePasswordAuthenticationToken(
                        nombre,
                        datos.getPassword()
                )
        );

        UserDetails usuarioAutenticado =
                (UserDetails) autenticacion.getPrincipal();

        Usuario usuario = buscar(usuarioAutenticado.getUsername());

        String token = jwtService.generarToken(usuarioAutenticado);

        List<String> roles =
                usuarioMapper.nombresRoles(usuario.getRoles());

        return new LoginResponseDTO(
                token,
                "Bearer",
                jwtService.getExpiracionSegundos(),
                usuario.getUsername(),
                usuario.getEmpleado().getNombreCompleto(),
                roles,
                PermisosPorRol.permisos(usuario.getRoles())
        );
    }

    @Override
    @Transactional(readOnly = true)
    public UsuarioResponseDTO usuarioActual(String nombre) {
        return usuarioMapper.toUsuarioResponse(buscar(nombre));
    }

    @Override
    @Transactional
    public void cambiarContrasena(
            String nombre,
            CambioContrasenaRequestDTO datos) {

        Usuario usuario = buscar(nombre);

        if (!codificador.matches(
                datos.getContrasenaActual(),
                usuario.getPassword())) {

            throw new ReglaNegocioException(
                    "La contraseña actual es incorrecta"
            );
        }

        String nueva = datos.getContrasenaNueva();
        String confirmacion = datos.getConfirmarContrasena();

        if (!nueva.equals(confirmacion)) {
            throw new ReglaNegocioException(
                    "Las contraseñas nuevas no coinciden"
            );
        }

        if (nueva.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new ReglaNegocioException(
                    "La contraseña no puede superar 72 bytes"
            );
        }

        if (!nueva.matches("(?s)^(?=.*[A-Za-z])(?=.*\\d).+$")) {
            throw new ReglaNegocioException(
                    "La contraseña debe contener al menos una letra y un número"
            );
        }

        if (codificador.matches(nueva, usuario.getPassword())) {
            throw new ReglaNegocioException(
                    "La nueva contraseña debe ser diferente a la actual"
            );
        }

        usuario.setPassword(codificador.encode(nueva));

        usuarioRepository.save(usuario);
    }

    private Usuario buscar(String nombre) {

        return usuarioRepository
                .findByUsernameIgnoreCase(nombre)
                .orElseThrow(() ->
                        new RecursoNoEncontradoException(
                                "Usuario no encontrado: " + nombre
                        )
                );
    }
}
