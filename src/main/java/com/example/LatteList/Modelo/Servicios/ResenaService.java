package com.example.LatteList.Modelo.Servicios;

import com.example.LatteList.Modelo.Excepciones.ConflictException;
import com.example.LatteList.Modelo.Entidades.Cafe;
import com.example.LatteList.Modelo.Entidades.Resena;
import com.example.LatteList.Modelo.Entidades.Usuario;
import com.example.LatteList.Modelo.Enum.TipoDeUsuario;
import com.example.LatteList.Modelo.Excepciones.ForbiddenOperationException;
import com.example.LatteList.Modelo.Excepciones.ResourceNotFoundException;
import com.example.LatteList.Modelo.Repositorios.CafeRepository;
import com.example.LatteList.Modelo.Repositorios.ResenaRepository;
import com.example.LatteList.Modelo.Repositorios.UserRepository;
import com.example.LatteList.Vista.ResenaDTOs.ResenaDetailDTO;
import com.example.LatteList.Vista.ResenaDTOs.ResenaListDTO;
import com.example.LatteList.Vista.ResenaDTOs.ResenaRequestDTO;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final CafeRepository cafeRepository;
    private final UserRepository userRepository;

    public ResenaService(ResenaRepository resenaRepository,
                         CafeRepository cafeRepository,
                         UserRepository userRepository) {
        this.resenaRepository = resenaRepository;
        this.cafeRepository = cafeRepository;
        this.userRepository = userRepository;
    }

    /** RF8: un CLIENTE deja una resena en un cafe. */
    @Transactional
    public ResenaDetailDTO crear(Long cafeId, ResenaRequestDTO dto, Usuario actor) {
        Cafe cafe = cafeRepository.findById(cafeId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el café con id " + cafeId));
        if (resenaRepository.existsByUsuarioIdAndCafeId(actor.getId(), cafeId)) {
            throw new ConflictException("Ya reseñaste este café. Podés editar tu reseña desde Mis reseñas.");
        }
        Usuario usuario = userRepository.findById(actor.getId())
                .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario autenticado"));

        Resena resena = new Resena();
        resena.setPuntuacionGeneral(dto.puntuacionGeneral());
        resena.setPuntuacionPrecio(dto.puntuacionPrecio());
        resena.setPuntuacionAtencion(dto.puntuacionAtencion());
        resena.setComentario(dto.comentario().trim());
        resena.setCafe(cafe);
        resena.setUsuario(usuario);

        return ResenaDetailDTO.from(resenaRepository.save(resena));
    }

    /** RF11: todas las resenas de un cafe. */
    @Transactional(readOnly = true)
    public List<ResenaListDTO> listarPorCafe(Long cafeId) {
        if (!cafeRepository.existsById(cafeId)) {
            throw new ResourceNotFoundException("No existe el café con id " + cafeId);
        }
        return resenaRepository.findByCafeIdOrderByFechaDescIdDesc(cafeId).stream()
                .map(ResenaListDTO::from)
                .toList();
    }

    /** RF9: las resenas del cliente autenticado. */
    @Transactional(readOnly = true)
    public List<ResenaListDTO> misResenas(Long usuarioId) {
        return resenaRepository.findByUsuarioIdOrderByFechaDescIdDesc(usuarioId).stream()
                .map(ResenaListDTO::from)
                .toList();
    }

    /** RF26: reseñas recibidas en los cafés del dueño autenticado. */
    @Transactional(readOnly = true)
    public List<ResenaListDTO> recibidas(Long duenioId) {
        return resenaRepository.findByCafeDuenioIdOrderByFechaDescIdDesc(duenioId).stream()
                .map(ResenaListDTO::from)
                .toList();
    }

    /** RF27: el ADMIN ve todas las reseñas, filtrando por café y/o rango de puntaje general. */
    @Transactional(readOnly = true)
    public List<ResenaListDTO> buscar(Long cafeId, Integer puntajeMin, Integer puntajeMax) {
        return resenaRepository.buscar(cafeId, puntajeMin, puntajeMax).stream()
                .map(ResenaListDTO::from)
                .toList();
    }

    /** RF23: solo el autor puede editar su reseña. */
    @Transactional
    public ResenaDetailDTO actualizar(Long id, ResenaRequestDTO dto, Usuario actor) {
        Resena resena = buscar(id);
        if (!resena.getUsuario().getId().equals(actor.getId())) {
            throw new ForbiddenOperationException("Solo el autor puede editar su reseña");
        }
        resena.setPuntuacionGeneral(dto.puntuacionGeneral());
        resena.setPuntuacionPrecio(dto.puntuacionPrecio());
        resena.setPuntuacionAtencion(dto.puntuacionAtencion());
        resena.setComentario(dto.comentario().trim());
        return ResenaDetailDTO.from(resenaRepository.save(resena));
    }

    @Transactional(readOnly = true)
    public ResenaDetailDTO obtener(Long id) {
        return ResenaDetailDTO.from(buscar(id));
    }

    /** RF8 y RF22: la baja la hace el autor de la resena o un ADMIN. */
    @Transactional
    public void eliminar(Long id, Usuario actor) {
        Resena resena = buscar(id);
        boolean esAutor = resena.getUsuario().getId().equals(actor.getId());
        boolean esAdmin = actor.getTipoDeUsuario() == TipoDeUsuario.ADMIN;
        if (!esAutor && !esAdmin) {
            throw new ForbiddenOperationException("Solo el autor de la reseña o un ADMIN pueden eliminarla");
        }
        resenaRepository.delete(resena);
    }

    private Resena buscar(Long id) {
        return resenaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe la reseña con id " + id));
    }
}

