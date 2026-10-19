package com.example.LatteList.Modelo.Servicios;

import com.example.LatteList.Modelo.Entidades.Usuario;
import com.example.LatteList.Modelo.Enum.TipoDeUsuario;
import com.example.LatteList.Modelo.Excepciones.ForbiddenOperationException;
import com.example.LatteList.Modelo.Repositorios.UserRepository;
import com.example.LatteList.security.JwtService;
import com.example.LatteList.Vista.AuthDTOs.AuthResponseDTO;
import com.example.LatteList.Vista.AuthDTOs.LoginRequestDTO;
import com.example.LatteList.Vista.UsuarioDTOs.UsuarioRequestDTO;
import com.example.LatteList.Vista.UsuarioDTOs.UsuarioResponseDTO;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserService userService,
                       UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /** RF1: registro publico. Solo se puede registrar CLIENTE o DUENIO; los ADMIN los crea otro ADMIN. */
    @Transactional
    public AuthResponseDTO register(UsuarioRequestDTO dto) {
        if (dto.tipoDeUsuario() == TipoDeUsuario.ADMIN) {
            throw new ForbiddenOperationException("No es posible registrarse como ADMIN");
        }
        Usuario usuario = userService.crear(dto);
        return armarRespuesta(usuario);
    }

    /** RF1: login. Devuelve el token JWT. */
    @Transactional(readOnly = true)
    public AuthResponseDTO login(LoginRequestDTO dto) {
        Usuario usuario = userRepository.findByEmail(dto.email().trim().toLowerCase())
                .orElseThrow(() -> new BadCredentialsException("Credenciales inválidas"));
        if (!passwordEncoder.matches(dto.contrasena(), usuario.getContrasena())) {
            throw new BadCredentialsException("Credenciales inválidas");
        }
        return armarRespuesta(usuario);
    }

    private AuthResponseDTO armarRespuesta(Usuario usuario) {
        return new AuthResponseDTO(jwtService.generarToken(usuario), "Bearer", UsuarioResponseDTO.from(usuario));
    }
}
