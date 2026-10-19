package com.example.LatteList.Modelo.Repositorios;

import com.example.LatteList.Modelo.Entidades.Cafe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface CafeRepository extends JpaRepository<Cafe, Long>, JpaSpecificationExecutor<Cafe> {

    List<Cafe> findByDuenioIdOrderByNombreAsc(Long duenioId);
}