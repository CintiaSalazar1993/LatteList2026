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

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

@Service
public class CafeService {

    private final CafeRepository cafeRepository;
    private final UserRepository userRepository;
    private final ResenaRepository resenaRepository;
    private final Random random = new Random();

    public CafeService(CafeRepository cafeRepository,
                       UserRepository userRepository,
                       ResenaRepository resenaRepository) {
        this.cafeRepository = cafeRepository;
        this.userRepository = userRepository;
        this.resenaRepository = resenaRepository;
    }

    /** RF5, RF6 y RF7: cafes con filtros opcionales por nombre, costo y etiquetas, ordenables por nombre, puntuacion o costo. */
    @Transactional(readOnly = true)
    public List<CafeResponseDTO> listar(String nombre, CostoPromedio costo, List<Etiquetas> etiquetas, String orden) {
        Specification<Cafe> spec = (root, query, cb) -> {
            List<Predicate> filtros = new ArrayList<>();
            if (nombre != null && !nombre.isBlank()) {
                filtros.add(cb.like(cb.lower(root.<String>get("nombre")), "%" + nombre.trim().toLowerCase() + "%"));
            }
            if (costo != null) {
                filtros.add(cb.equal(root.get("costoPromedio"), costo));
            }
            if (etiquetas != null) {
                for (Etiquetas etiqueta : etiquetas) {
                    filtros.add(cb.isMember(etiqueta, root.<Set<Etiquetas>>get("etiquetas")));
                }
            }
            return cb.and(filtros.toArray(new Predicate[0]));
        };
        List<CafeResponseDTO> lista = new ArrayList<>(
                cafeRepository.findAll(spec, Sort.by("nombre")).stream().map(this::aDTO).toList());
        // La lista ya viene ordenada por nombre; el sort de Java es estable, asi que el nombre desempata.
        if ("puntuacion".equals(orden)) {
            lista.sort(java.util.Comparator.comparing(
                    (CafeResponseDTO c) -> c.puntuacionPromedio() == null ? 0.0 : c.puntuacionPromedio()).reversed());
        } else if ("costo".equals(orden)) {
            lista.sort(java.util.Comparator.comparing((CafeResponseDTO c) -> c.costoPromedio().ordinal()));
        }
        return lista;
    }

    /** RF4: ficha de un cafe. */
    @Transactional(readOnly = true)
    public CafeResponseDTO obtener(Long id) {
        return aDTO(buscar(id));
    }

    /** RF17: cafes del dueño autenticado. */
    @Transactional(readOnly = true)
    public List<CafeResponseDTO> misCafes(Long duenioId) {
        return cafeRepository.findByDuenioIdOrderByNombreAsc(duenioId).stream().map(this::aDTO).toList();
    }

    /** RF18 y RF16: un DUENIO crea su cafe; un ADMIN lo crea a nombre de un DUENIO (idDuenio). */
    @Transactional
    public CafeResponseDTO crear(CafeRequestDTO dto, Usuario actor) {
        Usuario duenio;
        if (actor.getTipoDeUsuario() == TipoDeUsuario.ADMIN) {
            if (dto.idDuenio() == null) {
                throw new BadRequestException("Un ADMIN debe indicar el idDuenio del café");
            }
            duenio = userRepository.findById(dto.idDuenio())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario con id " + dto.idDuenio()));
            if (duenio.getTipoDeUsuario() != TipoDeUsuario.DUENIO) {
                throw new BadRequestException("El usuario indicado no es DUENIO");
            }
        } else {
            duenio = userRepository.findById(actor.getId())
                    .orElseThrow(() -> new ResourceNotFoundException("No existe el usuario autenticado"));
        }

        Cafe cafe = new Cafe();
        aplicar(dto, cafe);
        cafe.setDuenio(duenio);
        return aDTO(cafeRepository.save(cafe));
    }

    /** RF18: solo el DUENIO propietario modifica su cafe. */
    @Transactional
    public CafeResponseDTO actualizar(Long id, CafeRequestDTO dto, Usuario actor) {
        Cafe cafe = buscar(id);
        if (!cafe.getDuenio().getId().equals(actor.getId())) {
            throw new ForbiddenOperationException("Solo el dueño del café puede modificarlo");
        }
        aplicar(dto, cafe);
        return aDTO(cafeRepository.save(cafe));
    }

    /** RF16 y RF18: baja por el dueño propietario o por un ADMIN. */
    @Transactional
    public void eliminar(Long id, Usuario actor) {
        Cafe cafe = buscar(id);
        boolean esDuenio = cafe.getDuenio().getId().equals(actor.getId());
        boolean esAdmin = actor.getTipoDeUsuario() == TipoDeUsuario.ADMIN;
        if (!esDuenio && !esAdmin) {
            throw new ForbiddenOperationException("Solo el dueño del café o un ADMIN pueden eliminarlo");
        }
        cafeRepository.delete(cafe);
    }

    /** RF24: sugiere un cafe al azar. */
    @Transactional(readOnly = true)
    public CafeResponseDTO aleatorio() {
        List<Cafe> cafes = cafeRepository.findAll();
        if (cafes.isEmpty()) {
            throw new ResourceNotFoundException("Todavía no hay cafés cargados");
        }
        return aDTO(cafes.get(random.nextInt(cafes.size())));
    }

    // ---------- auxiliares ----------

    private Cafe buscar(Long id) {
        return cafeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("No existe el café con id " + id));
    }

    private void aplicar(CafeRequestDTO dto, Cafe cafe) {
        cafe.setNombre(dto.nombre().trim());
        cafe.setDireccion(dto.direccion().trim());
        cafe.setCostoPromedio(dto.costoPromedio());
        cafe.setLogo(dto.logo());
        cafe.setInstagramUrl(dto.instagramUrl());
        cafe.setEtiquetas(new HashSet<>(dto.etiquetas()));
    }

    private CafeResponseDTO aDTO(Cafe c) {
        Double promedio = resenaRepository.promedioGeneral(c.getId());
        Double redondeado = promedio == null ? null : Math.round(promedio * 100.0) / 100.0;
        return new CafeResponseDTO(
                c.getId(), c.getNombre(), c.getDireccion(), c.getCostoPromedio(),
                c.getLogo(), c.getInstagramUrl(), c.getEtiquetas(),
                c.getDuenio().getId(),
                c.getDuenio().getNombre() + " " + c.getDuenio().getApellido(),
                redondeado,
                resenaRepository.countByCafeId(c.getId()));
    }
}
