package com.example.LatteList.Vista.ListaDTOs;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ListaRequestDTO(

        @NotBlank(message = "El nombre de la lista es obligatorio")
        @Size(max = 50, message = "El nombre de la lista no puede superar los 50 caracteres")
        String nombre
) {
}
