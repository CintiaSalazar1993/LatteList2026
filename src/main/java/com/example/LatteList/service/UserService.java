package com.example.LatteList.service;

import com.example.LatteList.DTOs.UsuarioDTOs.UsuarioRequestDTO;
import com.example.LatteList.DTOs.UsuarioDTOs.UsuarioResponseDTO;
import com.example.LatteList.model.Usuario;
import com.example.LatteList.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public UsuarioResponseDTO crearUsuario(UsuarioRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.getNombre());
        usuario.setApellido(dto.getApellido());
        usuario.setEmail(dto.getEmail());
        usuario.setContrasena(dto.getContrasena());
        usuario.setTipoDeUsuario(dto.getTipoDeUsuario());

        Usuario guardado = userRepository.save(usuario);
        return new UsuarioResponseDTO(guardado);
    }

    public List<UsuarioResponseDTO> listarUsuarios() {
        return userRepository.findAll()
                .stream()
                .map(UsuarioResponseDTO::new)
                .collect(Collectors.toList());
    }
}
