package com.example.LatteList.Modelo.Servicios;

import com.example.LatteList.Modelo.Entidades.Cafe;
import com.example.LatteList.Modelo.Entidades.Resena;
import com.example.LatteList.Modelo.Entidades.Usuario;
import com.example.LatteList.Modelo.Enum.TipoDeUsuario;
import com.example.LatteList.Modelo.Excepciones.ConflictException;
import com.example.LatteList.Modelo.Excepciones.ForbiddenOperationException;
import com.example.LatteList.Modelo.Excepciones.ResourceNotFoundException;
import com.example.LatteList.Modelo.Repositorios.CafeRepository;
import com.example.LatteList.Modelo.Repositorios.ResenaRepository;
import com.example.LatteList.Modelo.Repositorios.UserRepository;
import com.example.LatteList.Vista.ResenaDTOs.ResenaDetailDTO;
import com.example.LatteList.Vista.ResenaDTOs.ResenaListDTO;
import com.example.LatteList.Vista.ResenaDTOs.ResenaRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/** Pruebas unitarias de ResenaService (RF8, RF9, RF11, RF22, RF23, RF26, RF27). Los repositorios se simulan con Mockito. */
@ExtendWith(MockitoExtension.class)
class ResenaServiceTest {

    @Mock
    private ResenaRepository resenaRepository;
    @Mock
    private CafeRepository cafeRepository;
    @Mock
    private UserRepository userRepository;
    @InjectMocks
    private ResenaService resenaService;

    // ---------- datos de prueba ----------

    private Usuario usuario(Long id, TipoDeUsuario tipo) {
        Usuario u = new Usuario();
        ReflectionTestUtils.setField(u, "id", id);
        u.setNombre("Ana");
        u.setApellido("Perez");
        u.setTipoDeUsuario(tipo);
        return u;
    }

    private Cafe cafe(Long id, Usuario duenio) {
        Cafe c = new Cafe();
        ReflectionTestUtils.setField(c, "id", id);
        c.setNombre("Cafe de prueba");
        c.setDuenio(duenio);
        return c;
    }

    private Resena resena(Long id, Usuario autor, Cafe cafe) {
        Resena r = new Resena();
        ReflectionTestUtils.setField(r, "id", id);
        r.setUsuario(autor);
        r.setCafe(cafe);
        r.setPuntuacionGeneral(4);
        r.setPuntuacionPrecio(4);
        r.setPuntuacionAtencion(4);
        r.setComentario("Bueno");
        return r;
    }

    private ResenaRequestDTO dto() {
        return new ResenaRequestDTO(5, 4, 3, "  Muy bueno  ");
    }

    // ---------- crear (RF8) ----------

    @Test
    void crear_guardaLaResenaYRecortaElComentario() {
        Usuario cliente = usuario(10L, TipoDeUsuario.CLIENTE);
        Cafe cafe = cafe(1L, usuario(20L, TipoDeUsuario.DUENIO));
        when(cafeRepository.findById(1L)).thenReturn(Optional.of(cafe));
        when(resenaRepository.existsByUsuarioIdAndCafeId(10L, 1L)).thenReturn(false);
        when(userRepository.findById(10L)).thenReturn(Optional.of(cliente));
        when(resenaRepository.save(any(Resena.class))).thenAnswer(inv -> inv.getArgument(0));

        ResenaDetailDTO resultado = resenaService.crear(1L, dto(), cliente);

        assertEquals("Muy bueno", resultado.comentario());
        assertEquals(5, resultado.puntuacionGeneral());
        assertEquals(1L, resultado.cafeId());
        verify(resenaRepository).save(any(Resena.class));
    }

    @Test
    void crear_cafeInexistente_lanzaResourceNotFound() {
        Usuario cliente = usuario(10L, TipoDeUsuario.CLIENTE);
        when(cafeRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> resenaService.crear(99L, dto(), cliente));
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    @Test
    void crear_siYaResenoElCafe_lanzaConflict() {
        Usuario cliente = usuario(10L, TipoDeUsuario.CLIENTE);
        Cafe cafe = cafe(1L, usuario(20L, TipoDeUsuario.DUENIO));
        when(cafeRepository.findById(1L)).thenReturn(Optional.of(cafe));
        when(resenaRepository.existsByUsuarioIdAndCafeId(10L, 1L)).thenReturn(true);

        assertThrows(ConflictException.class, () -> resenaService.crear(1L, dto(), cliente));
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    @Test
    void crear_usuarioInexistente_lanzaResourceNotFound() {
        Usuario cliente = usuario(10L, TipoDeUsuario.CLIENTE);
        Cafe cafe = cafe(1L, usuario(20L, TipoDeUsuario.DUENIO));
        when(cafeRepository.findById(1L)).thenReturn(Optional.of(cafe));
        when(resenaRepository.existsByUsuarioIdAndCafeId(10L, 1L)).thenReturn(false);
        when(userRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> resenaService.crear(1L, dto(), cliente));
    }

    // ---------- consultas (RF9, RF11, RF26, RF27) ----------

    @Test
    void listarPorCafe_devuelveLasResenasDelCafe() {
        Cafe cafe = cafe(1L, usuario(20L, TipoDeUsuario.DUENIO));
        Resena r = resena(5L, usuario(10L, TipoDeUsuario.CLIENTE), cafe);
        when(cafeRepository.existsById(1L)).thenReturn(true);
        when(resenaRepository.findByCafeIdOrderByFechaDescIdDesc(1L)).thenReturn(List.of(r));

        List<ResenaListDTO> resultado = resenaService.listarPorCafe(1L);

        assertEquals(1, resultado.size());
        assertEquals("Bueno", resultado.get(0).comentario());
    }

    @Test
    void listarPorCafe_cafeInexistente_lanzaResourceNotFound() {
        when(cafeRepository.existsById(99L)).thenReturn(false);

        assertThrows(ResourceNotFoundException.class, () -> resenaService.listarPorCafe(99L));
    }

    @Test
    void misResenas_devuelveLasDelUsuario() {
        Usuario cliente = usuario(10L, TipoDeUsuario.CLIENTE);
        Resena r = resena(5L, cliente, cafe(1L, usuario(20L, TipoDeUsuario.DUENIO)));
        when(resenaRepository.findByUsuarioIdOrderByFechaDescIdDesc(10L)).thenReturn(List.of(r));

        assertEquals(1, resenaService.misResenas(10L).size());
    }

    @Test
    void recibidas_devuelveLasDeLosCafesDelDuenio() {
        Usuario duenio = usuario(20L, TipoDeUsuario.DUENIO);
        Resena r = resena(5L, usuario(10L, TipoDeUsuario.CLIENTE), cafe(1L, duenio));
        when(resenaRepository.findByCafeDuenioIdOrderByFechaDescIdDesc(20L)).thenReturn(List.of(r));

        assertEquals(1, resenaService.recibidas(20L).size());
    }

    @Test
    void buscar_filtraPorCafeYPuntaje() {
        Resena r = resena(5L, usuario(10L, TipoDeUsuario.CLIENTE), cafe(1L, usuario(20L, TipoDeUsuario.DUENIO)));
        when(resenaRepository.buscar(1L, 3, 5)).thenReturn(List.of(r));

        assertEquals(1, resenaService.buscar(1L, 3, 5).size());
    }

    @Test
    void obtener_resenaInexistente_lanzaResourceNotFound() {
        when(resenaRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class, () -> resenaService.obtener(99L));
    }

    // ---------- actualizar (RF23) ----------

    @Test
    void actualizar_elAutorModificaSuResena() {
        Usuario autor = usuario(10L, TipoDeUsuario.CLIENTE);
        Resena existente = resena(5L, autor, cafe(1L, usuario(20L, TipoDeUsuario.DUENIO)));
        when(resenaRepository.findById(5L)).thenReturn(Optional.of(existente));
        when(resenaRepository.save(any(Resena.class))).thenAnswer(inv -> inv.getArgument(0));

        ResenaDetailDTO resultado = resenaService.actualizar(5L, dto(), autor);

        assertEquals("Muy bueno", resultado.comentario());
        assertEquals(5, resultado.puntuacionGeneral());
    }

    @Test
    void actualizar_otroUsuario_lanzaForbidden() {
        Usuario autor = usuario(10L, TipoDeUsuario.CLIENTE);
        Usuario otro = usuario(11L, TipoDeUsuario.CLIENTE);
        Resena existente = resena(5L, autor, cafe(1L, usuario(20L, TipoDeUsuario.DUENIO)));
        when(resenaRepository.findById(5L)).thenReturn(Optional.of(existente));

        assertThrows(ForbiddenOperationException.class, () -> resenaService.actualizar(5L, dto(), otro));
        verify(resenaRepository, never()).save(any(Resena.class));
    }

    // ---------- eliminar (RF8 y RF22) ----------

    @Test
    void eliminar_elAutorPuedeBorrarSuResena() {
        Usuario autor = usuario(10L, TipoDeUsuario.CLIENTE);
        Resena existente = resena(5L, autor, cafe(1L, usuario(20L, TipoDeUsuario.DUENIO)));
        when(resenaRepository.findById(5L)).thenReturn(Optional.of(existente));

        resenaService.eliminar(5L, autor);

        verify(resenaRepository).delete(existente);
    }

    @Test
    void eliminar_unAdminPuedeBorrarCualquierResena() {
        Usuario autor = usuario(10L, TipoDeUsuario.CLIENTE);
        Usuario admin = usuario(1L, TipoDeUsuario.ADMIN);
        Resena existente = resena(5L, autor, cafe(1L, usuario(20L, TipoDeUsuario.DUENIO)));
        when(resenaRepository.findById(5L)).thenReturn(Optional.of(existente));

        resenaService.eliminar(5L, admin);

        verify(resenaRepository).delete(existente);
    }

    @Test
    void eliminar_otroClienteNoPuede_lanzaForbidden() {
        Usuario autor = usuario(10L, TipoDeUsuario.CLIENTE);
        Usuario otro = usuario(11L, TipoDeUsuario.CLIENTE);
        Resena existente = resena(5L, autor, cafe(1L, usuario(20L, TipoDeUsuario.DUENIO)));
        when(resenaRepository.findById(5L)).thenReturn(Optional.of(existente));

        assertThrows(ForbiddenOperationException.class, () -> resenaService.eliminar(5L, otro));
        verify(resenaRepository, never()).delete(any(Resena.class));
    }
}
