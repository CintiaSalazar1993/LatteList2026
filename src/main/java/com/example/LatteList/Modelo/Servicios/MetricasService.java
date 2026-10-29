package com.example.LatteList.Modelo.Servicios;

import com.example.LatteList.Modelo.Entidades.Cafe;
import com.example.LatteList.Modelo.Entidades.Usuario;
import com.example.LatteList.Modelo.Enum.TipoDeUsuario;
import com.example.LatteList.Modelo.Repositorios.CafeRepository;
import com.example.LatteList.Modelo.Repositorios.ResenaRepository;
import com.example.LatteList.Modelo.Repositorios.UserRepository;
import com.example.LatteList.Vista.MetricasDTOs.*;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

/** RF25 (métricas del dueño) y RF28 (métricas globales del admin). */
@Service
public class MetricasService {

    private static final int MESES_ALTAS = 6;
    private static final int TOP_CAFES = 5;
    private static final int TOP_ETIQUETAS = 8;

    private final CafeRepository cafeRepository;
    private final ResenaRepository resenaRepository;
    private final UserRepository userRepository;

    public MetricasService(CafeRepository cafeRepository, ResenaRepository resenaRepository,
                           UserRepository userRepository) {
        this.cafeRepository = cafeRepository;
        this.resenaRepository = resenaRepository;
        this.userRepository = userRepository;
    }

    /** RF25: totales del dueño y posición de cada uno de sus cafés en el ranking general. */
    @Transactional(readOnly = true)
    public MetricasDuenioDTO delDuenio(Long duenioId) {
        List<Resumen> ranking = ranking();
        Map<Long, Integer> posicion = new HashMap<>();
        for (int i = 0; i < ranking.size(); i++) posicion.put(ranking.get(i).cafeId(), i + 1);
        Map<Long, Resumen> porCafe = new HashMap<>();
        ranking.forEach(r -> porCafe.put(r.cafeId(), r));

        List<CafeMetricaDTO> cafes = cafeRepository.findByDuenioIdOrderByNombreAsc(duenioId).stream()
                .map(c -> {
                    Resumen r = porCafe.get(c.getId());
                    return new CafeMetricaDTO(c.getId(), c.getNombre(),
                            r == null ? 0 : r.cantidad(),
                            r == null ? null : redondear(r.promedio()),
                            posicion.get(c.getId()));
                }).toList();

        return new MetricasDuenioDTO(cafes.size(),
                resenaRepository.countByCafeDuenioId(duenioId),
                redondear(resenaRepository.promedioDeDuenio(duenioId)),
                ranking.size(), cafes);
    }

    /** RF28: totales y distribuciones de todo el sistema. */
    @Transactional(readOnly = true)
    public MetricasGlobalesDTO globales() {
        Map<String, Long> porCosto = new LinkedHashMap<>();
        for (Object[] f : cafeRepository.contarPorCosto()) porCosto.put(f[0].toString(), (Long) f[1]);

        Map<String, Long> porEtiqueta = new LinkedHashMap<>();
        for (Object[] f : cafeRepository.contarPorEtiqueta()) {
            if (porEtiqueta.size() == TOP_ETIQUETAS) break;
            porEtiqueta.put(f[0].toString(), (Long) f[1]);
        }

        List<Resumen> ranking = ranking();
        Map<Long, Cafe> cafes = new HashMap<>();
        cafeRepository.findAllById(ranking.stream().limit(TOP_CAFES).map(Resumen::cafeId).toList())
                .forEach(c -> cafes.put(c.getId(), c));
        List<TopCafeDTO> top = ranking.stream().limit(TOP_CAFES)
                .map(r -> new TopCafeDTO(r.cafeId(), cafes.get(r.cafeId()).getNombre(),
                        redondear(r.promedio()), r.cantidad()))
                .toList();

        return new MetricasGlobalesDTO(
                userRepository.count(),
                userRepository.countByTipoDeUsuario(TipoDeUsuario.CLIENTE),
                userRepository.countByTipoDeUsuario(TipoDeUsuario.DUENIO),
                userRepository.countByTipoDeUsuario(TipoDeUsuario.ADMIN),
                cafeRepository.count(), resenaRepository.count(),
                redondear(resenaRepository.promedioGlobal()),
                porCosto, porEtiqueta, top, altas());
    }

    private List<AltaMesDTO> altas() {
        LocalDate primero = LocalDate.now().withDayOfMonth(1).minusMonths(MESES_ALTAS - 1L);
        List<Usuario> usuarios = userRepository.findByFechaAltaGreaterThanEqual(primero);
        List<AltaMesDTO> salida = new ArrayList<>();
        for (int i = 0; i < MESES_ALTAS; i++) {
            LocalDate mes = primero.plusMonths(i);
            long clientes = 0, duenios = 0;
            for (Usuario u : usuarios) {
                if (u.getFechaAlta().getYear() == mes.getYear()
                        && u.getFechaAlta().getMonth() == mes.getMonth()) {
                    if (u.getTipoDeUsuario() == TipoDeUsuario.CLIENTE) clientes++;
                    else if (u.getTipoDeUsuario() == TipoDeUsuario.DUENIO) duenios++;
                }
            }
            salida.add(new AltaMesDTO(String.format("%d-%02d", mes.getYear(), mes.getMonthValue()), clientes, duenios));
        }
        return salida;
    }

    /** Cafés con reseñas, de mejor a peor: promedio descendente y, a igual promedio, más reseñas primero. */
    private List<Resumen> ranking() {
        List<Resumen> lista = new ArrayList<>();
        for (Object[] f : resenaRepository.resumenPorCafe()) {
            lista.add(new Resumen((Long) f[0], (Long) f[1], ((Number) f[2]).doubleValue()));
        }
        lista.sort(Comparator.comparingDouble(Resumen::promedio).reversed()
                .thenComparing(Comparator.comparingLong(Resumen::cantidad).reversed()));
        return lista;
    }

    private static Double redondear(Double v) {
        return v == null ? null : Math.round(v * 100.0) / 100.0;
    }

    private record Resumen(Long cafeId, long cantidad, double promedio) {}
}
