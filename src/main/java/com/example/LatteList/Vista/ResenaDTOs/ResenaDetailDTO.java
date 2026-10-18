package com.example.LatteList.Vista.ResenaDTOs;

import com.example.LatteList.Modelo.Entidades.Resena;

import java.time.LocalDate;

/** VISTA (salida): resena completa. */
public record ResenaDetailDTO(
        Long id,
        Long cafeId,
        String cafeNombre,
        Long usuarioId,
        String autor,
        Integer puntuacionGeneral,
        Integer puntuacionPrecio,
        Integer puntuacionAtencion,
        String comentario,
        LocalDate fecha
) {
    public static ResenaDetailDTO from(Resena r) {
        return new ResenaDetailDTO(
                r.getId(),
                r.getCafe().getId(),
                r.getCafe().getNombre(),
                r.getUsuario().getId(),
                r.getUsuario().getNombre() + " " + r.getUsuario().getApellido(),
                r.getPuntuacionGeneral(),
                r.getPuntuacionPrecio(),
                r.getPuntuacionAtencion(),
                r.getComentario(),
                r.getFecha());
    }
}

