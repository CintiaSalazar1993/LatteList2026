// MetricasGlobalesDTO.java
package com.example.LatteList.Vista.MetricasDTOs;
import java.util.List;
import java.util.Map;
/** RF28: métricas globales del sistema (solo ADMIN). */
public record MetricasGlobalesDTO(long totalUsuarios, long clientes, long duenios, long admins,
                                  long totalCafes, long totalResenas, Double promedioGeneral,
                                  Map<String, Long> cafesPorCosto, Map<String, Long> cafesPorEtiqueta,
                                  List<TopCafeDTO> topCafes, List<AltaMesDTO> altasUltimosMeses) {}
