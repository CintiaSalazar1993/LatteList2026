package com.example.LatteList.Controlador;

import com.example.LatteList.Modelo.Servicios.ListaDeCafeService;
import com.example.LatteList.security.SecurityUtils;
import com.example.LatteList.Vista.CafeDTOs.CafeResumenDTO;
import com.example.LatteList.Vista.ListaDTOs.ListaRequestDTO;
import com.example.LatteList.Vista.ListaDTOs.ListaResponseDTO;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/** RF10, RF12, RF13 y RF24 (ruleta). Solo para CLIENTE. */
@RestController
@RequestMapping("/api/listas")
@PreAuthorize("hasRole('CLIENTE')")
public class ListaDeCafeController {

    private final ListaDeCafeService listaService;

    public ListaDeCafeController(ListaDeCafeService listaService) {
        this.listaService = listaService;
    }

    @GetMapping
    public List<ListaResponseDTO> listar() {
        return listaService.listar(SecurityUtils.usuarioActual().getId());
    }

    @GetMapping("/{id}")
    public ListaResponseDTO obtener(@PathVariable Long id) {
        return listaService.obtener(id, SecurityUtils.usuarioActual().getId());
    }

    @PostMapping
    public ResponseEntity<ListaResponseDTO> crear(@Valid @RequestBody ListaRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(listaService.crear(dto, SecurityUtils.usuarioActual().getId()));
    }

    @PutMapping("/{id}")
    public ListaResponseDTO renombrar(@PathVariable Long id, @Valid @RequestBody ListaRequestDTO dto) {
        return listaService.renombrar(id, dto, SecurityUtils.usuarioActual().getId());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        listaService.eliminar(id, SecurityUtils.usuarioActual().getId());
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/cafes/{cafeId}")
    public ListaResponseDTO agregarCafe(@PathVariable Long id, @PathVariable Long cafeId) {
        return listaService.agregarCafe(id, cafeId, SecurityUtils.usuarioActual().getId());
    }

    @DeleteMapping("/{id}/cafes/{cafeId}")
    public ListaResponseDTO quitarCafe(@PathVariable Long id, @PathVariable Long cafeId) {
        return listaService.quitarCafe(id, cafeId, SecurityUtils.usuarioActual().getId());
    }

    @GetMapping("/{id}/aleatorio")
    public CafeResumenDTO aleatorio(@PathVariable Long id) {
        return listaService.aleatorio(id, SecurityUtils.usuarioActual().getId());
    }
}

