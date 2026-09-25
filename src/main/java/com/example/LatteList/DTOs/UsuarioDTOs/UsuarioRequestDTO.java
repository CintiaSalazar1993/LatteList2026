package com.example.LatteList.DTOs.UsuarioDTOs;

import com.example.LatteList.Enums.TipoDeUsuario;
import com.example.LatteList.model.Usuario;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class UsuarioRequestDTO {

    private Long id;

    @NotBlank(message = "El nombre del usuario no puede ser nulo")
    @Size(min = 6, max = 20,message = "El nombre debe contener entre 6 y 20 caracteres")
    private String nombre;

    @NotBlank(message = "El apellido del usuario no puede ser nulo")
    @Size(min = 6, max = 20,message = "El apellido debe contener entre 6 y 20 caracteres")
    private String apellido;

    @Email(message = "El email debe tener un formato válido")
    @NotBlank(message = "El email es obligatorio")
    private String email;

    @NotBlank(message = "La contraseña es obligatoria")
    @Size(min = 6, max = 20,message = "La contraseña debe contener entre 6 y 20 caracteres")
    private String contrasena;

    @NotNull(message = "El tipo de usuario es obligatorio")
    private TipoDeUsuario tipoDeUsuario;

    public UsuarioRequestDTO(Usuario usuario) {
        this.nombre = nombre;
        this.id = id;
        this.apellido = apellido;
        this.email = email;
        this.contrasena = contrasena;
        this.tipoDeUsuario = tipoDeUsuario;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public @NotBlank(message = "El nombre del usuario no puede ser nulo") @Size(min = 6, max = 20, message = "El nombre debe contener entre 6 y 20 caracteres") String getNombre() {
        return nombre;
    }

    public void setNombre(@NotBlank(message = "El nombre del usuario no puede ser nulo") @Size(min = 6, max = 20, message = "El nombre debe contener entre 6 y 20 caracteres") String nombre) {
        this.nombre = nombre;
    }

    public @NotBlank(message = "El apellido del usuario no puede ser nulo") @Size(min = 6, max = 20, message = "El apellido debe contener entre 6 y 20 caracteres") String getApellido() {
        return apellido;
    }

    public void setApellido(@NotBlank(message = "El apellido del usuario no puede ser nulo") @Size(min = 6, max = 20, message = "El apellido debe contener entre 6 y 20 caracteres") String apellido) {
        this.apellido = apellido;
    }

    public @Email(message = "El email debe tener un formato válido") @NotBlank(message = "El email es obligatorio") String getEmail() {
        return email;
    }

    public void setEmail(@Email(message = "El email debe tener un formato válido") @NotBlank(message = "El email es obligatorio") String email) {
        this.email = email;
    }

    public @NotBlank(message = "La contraseña es obligatoria") @Size(min = 6, max = 20, message = "La contraseña debe contener entre 6 y 20 caracteres") String getContrasena() {
        return contrasena;
    }

    public void setContrasena(@NotBlank(message = "La contraseña es obligatoria") @Size(min = 6, max = 20, message = "La contraseña debe contener entre 6 y 20 caracteres") String contrasena) {
        this.contrasena = contrasena;
    }

    public @NotNull(message = "El tipo de usuario es obligatorio") TipoDeUsuario getTipoDeUsuario() {
        return tipoDeUsuario;
    }

    public void setTipoDeUsuario(@NotNull(message = "El tipo de usuario es obligatorio") TipoDeUsuario tipoDeUsuario) {
        this.tipoDeUsuario = tipoDeUsuario;
    }
}
