package com.example.LatteList.Modelo.Validaciones;

import com.example.LatteList.Modelo.Excepciones.ConflictException;
import com.example.LatteList.Modelo.Repositorios.UserRepository;

import org.springframework.stereotype.Component;

/** Reglas de negocio de validacion para usuarios. */
@Component
public class UserValidation {

    private final UserRepository userRepository;

    public UserValidation(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public void validarEmailNoRepetido(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new ConflictException("Ya existe un usuario con ese email");
        }
    }
}