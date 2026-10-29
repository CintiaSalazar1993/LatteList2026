// MetricasDuenioDTO.java
package com.example.LatteList.Vista.MetricasDTOs;

import java.util.List;

/** RF25. totalRankeados = cafés del sistema con al menos una reseña. */
public record MetricasDuenioDTO(long totalCafes, long totalResenas, Double promedioGeneral,
                                int totalRankeados, List<CafeMetricaDTO> cafes) {}
