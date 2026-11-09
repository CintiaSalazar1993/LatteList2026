package com.example.LatteList.Modelo.Servicios;

import com.example.LatteList.Modelo.Entidades.Cafe;
import com.example.LatteList.Modelo.Entidades.Usuario;
import com.example.LatteList.Modelo.Enum.CostoPromedio;
import com.example.LatteList.Modelo.Enum.Etiquetas;
import com.example.LatteList.Modelo.Enum.TipoDeUsuario;
import com.example.LatteList.Modelo.Excepciones.BadRequestException;
import com.example.LatteList.Modelo.Excepciones.ForbiddenOperationException;
import com.example.LatteList.Modelo.Excepciones.ResourceNotFoundException;
import com.example.LatteList.Modelo.Repositorios.CafeRepository;
import com.example.LatteList.Modelo.Repositorios.ResenaRepository;
import com.example.LatteList.Modelo.Repositorios.UserRepository;
import com.example.LatteList.Vista.CafeDTOs.CafeRequestDTO;
import com.example.LatteList.Vista.CafeDTOs.CafeResponseDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias de CafeService (RF4, RF5 a RF7, RF16 a RF18, RF24). Los repositorios se simulan con Mockito. */
@ExtendWith(MockitoExtension.class)
class CafeServiceTest {

    @Mock
    private CafeRepository cafeRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ResenaRepository resenaRepository;
    @InjectMocks
    private CafeService cafeService;

    // ---------- datos de prueba ----------

    private Usuario usuario(Long id, TipoDeUsuario tipo) {
        Usuario u = new Usuario();
        ReflectionTestUtils.setField(u, "id", id);
        u.setNombre("Luis");
        u.setApellido("Gomez");
        u.setTipoDeUsuario(tipo);
        return u;
    }

    private Cafe cafe(Long id, String nombre, CostoPromedio costo, Usuario duenio) {
        Cafe c = new Cafe();
        ReflectionTestUtils.setField(c, "id", id);
        c.setNombre(nombre);
        c.setDireccion("Colon 3231");
        c.setCostoPromedio(costo);
        c.setEtiquetas(new HashSet<>(Set.of(Etiquetas.WIFI)));
        c.setDuenio(duenio);
        return c;
    }

    private CafeRequestDTO dto(Long idDuenio) {
        return new CafeRequestDTO("  Cafe Nuevo  ", "Colon 3231", CostoPromedio.$$, null, null,
                Set.of(Etiquetas.WIFI), idDuenio);
    }

    // ---------- consultas (RF4, RF17) ----------

    @Test
    void obtener_devuelveLaFichaConSuPromedio() {
        Usuario duenio = usuario(20L, TipoDeUsuario.DUENIO);
        when(cafeRepository.findById(1L)).thenReturn(Optional.of(cafe(1L, "Cafe A", CostoPromedio.$, duenio)));
        when(resenaRepository.promedioGeneral(1L)).thenReturn(4.256);
        when(resenaRepository.countByCafeId(1L)).thenReturn(3L);

        CafeResponseDTO resultado = cafeService.obtener(1L);

        assertEquals("Cafe A", resultado.nombre());
        assertEquals(4.26, resultado.puntuacionPromedio());
        assertEquals(3L, resultado.cantidadResenas());
    }

    @Test
    void obtener_cafeInexistente_lanzaResourceNotFound() {
        when(cafeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cafeService.obtener(99L));
    }

    @Test
    void misCafes_devuelveLosCafesDelDuenio() {
        Usuario duenio = usuario(20L, TipoDeUsuario.DUENIO);
        when(cafeRepository.findByDuenioIdOrderByNombreAsc(20L))
                .thenReturn(List.of(cafe(1L, "Cafe A", CostoPromedio.$, duenio)));

        assertEquals(1, cafeService.misCafes(20L).size());
    }

    // ---------- listar y ordenar (RF5, RF6, RF7) ----------

    @Test
    void listar_ordenaPorPuntuacionDeMayorAMenor() {
        Usuario duenio = usuario(20L, TipoDeUsuario.DUENIO);
        Cafe a = cafe(1L, "Cafe A", CostoPromedio.$, duenio);
        Cafe b = cafe(2L, "Cafe B", CostoPromedio.$, duenio);
        when(cafeRepository.findAll(ArgumentMatchers.<Specification<Cafe>>any(), any(Sort.class)))
                .thenReturn(List.of(a, b));
        when(resenaRepository.promedioGeneral(1L)).thenReturn(2.0);
        when(resenaRepository.promedioGeneral(2L)).thenReturn(4.5);

        List<CafeResponseDTO> resultado = cafeService.listar(null, null, null, "puntuacion");

        assertEquals("Cafe B", resultado.get(0).nombre());
        assertEquals("Cafe A", resultado.get(1).nombre());
    }

    @Test
    void listar_ordenaPorCostoDeMenorAMayor() {
        Usuario duenio = usuario(20L, TipoDeUsuario.DUENIO);
        Cafe caro = cafe(1L, "Cafe Caro", CostoPromedio.$$$, duenio);
        Cafe barato = cafe(2L, "Cafe Barato", CostoPromedio.$, duenio);
        when(cafeRepository.findAll(ArgumentMatchers.<Specification<Cafe>>any(), any(Sort.class)))
                .thenReturn(List.of(caro, barato));

        List<CafeResponseDTO> resultado = cafeService.listar(null, null, null, "costo");

        assertEquals("Cafe Barato", resultado.get(0).nombre());
    }

    @Test
    void listar_sinOrdenMantieneElOrdenAlfabeticoDelRepositorio() {
        Usuario duenio = usuario(20L, TipoDeUsuario.DUENIO);
        Cafe a = cafe(1L, "Cafe A", CostoPromedio.$$$, duenio);
        Cafe b = cafe(2L, "Cafe B", CostoPromedio.$, duenio);
        when(cafeRepository.findAll(ArgumentMatchers.<Specification<Cafe>>any(), any(Sort.class)))
                .thenReturn(List.of(a, b));

        List<CafeResponseDTO> resultado = cafeService.listar("cafe", CostoPromedio.$, List.of(Etiquetas.WIFI), null);

        assertEquals("Cafe A", resultado.get(0).nombre());
        assertEquals(2, resultado.size());
    }

    // ---------- crear (RF16 y RF18) ----------

    @Test
    void crear_unDuenioCreaSuPropioCafe() {
        Usuario duenio = usuario(20L, TipoDeUsuario.DUENIO);
        when(userRepository.findById(20L)).thenReturn(Optional.of(duenio));
        when(cafeRepository.save(any(Cafe.class))).thenAnswer(inv -> inv.getArgument(0));

        CafeResponseDTO resultado = cafeService.crear(dto(null), duenio);

        assertEquals("Cafe Nuevo", resultado.nombre());
        assertEquals(20L, resultado.duenioId());
    }

    @Test
    void crear_unAdminCreaElCafeANombreDeUnDuenio() {
        Usuario admin = usuario(1L, TipoDeUsuario.ADMIN);
        Usuario duenio = usuario(20L, TipoDeUsuario.DUENIO);
        when(userRepository.findById(20L)).thenReturn(Optional.of(duenio));
        when(cafeRepository.save(any(Cafe.class))).thenAnswer(inv -> inv.getArgument(0));

        CafeResponseDTO resultado = cafeService.crear(dto(20L), admin);

        assertEquals(20L, resultado.duenioId());
    }

    @Test
    void crear_unAdminSinIdDuenio_lanzaBadRequest() {
        Usuario admin = usuario(1L, TipoDeUsuario.ADMIN);

        assertThrows(BadRequestException.class, () -> cafeService.crear(dto(null), admin));
        verify(cafeRepository, never()).save(any(Cafe.class));
    }

    @Test
    void crear_unAdminConUnUsuarioQueNoEsDuenio_lanzaBadRequest() {
        Usuario admin = usuario(1L, TipoDeUsuario.ADMIN);
        Usuario cliente = usuario(30L, TipoDeUsuario.CLIENTE);
        when(userRepository.findById(30L)).thenReturn(Optional.of(cliente));

        assertThrows(BadRequestException.class, () -> cafeService.crear(dto(30L), admin));
    }

    @Test
    void crear_unAdminConDuenioInexistente_lanzaResourceNotFound() {
        Usuario admin = usuario(1L, TipoDeUsuario.ADMIN);
        when(userRepository.findById(77L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> cafeService.crear(dto(77L), admin));
    }

    // ---------- actualizar (RF18) ----------

    @Test
    void actualizar_elDuenioModificaSuCafe() {
        Usuario duenio = usuario(20L, TipoDeUsuario.DUENIO);
        when(cafeRepository.findById(1L)).thenReturn(Optional.of(cafe(1L, "Cafe A", CostoPromedio.$, duenio)));
        when(cafeRepository.save(any(Cafe.class))).thenAnswer(inv -> inv.getArgument(0));

        CafeResponseDTO resultado = cafeService.actualizar(1L, dto(null), duenio);

        assertEquals("Cafe Nuevo", resultado.nombre());
        assertEquals(CostoPromedio.$$, resultado.costoPromedio());
    }

    @Test
    void actualizar_otroDuenio_lanzaForbidden() {
        Usuario duenio = usuario(20L, TipoDeUsuario.DUENIO);
        Usuario otro = usuario(21L, TipoDeUsuario.DUENIO);
        when(cafeRepository.findById(1L)).thenReturn(Optional.of(cafe(1L, "Cafe A", CostoPromedio.$, duenio)));

        assertThrows(ForbiddenOperationException.class, () -> cafeService.actualizar(1L, dto(null), otro));
        verify(cafeRepository, never()).save(any(Cafe.class));
    }

    // ---------- eliminar (RF16 y RF18) ----------

    @Test
    void eliminar_elDuenioPuedeBorrarSuCafe() {
        Usuario duenio = usuario(20L, TipoDeUsuario.DUENIO);
        Cafe cafe = cafe(1L, "Cafe A", CostoPromedio.$, duenio);
        when(cafeRepository.findById(1L)).thenReturn(Optional.of(cafe));

        cafeService.eliminar(1L, duenio);

        verify(cafeRepository).delete(cafe);
    }

    @Test
    void eliminar_unAdminPuedeBorrarCualquierCafe() {
        Usuario duenio = usuario(20L, TipoDeUsuario.DUENIO);
        Usuario admin = usuario(1L, TipoDeUsuario.ADMIN);
        Cafe cafe = cafe(1L, "Cafe A", CostoPromedio.$, duenio);
        when(cafeRepository.findById(1L)).thenReturn(Optional.of(cafe));

        cafeService.eliminar(1L, admin);

        verify(cafeRepository).delete(cafe);
    }

    @Test
    void eliminar_otroUsuario_lanzaForbidden() {
        Usuario duenio = usuario(20L, TipoDeUsuario.DUENIO);
        Usuario otro = usuario(30L, TipoDeUsuario.CLIENTE);
        when(cafeRepository.findById(1L)).thenReturn(Optional.of(cafe(1L, "Cafe A", CostoPromedio.$, duenio)));

        assertThrows(ForbiddenOperationException.class, () -> cafeService.eliminar(1L, otro));
        verify(cafeRepository, never()).delete(any(Cafe.class));
    }

    // ---------- cafe sorpresa (RF24) ----------

    @Test
    void aleatorio_sinCafesQueCumplanLosFiltros_lanzaResourceNotFound() {
        when(cafeRepository.findAll(ArgumentMatchers.<Specification<Cafe>>any())).thenReturn(List.of());

        assertThrows(ResourceNotFoundException.class, () -> cafeService.aleatorio(null, null, null));
    }

    @Test
    void aleatorio_conUnSoloCafeDevuelveEseCafe() {
        Usuario duenio = usuario(20L, TipoDeUsuario.DUENIO);
        when(cafeRepository.findAll(ArgumentMatchers.<Specification<Cafe>>any()))
                .thenReturn(List.of(cafe(1L, "Cafe A", CostoPromedio.$, duenio)));

        CafeResponseDTO resultado = cafeService.aleatorio("cafe", null, null);

        assertEquals("Cafe A", resultado.nombre());
    }
}
