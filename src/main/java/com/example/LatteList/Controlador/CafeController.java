package com.example.LatteList.Controlador;

import com.example.LatteList.Modelo.Enum.CostoPromedio;
import com.example.LatteList.Modelo.Enum.Etiquetas;
import com.example.LatteList.Modelo.Servicios.CafeService;
import com.example.LatteList.Modelo.Servicios.MetricasService;
import com.example.LatteList.Vista.MetricasDTOs.MetricasDuenioDTO;
import com.example.LatteList.security.SecurityUtils;
import com.example.LatteList.Vista.CafeDTOs.CafeRequestDTO;
import com.example.LatteList.Vista.CafeDTOs.CafeResponseDTO;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** RF4, RF5, RF6, RF16, RF17, RF18 y RF24. */
@RestController
@RequestMapping("/api/cafes")
public class CafeController {

    private final CafeService cafeService;
    private final MetricasService metricasService;

    public CafeController(CafeService cafeService, MetricasService metricasService) {
        this.cafeService = cafeService;
        this.metricasService = metricasService;
    }

    @GetMapping
    public List<CafeResponseDTO> listar(@RequestParam(required = false) String nombre,
                                        @RequestParam(required = false) CostoPromedio costo,
                                        @RequestParam(required = false) List<Etiquetas> etiquetas,
                                        @RequestParam(required = false) String orden) {
        return cafeService.listar(nombre, costo, etiquetas, orden);
    }

    @GetMapping("/aleatorio")
    public CafeResponseDTO aleatorio() {
        return cafeService.aleatorio();
    }

    @GetMapping("/mis-cafes")
    @PreAuthorize("hasRole('DUENIO')")
    public List<CafeResponseDTO> misCafes() {
        return cafeService.misCafes(SecurityUtils.usuarioActual().getId());
    }

    @GetMapping("/{id}")
    public CafeResponseDTO obtener(@PathVariable Long id) {
        return cafeService.obtener(id);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('DUENIO','ADMIN')")
    public ResponseEntity<CafeResponseDTO> crear(@Valid @RequestBody CafeRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(cafeService.crear(dto, SecurityUtils.usuarioActual()));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('DUENIO')")
    public CafeResponseDTO actualizar(@PathVariable Long id, @Valid @RequestBody CafeRequestDTO dto) {
        return cafeService.actualizar(id, dto, SecurityUtils.usuarioActual());
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('DUENIO','ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        cafeService.eliminar(id, SecurityUtils.usuarioActual());
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/mis-cafes/metricas")
    @PreAuthorize("hasRole('DUENIO')")
    public MetricasDuenioDTO misMetricas() {
        return metricasService.delDuenio(SecurityUtils.usuarioActual().getId());
    }
}
