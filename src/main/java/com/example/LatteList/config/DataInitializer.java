package com.example.LatteList.config;
import com.example.LatteList.Modelo.Entidades.Usuario;
import com.example.LatteList.Modelo.Enum.TipoDeUsuario;
import com.example.LatteList.Modelo.Repositorios.UserRepository;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/** Al arrancar, crea el primer ADMIN si todavia no existe (un ADMIN no puede registrarse por la API). */
@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Value("${admin.email}")
    private String adminEmail;

    @Value("${admin.password}")
    private String adminPassword;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        if (!userRepository.existsByEmail(adminEmail)) {
            Usuario admin = new Usuario();
            admin.setNombre("Administrador");
            admin.setApellido("LatteList");
            admin.setEmail(adminEmail);
            admin.setContrasena(passwordEncoder.encode(adminPassword));
            admin.setTipoDeUsuario(TipoDeUsuario.ADMIN);
            userRepository.save(admin);
        }
    }
}

