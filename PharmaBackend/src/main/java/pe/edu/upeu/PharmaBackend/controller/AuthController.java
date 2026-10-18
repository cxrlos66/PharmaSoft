package pe.edu.upeu.PharmaBackend.controller;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import pe.edu.upeu.PharmaBackend.dto.auth.CambioContrasenaRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.auth.LoginRequestDTO;
import pe.edu.upeu.PharmaBackend.dto.auth.LoginResponseDTO;
import pe.edu.upeu.PharmaBackend.dto.auth.UsuarioResponseDTO;
import pe.edu.upeu.PharmaBackend.service.service.AuthService;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(
            @Valid @RequestBody LoginRequestDTO datos) {

        return ResponseEntity.ok(authService.login(datos));
    }

    @GetMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> usuarioActual(
            Authentication autenticacion) {

        return ResponseEntity.ok(
                authService.usuarioActual(autenticacion.getName())
        );
    }

    @PutMapping("/cambiar-contrasena")
    public ResponseEntity<Void> cambiarContrasena(
            Authentication autenticacion,
            @Valid @RequestBody CambioContrasenaRequestDTO datos) {

        authService.cambiarContrasena(
                autenticacion.getName(),
                datos
        );

        return ResponseEntity.noContent().build();
    }
}
