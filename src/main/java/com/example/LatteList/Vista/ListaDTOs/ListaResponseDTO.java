package com.example.LatteList.Vista.ListaDTOs;

import com.example.LatteList.Modelo.Entidades.ListaDeCafe;
import com.example.LatteList.Vista.CafeDTOs.CafeResumenDTO;

import java.time.LocalDate;
import java.util.List;

public record ListaResponseDTO(
        Long id,
        String nombre,
        LocalDate fechaCreacion,
        boolean favoritos,
        int cantidadCafes,
        List<CafeResumenDTO> cafes
) {
    public static ListaResponseDTO from(ListaDeCafe l) {
        List<CafeResumenDTO> cafes = l.getCafes().stream().map(CafeResumenDTO::from).toList();
        return new ListaResponseDTO(l.getId(), l.getNombre(), l.getFechaCreacion(), l.isFavoritos(), cafes.size(), cafes);
    }
}
