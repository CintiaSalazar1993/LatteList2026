package com.example.LatteList.Modelo.Repositorios;

import com.example.LatteList.Modelo.Entidades.ListaDeCafe;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ListaDeCafeRepository extends JpaRepository<ListaDeCafe, Long> {

    List<ListaDeCafe> findByUsuarioIdOrderByFechaCreacionAscIdAsc(Long usuarioId);
}