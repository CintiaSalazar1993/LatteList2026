package com.example.LatteList.Modelo.Repositorios;

import com.example.LatteList.Modelo.Entidades.Usuario;

import com.example.LatteList.Modelo.Enum.TipoDeUsuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<Usuario, Long>, JpaSpecificationExecutor<Usuario> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    long countByTipoDeUsuario(TipoDeUsuario tipoDeUsuario);

    List<Usuario> findByFechaAltaGreaterThanEqual(LocalDate desde);
}

