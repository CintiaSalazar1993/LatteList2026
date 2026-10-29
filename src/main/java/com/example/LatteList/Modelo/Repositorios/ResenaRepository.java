package com.example.LatteList.Modelo.Repositorios;

import com.example.LatteList.Modelo.Entidades.Resena;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByCafeIdOrderByFechaDescIdDesc(Long cafeId);

    List<Resena> findByUsuarioIdOrderByFechaDescIdDesc(Long usuarioId);

    /** RF26: reseñas de todos los cafés de un dueño. */
    List<Resena> findByCafeDuenioIdOrderByFechaDescIdDesc(Long duenioId);

    /** RF27: búsqueda del ADMIN; cada filtro es opcional (null = no filtra). */
    @Query("select r from Resena r where (:cafeId is null or r.cafe.id = :cafeId) "
            + "and (:min is null or r.puntuacionGeneral >= :min) "
            + "and (:max is null or r.puntuacionGeneral <= :max) "
            + "order by r.fecha desc, r.id desc")
    List<Resena> buscar(@Param("cafeId") Long cafeId,
                        @Param("min") Integer min,
                        @Param("max") Integer max);

    long countByCafeId(Long cafeId);

    boolean existsByUsuarioIdAndCafeId(Long usuarioId, Long cafeId);

    @Query("select avg(r.puntuacionGeneral) from Resena r where r.cafe.id = :cafeId")
    Double promedioGeneral(@Param("cafeId") Long cafeId);

    long countByCafeDuenioId(Long duenioId);

    @Query("select avg(r.puntuacionGeneral) from Resena r")
    Double promedioGlobal();

    @Query("select avg(r.puntuacionGeneral) from Resena r where r.cafe.duenio.id = :duenioId")
    Double promedioDeDuenio(@Param("duenioId") Long duenioId);

    /** [cafeId, cantidad, promedio] de los cafés que tienen reseñas. */
    @Query("select r.cafe.id, count(r), avg(r.puntuacionGeneral) from Resena r group by r.cafe.id")
    List<Object[]> resumenPorCafe();
}