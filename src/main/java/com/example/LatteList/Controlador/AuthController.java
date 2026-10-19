package com.example.LatteList.Controlador;

import com.example.LatteList.Modelo.Servicios.AuthService;
import com.example.LatteList.Vista.AuthDTOs.AuthResponseDTO;
import com.example.LatteList.Vista.AuthDTOs.LoginRequestDTO;
import com.example.LatteList.Vista.UsuarioDTOs.UsuarioRequestDTO;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/** RF1 y RF2: registro, login y logout. */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponseDTO> register(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(authService.register(dto));
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@Valid @RequestBody LoginRequestDTO dto) {
        return ResponseEntity.ok(authService.login(dto));
    }

    /**
     * Con JWT la sesion es "stateless": el servidor no guarda nada, asi que cerrar sesion
     * consiste en que el cliente descarte su token. Este endpoint existe para cumplir el RF2.
     */
    @PostMapping("/logout")
    public ResponseEntity<Map<String, String>> logout() {
        return ResponseEntity.ok(Map.of("mensaje", "Sesión cerrada. Descartá el token en el cliente."));
    }
}

