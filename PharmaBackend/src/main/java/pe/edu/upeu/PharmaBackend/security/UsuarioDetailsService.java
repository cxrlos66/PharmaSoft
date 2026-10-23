package pe.edu.upeu.PharmaBackend.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import pe.edu.upeu.PharmaBackend.entity.Usuario;
import pe.edu.upeu.PharmaBackend.repository.UsuarioRepository;

@Service
public class UsuarioDetailsService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;

    public UsuarioDetailsService(UsuarioRepository usuarioRepository) {
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String nombre)
            throws UsernameNotFoundException {

        Usuario usuario = usuarioRepository
                .findByUsernameIgnoreCase(nombre.trim())
                .orElseThrow(() -> new UsernameNotFoundException(
                        "Usuario no encontrado: " + nombre));

        boolean activo = Boolean.TRUE.equals(usuario.getEstado())
                && Boolean.TRUE.equals(usuario.getEmpleado().getEstado());

        return User.withUsername(usuario.getUsername())
                .password(usuario.getPassword())
                .authorities(PermisosPorRol.authorities(usuario.getRoles()))
                .disabled(!activo)
                .build();
    }
}
