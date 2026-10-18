package com.example.LatteList.Modelo.Servicios;

import com.example.LatteList.Modelo.Entidades.ListaDeCafe;
import com.example.LatteList.Modelo.Entidades.Usuario;
import com.example.LatteList.Modelo.Enum.TipoDeUsuario;
import com.example.LatteList.Modelo.Excepciones.ResourceNotFoundException;
import com.example.LatteList.Modelo.Repositorios.ListaDeCafeRepository;
import com.example.LatteList.Modelo.Repositorios.UserRepository;
import com.example.LatteList.Modelo.Validaciones.UserValidation;
import com.example.LatteList.Vista.UsuarioDTOs.UsuarioRequestDTO;
import com.example.LatteList.Vista.UsuarioDTOs.UsuarioResponseDTO;
import com.example.LatteList.Vista.UsuarioDTOs.UsuarioUpdateDTO;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final ListaDeCafeRepository listaRepository;
    private final UserValidation userValidation;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository,
                       ListaDeCafeRepository listaRepository,
                       UserValidation userValidation,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.listaRepository = listaRepository;
        this.userValidation = userValidation;
        this.passwordEncoder = passwordEncoder;
    }

    /** Crea un usuario (hashea la contraseña). Si es CLIENTE, le crea su lista "Favoritos". */
    @Transactional
    public Usuario crear(UsuarioRequestDTO dto) {
        String email = dto.email().trim().toLowerCase();
        userValidation.validarEmailNoRepetido(email);

        Usuario usuario = new Usuario();
        usuario.setNombre(dto.nombre().trim());
        usuario.setApellido(dto.apellido().trim());
        usuario.setEmail(email);
        usuario.setContrasena(passwordEncoder.encode(dto.contrasena()));
        usuario.setTipoDeUsuario(dto.tipoDeUsuario());
        Usuario guardado = userRepository.save(usuario);

        if (guardado.getTipoDeUsuario() == TipoDeUsuario.CLIENTE) {
            ListaDeCafe favoritos = new ListaDeCafe();
            favoritos.setNombre("Favoritos");
            favoritos.setFavoritos(true);
            favoritos.setUsuario(guardado);
            listaRepository.save(favoritos);
        }
        return guardado;
    }

    @Transactional
    public UsuarioResponseDTO crearComoAdmin(UsuarioRequestDTO dto) {
        return UsuarioResponseDTO.from(crear(dto));
    }

    @Transactional(readOnly = true)
    public UsuarioResponseDTO obtener(Long id) {
        return UsuarioResponseDTO.from(buscar(id));
    }

    /** RF20 y RF21: lista usuarios, con filtro opcional por rol y por texto (nombre, apellido o email). */
    @Transactional(readOnly = true)
    public List<UsuarioResponseDTO> listar(String texto, TipoDeUsuario tipo) {
        Specification<Usuario> spec = (root, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();
            if (tipo != null) {
                filtros.add(cb.equal(root.get("tipoDeUsuario"), tipo));
            }
            if (texto != null && !texto.isBlank()) {
                String like = "%" + texto.trim().toLowerCase() + "%";
                filtros.add(cb.or(
                        cb.like(cb.lower(root.<String>get("nombre")), like),
                        cb.like(cb.lower(root.<String>get("apellido")), like),
                        cb.like(cb.lower(root.<String>get("email")), like)));
            }
            return cb.and(filtros.toArray(new Predicate[0]));
        };
        return userRepository.findAll(spec, Sort.by("id")).stream()
                .map(UsuarioResponseDTO::from)
                .toList();
    }

    /** RF3: edicion del perfil propio. */
    @Transactional
    public UsuarioResponseDTO actualizarPerfil(Long id, UsuarioUpdateDTO dto) {
        Usuario usuario = buscar(id);
        usuario.setNombre(dto.nombre().trim());
        usuario.setApellido(dto.apellido().trim());
        if (dto.contrasena() != null) {
            usuario.setContrasena(passwordEncoder.encode(dto.contrasena()));
        }
        return UsuarioResponseDTO.from(userRepository.save(usuario));
    }

    /** Baja de usuario (propia o por ADMIN). Borra en cascada sus listas, resenas y cafes. */
    @Transactional
    public void eliminar(Long id) {
        userRepository.delete(buscar(id));
    }

    private Usuario buscar(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario con id " + id));
    }
}
