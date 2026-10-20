package com.example.LatteList.Controlador;

import com.example.LatteList.Modelo.Servicios.ResenaService;
import com.example.LatteList.security.SecurityUtils;
import com.example.LatteList.Vista.ResenaDTOs.ResenaDetailDTO;
import com.example.LatteList.Vista.ResenaDTOs.ResenaListDTO;
import com.example.LatteList.Vista.ResenaDTOs.ResenaRequestDTO;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** RF8, RF9, RF11, RF22, RF23, RF26 y RF27. */
@RestController
@RequestMapping("/api")
public class ResenaController {

    private final ResenaService resenaService;

    public ResenaController(ResenaService resenaService) {
        this.resenaService = resenaService;
    }

    @PostMapping("/cafes/{cafeId}/resenas")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResponseEntity<ResenaDetailDTO> crear(@PathVariable Long cafeId,
                                                 @Valid @RequestBody ResenaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(resenaService.crear(cafeId, dto, SecurityUtils.usuarioActual()));
    }

    @GetMapping("/cafes/{cafeId}/resenas")
    public List<ResenaListDTO> listarPorCafe(@PathVariable Long cafeId) {
        return resenaService.listarPorCafe(cafeId);
    }

    @GetMapping("/resenas/mis-resenas")
    @PreAuthorize("hasRole('CLIENTE')")
    public List<ResenaListDTO> misResenas() {
        return resenaService.misResenas(SecurityUtils.usuarioActual().getId());
    }

    @GetMapping("/resenas/recibidas")
    @PreAuthorize("hasRole('DUENIO')")
    public List<ResenaListDTO> recibidas() {
        return resenaService.recibidas(SecurityUtils.usuarioActual().getId());
    }

    @GetMapping("/resenas")
    @PreAuthorize("hasRole('ADMIN')")
    public List<ResenaListDTO> buscar(@RequestParam(required = false) Long cafeId,
                                      @RequestParam(required = false) Integer puntajeMin,
                                      @RequestParam(required = false) Integer puntajeMax) {
        return resenaService.buscar(cafeId, puntajeMin, puntajeMax);
    }

    @PutMapping("/resenas/{id}")
    @PreAuthorize("hasRole('CLIENTE')")
    public ResenaDetailDTO actualizar(@PathVariable Long id,
                                      @Valid @RequestBody ResenaRequestDTO dto) {
        return resenaService.actualizar(id, dto, SecurityUtils.usuarioActual());
    }

    @GetMapping("/resenas/{id}")
    public ResenaDetailDTO obtener(@PathVariable Long id) {
        return resenaService.obtener(id);
    }

    @DeleteMapping("/resenas/{id}")
    @PreAuthorize("hasAnyRole('CLIENTE','ADMIN')")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        resenaService.eliminar(id, SecurityUtils.usuarioActual());
        return ResponseEntity.noContent().build();
    }
}