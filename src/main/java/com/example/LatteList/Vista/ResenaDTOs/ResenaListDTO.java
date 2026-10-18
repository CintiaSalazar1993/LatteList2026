package com.example.LatteList.Vista.ResenaDTOs;

import com.example.LatteList.Modelo.Entidades.Resena;

import java.time.LocalDate;

/** VISTA (salida): resena resumida, para listados. */
public record ResenaListDTO(
        Long id,
        Long cafeId,
        String cafeNombre,
        String autor,
        Integer puntuacionGeneral,
        String comentario,
        LocalDate fecha
) {
    public static ResenaListDTO from(Resena r) {
        return new ResenaListDTO(
                r.getId(),
                r.getCafe().getId(),
                r.getCafe().getNombre(),
                r.getUsuario().getNombre() + " " + r.getUsuario().getApellido(),
                r.getPuntuacionGeneral(),
                r.getComentario(),
                r.getFecha());
    }
}

