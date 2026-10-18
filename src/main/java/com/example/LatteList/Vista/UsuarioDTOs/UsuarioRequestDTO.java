package com.example.LatteList.Vista.UsuarioDTOs;

import com.example.LatteList.Modelo.Enum.TipoDeUsuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/** VISTA (entrada): datos para registrar/crear un usuario. */
public record UsuarioRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 30, message = "El nombre debe contener entre 2 y 30 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, max = 30, message = "El apellido debe contener entre 2 y 30 caracteres")
        String apellido,

        @NotBlank(message = "El email es obligatorio")
        @Email(message = "El email debe tener un formato válido")
        String email,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 6, max = 20, message = "La contraseña debe contener entre 6 y 20 caracteres")
        String contrasena,

        @NotNull(message = "El tipo de usuario es obligatorio")
        TipoDeUsuario tipoDeUsuario
) {
}
