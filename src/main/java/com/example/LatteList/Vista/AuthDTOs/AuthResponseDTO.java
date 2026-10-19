package com.example.LatteList.Vista.AuthDTOs;

import com.example.LatteList.Vista.UsuarioDTOs.UsuarioResponseDTO;

public record AuthResponseDTO(
        String token,
        String tipo,
        UsuarioResponseDTO usuario
) {
}
