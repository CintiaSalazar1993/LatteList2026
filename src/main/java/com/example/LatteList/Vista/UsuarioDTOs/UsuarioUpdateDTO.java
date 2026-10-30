package com.example.LatteList.Vista.UsuarioDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** VISTA (entrada): datos editables del perfil propio. Para cambiar la contraseña hay que mandar también la actual. */
public record UsuarioUpdateDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 2, max = 30, message = "El nombre debe contener entre 2 y 30 caracteres")
        String nombre,

        @NotBlank(message = "El apellido es obligatorio")
        @Size(min = 2, max = 30, message = "El apellido debe contener entre 2 y 30 caracteres")
        String apellido,

        @Size(min = 6, max = 20, message = "La contraseña debe contener entre 6 y 20 caracteres")
        String contrasena,

        /** Solo hace falta si se manda una contraseña nueva. */
        String contrasenaActual
) {
}
