package com.example.LatteList.Modelo.Servicios;

import com.example.LatteList.Modelo.Entidades.Cafe;
import com.example.LatteList.Modelo.Entidades.ListaDeCafe;
import com.example.LatteList.Modelo.Entidades.Usuario;
import com.example.LatteList.Modelo.Excepciones.BadRequestException;
import com.example.LatteList.Modelo.Excepciones.ConflictException;
import com.example.LatteList.Modelo.Excepciones.ForbiddenOperationException;
import com.example.LatteList.Modelo.Excepciones.ResourceNotFoundException;
import com.example.LatteList.Modelo.Repositorios.CafeRepository;
import com.example.LatteList.Modelo.Repositorios.ListaDeCafeRepository;
import com.example.LatteList.Modelo.Repositorios.UserRepository;
import com.example.LatteList.Vista.CafeDTOs.CafeResumenDTO;
import com.example.LatteList.Vista.ListaDTOs.ListaRequestDTO;
import com.example.LatteList.Vista.ListaDTOs.ListaResponseDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Random;

@Service
public class ListaDeCafeService {

    private final ListaDeCafeRepository listaRepository;
    private final CafeRepository cafeRepository;
    private final UserRepository userRepository;
    private final Random random = new Random();

    public ListaDeCafeService(ListaDeCafeRepository listaRepository,
                              CafeRepository cafeRepository,
                              UserRepository userRepository) {
        this.listaRepository = listaRepository;
        this.cafeRepository = cafeRepository;
        this.userRepository = userRepository;
    }

    /** RF12: listas del cliente autenticado. */
    @Transactional(readOnly = true)
    public List<ListaResponseDTO> listar(Long usuarioId) {
        return listaRepository.findByUsuarioIdOrderByFechaCreacionAscIdAsc(usuarioId).stream()
                .map(ListaResponseDTO::from)
                .toList();
    }

    @Transactional(readOnly = true)
    public ListaResponseDTO obtener(Long listaId, Long usuarioId) {
        return ListaResponseDTO.from(buscarPropia(listaId, usuarioId));
    }

    /** RF10 y RF13: alta de lista. */
    @Transactional
    public ListaResponseDTO crear(ListaRequestDTO dto, Long usuarioId) {
        Usuario usuario = userRepository.findById(usuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario autenticado"));
        ListaDeCafe lista = new ListaDeCafe();
        lista.setNombre(dto.nombre().trim());
        lista.setUsuario(usuario);
        return ListaResponseDTO.from(listaRepository.save(lista));
    }

    /** RF13: modificacion (renombrar). */
    @Transactional
    public ListaResponseDTO renombrar(Long listaId, ListaRequestDTO dto, Long usuarioId) {
        ListaDeCafe lista = buscarPropia(listaId, usuarioId);
        if (lista.isFavoritos()) {
            throw new BadRequestException("La lista Favoritos no se puede renombrar");
        }
        lista.setNombre(dto.nombre().trim());
        return ListaResponseDTO.from(listaRepository.save(lista));
    }

    /** RF13: baja de lista (nunca borra los cafes). */
    @Transactional
    public void eliminar(Long listaId, Long usuarioId) {
        ListaDeCafe lista = buscarPropia(listaId, usuarioId);
        if (lista.isFavoritos()) {
            throw new BadRequestException("La lista Favoritos no se puede eliminar");
        }
        listaRepository.delete(lista);
    }

    /** RF10: agrega un cafe a la lista. */
    @Transactional
    public ListaResponseDTO agregarCafe(Long listaId, Long cafeId, Long usuarioId) {
        ListaDeCafe lista = buscarPropia(listaId, usuarioId);
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el café con id " + cafeId));
        boolean yaEsta = lista.getCafes().stream().anyMatch(c -> c.getId().equals(cafeId));
        if (yaEsta) {
            throw new ConflictException("El café ya está en la lista");
        }
        lista.getCafes().add(cafe);
        return ListaResponseDTO.from(listaRepository.save(lista));
    }

    /** RF13: quita un cafe de la lista. */
    @Transactional
    public ListaResponseDTO quitarCafe(Long listaId, Long cafeId, Long usuarioId) {
        ListaDeCafe lista = buscarPropia(listaId, usuarioId);
        boolean quitado = lista.getCafes().removeIf(c -> c.getId().equals(cafeId));
        if (!quitado) {
            throw new ResourceNotFoundException("Ese café no está en la lista");
        }
        return ListaResponseDTO.from(listaRepository.save(lista));
    }

    /** RF24: la "ruleta" - sugiere un cafe al azar dentro de una lista. */
    @Transactional(readOnly = true)
    public CafeResumenDTO aleatorio(Long listaId, Long usuarioId) {
        ListaDeCafe lista = buscarPropia(listaId, usuarioId);
        if (lista.getCafes().isEmpty()) {
            throw new BadRequestException("La lista no tiene cafés para sortear");
        }
        Cafe elegido = lista.getCafes().get(random.nextInt(lista.getCafes().size()));
        return CafeResumenDTO.from(elegido);
    }

    private ListaDeCafe buscarPropia(Long listaId, Long usuarioId) {
        ListaDeCafe lista = listaRepository.findById(listaId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la lista con id " + listaId));
        if (!lista.getUsuario().getId().equals(usuarioId)) {
            throw new ForbiddenOperationException("Esa lista pertenece a otro usuario");
        }
        return lista;
    }
}