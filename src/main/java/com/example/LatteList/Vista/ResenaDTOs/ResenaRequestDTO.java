package com.example.LatteList.Vista.ResenaDTOs;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * VISTA (entrada): datos de una resena.
 * El usuario sale del token y el cafe de la URL, asi nadie puede resenar "en nombre de otro".
 */
public record ResenaRequestDTO(

        @NotNull(message = "La puntuación general es obligatoria")
        @Min(value = 1, message = "La puntuación general debe ser al menos 1")
        @Max(value = 5, message = "La puntuación general no puede superar 5")
        Integer puntuacionGeneral,

        @NotNull(message = "La puntuación de precio es obligatoria")
        @Min(value = 1, message = "La puntuación de precio debe ser al menos 1")
        @Max(value = 5, message = "La puntuación de precio no puede superar 5")
        Integer puntuacionPrecio,

        @NotNull(message = "La puntuación de atención es obligatoria")
        @Min(value = 1, message = "La puntuación de atención debe ser al menos 1")
        @Max(value = 5, message = "La puntuación de atención no puede superar 5")
        Integer puntuacionAtencion,

        @NotBlank(message = "El comentario es obligatorio")
        @Size(max = 255, message = "El comentario no puede superar los 255 caracteres")
        String comentario
) {
}
