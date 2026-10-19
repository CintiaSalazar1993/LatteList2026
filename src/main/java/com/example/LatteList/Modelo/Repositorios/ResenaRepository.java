package com.example.LatteList.Modelo.Repositorios;

import com.example.LatteList.Modelo.Entidades.Resena;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ResenaRepository extends JpaRepository<Resena, Long> {

    List<Resena> findByCafeIdOrderByFechaDescIdDesc(Long cafeId);

    List<Resena> findByUsuarioIdOrderByFechaDescIdDesc(Long usuarioId);

    long countByCafeId(Long cafeId);

    @Query("select avg(r.puntuacionGeneral) from Resena r where r.cafe.id = :cafeId")
    Double promedioGeneral(@Param("cafeId") Long cafeId);
}
