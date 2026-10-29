package com.example.LatteList.Modelo.Repositorios;

import com.example.LatteList.Modelo.Entidades.Cafe;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CafeRepository extends JpaRepository<Cafe, Long>, JpaSpecificationExecutor<Cafe> {

    List<Cafe> findByDuenioIdOrderByNombreAsc(Long duenioId);

    @Query("select c.costoPromedio, count(c) from Cafe c group by c.costoPromedio")
    List<Object[]> contarPorCosto();

    @Query("select e, count(c) from Cafe c join c.etiquetas e group by e order by count(c) desc")
    List<Object[]> contarPorEtiqueta();
}

