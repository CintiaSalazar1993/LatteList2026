package com.example.LatteList.Vista.CafeDTOs;

import com.example.LatteList.Modelo.Entidades.Cafe;
import com.example.LatteList.Modelo.Enum.CostoPromedio;

/** VISTA (salida): version corta de un cafe, para listas. */
public record CafeResumenDTO(
        Long id,
        String nombre,
        String direccion,
        CostoPromedio costoPromedio,
        String logo
) {
    public static CafeResumenDTO from(Cafe c) {
        return new CafeResumenDTO(c.getId(), c.getNombre(), c.getDireccion(), c.getCostoPromedio(), c.getLogo());
    }
}