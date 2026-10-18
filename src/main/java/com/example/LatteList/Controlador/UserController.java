package com.example.LatteList.Controlador;

import com.example.LatteList.Modelo.Enum.TipoDeUsuario;
import com.example.LatteList.Modelo.Servicios.UserService;
import com.example.LatteList.security.SecurityUtils;
import com.example.LatteList.Vista.UsuarioDTOs.UsuarioRequestDTO;
import com.example.LatteList.Vista.UsuarioDTOs.UsuarioResponseDTO;
import com.example.LatteList.Vista.UsuarioDTOs.UsuarioUpdateDTO;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** RF3, RF14, RF15 (perfil propio) y RF19, RF20, RF21 (gestion de usuarios por ADMIN). */
@RestController
@RequestMapping("/api/usuarios")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    // ---------- perfil propio (cualquier rol logueado) ----------

    @GetMapping("/me")
    public UsuarioResponseDTO miPerfil() {
        return userService.obtener(SecurityUtils.usuarioActual().getId());
    }

    @PutMapping("/me")
    public UsuarioResponseDTO actualizarMiPerfil(@Valid @RequestBody UsuarioUpdateDTO dto) {
        return userService.actualizarPerfil(SecurityUtils.usuarioActual().getId(), dto);
    }

    @DeleteMapping("/me")
    public ResponseEntity<Void> eliminarMiCuenta() {
        userService.eliminar(SecurityUtils.usuarioActual().getId());
        return ResponseEntity.noContent().build();
    }

    // ---------- gestion de usuarios (solo ADMIN) ----------

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UsuarioResponseDTO> listar(@RequestParam(required = false) String texto,
                                           @RequestParam(required = false) TipoDeUsuario tipo) {
        return userService.listar(texto, tipo);
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UsuarioResponseDTO obtener(@PathVariable Long id) {
        return userService.obtener(id);
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<UsuarioResponseDTO> crear(@Valid @RequestBody UsuarioRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.crearComoAdmin(dto));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        userService.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}

