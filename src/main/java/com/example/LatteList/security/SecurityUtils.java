package com.example.LatteList.security;

import com.example.LatteList.Modelo.Entidades.Usuario;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/** Acceso rapido al usuario autenticado en la request actual. */
public final class SecurityUtils {

    private SecurityUtils() {
    }

    public static Usuario usuarioActual() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof CustomUserDetails detalles)) {
            throw new IllegalStateException("No hay un usuario autenticado en el contexto");
        }
        return detalles.getUsuario();
    }
}
