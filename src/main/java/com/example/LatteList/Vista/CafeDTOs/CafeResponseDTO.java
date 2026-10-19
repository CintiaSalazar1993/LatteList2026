package com.example.LatteList.Vista.CafeDTOs;

import com.example.LatteList.Modelo.Enum.CostoPromedio;
import com.example.LatteList.Modelo.Enum.Etiquetas;

import java.util.Set;

/** VISTA (salida): ficha completa de un cafe. */
public record CafeResponseDTO(
        Long id,
        String nombre,
        String direccion,
        CostoPromedio costoPromedio,
        String logo,
        String instagramUrl,
        Set<Etiquetas> etiquetas,
        Long duenioId,
        String duenioNombre,
        Double puntuacionPromedio,
        long cantidadResenas
) {
}
