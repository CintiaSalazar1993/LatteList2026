package com.example.LatteList.Controlador;

import com.example.LatteList.Modelo.Servicios.MetricasService;
import com.example.LatteList.Vista.MetricasDTOs.MetricasGlobalesDTO;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/** RF28: métricas globales del sistema. */
@RestController
@RequestMapping("/api/metricas")
@PreAuthorize("hasRole('ADMIN')")
public class MetricasController {

    private final MetricasService metricasService;

    public MetricasController(MetricasService metricasService) {
        this.metricasService = metricasService;
    }

    @GetMapping
    public MetricasGlobalesDTO globales() {
        return metricasService.globales();
    }
}
