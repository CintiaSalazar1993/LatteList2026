package com.example.LatteList.Vista.MetricasDTOs;

/** RF25: métricas de un café del dueño. posicion es null si aún no tiene reseñas. */
public record CafeMetricaDTO(Long cafeId, String nombre, long cantidadResenas,
                             Double puntuacionPromedio, Integer posicion) {}
