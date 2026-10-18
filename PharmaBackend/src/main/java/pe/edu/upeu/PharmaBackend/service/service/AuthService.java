package pe.edu.upeu.PharmaBackend.service.service;

import pe.edu.upeu.PharmaBackend.dto.auth.CambioContrasenaRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.auth.LoginRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.auth.LoginResponseDTO;
import pe.edu.upeu.PharmaBackend.dto.auth.UsuarioResponseDTO;

public interface AuthService {

    LoginResponseDTO login(LoginRequestDTO datos);

    UsuarioResponseDTO usuarioActual(String nombre);

    void cambiarContrasena(
            String nombre,
            CambioContrasenaRequestDTO datos
    );
}
