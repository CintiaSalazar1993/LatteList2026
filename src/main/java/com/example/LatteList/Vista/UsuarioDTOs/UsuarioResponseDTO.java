package com.example.LatteList.Vista.UsuarioDTOs;

import com.example.LatteList.Modelo.Entidades.Usuario;
import com.example.LatteList.Modelo.Enum.TipoDeUsuario;

import java.time.LocalDate;

/** VISTA (salida): lo que se devuelve de un usuario. Nunca incluye la contraseña. */
public record UsuarioResponseDTO(
        Long id,
        String nombre,
        String apellido,
        String email,
        TipoDeUsuario tipoDeUsuario,
        LocalDate fechaAlta
) {
    public static UsuarioResponseDTO from(Usuario u) {
        return new UsuarioResponseDTO(
                u.getId(), u.getNombre(), u.getApellido(), u.getEmail(),
                u.getTipoDeUsuario(), u.getFechaAlta());
    }
}