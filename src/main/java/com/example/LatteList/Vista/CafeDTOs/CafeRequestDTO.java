package com.example.LatteList.Vista.CafeDTOs;

import com.example.LatteList.Modelo.Enum.CostoPromedio;
import com.example.LatteList.Modelo.Enum.Etiquetas;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.Set;

/** VISTA (entrada): datos para crear/editar un cafe. */
public record CafeRequestDTO(

        @NotBlank(message = "El nombre es obligatorio.")
        @Size(min = 3, max = 50, message = "El nombre debe contener entre 3 y 50 caracteres")
        String nombre,

        @NotBlank(message = "La dirección es obligatoria.")
        @Size(min = 3, max = 60, message = "La dirección debe contener entre 3 y 60 caracteres")
        @Pattern(
                regexp = "^[\\p{L}\\d][\\p{L}\\d\\s.]*\\s\\d+$",
                message = "La dirección debe tener el formato: Calle seguida de la altura numérica, por ejemplo 'Colón 3231'."
        )
        String direccion,

        @NotNull(message = "El costo promedio es obligatorio")
        CostoPromedio costoPromedio,

        /** Un link (http/https) o una imagen subida desde el front, en base64 (PNG, JPG o WEBP). */
        @Size(max = 300000, message = "La imagen del logo es demasiado pesada")
        @Pattern(
                regexp = "^(https?://\\S+|data:image/(png|jpeg|webp);base64,[A-Za-z0-9+/=]+)$",
                message = "El logo debe ser un link (http o https) o una imagen PNG, JPG o WEBP"
        )
        String logo,

        @Pattern(
                regexp = "^(https?://)?(www\\.)?instagram\\.com/[A-Za-z0-9_.]+/?$",
                message = "Debe ser un enlace válido de Instagram."
        )
        String instagramUrl,

        @NotEmpty(message = "Debe tener al menos una etiqueta.")
        Set<Etiquetas> etiquetas,

        /** Solo lo usa un ADMIN para crear un cafe a nombre de un DUENIO. Un DUENIO lo ignora. */
        Long idDuenio
) {
}