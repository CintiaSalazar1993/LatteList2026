package com.example.LatteList.Modelo.Entidades;

import com.example.LatteList.Modelo.Enum.TipoDeUsuario;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * MODELO: usuario del sistema (CLIENTE, DUENIO o ADMIN).
 * Es una clase de dominio pura: la adaptacion a Spring Security vive en security/CustomUserDetails.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido;

    @Column(unique = true, nullable = false)
    private String email;

    @Column(nullable = false)
    private String contrasena;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDeUsuario tipoDeUsuario;

    @Column(nullable = false, updatable = false)
    private LocalDate fechaAlta;

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<ListaDeCafe> listasDeCafes = new ArrayList<>();

    @OneToMany(mappedBy = "usuario", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resena> resenas = new ArrayList<>();

    @OneToMany(mappedBy = "duenio", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Cafe> cafes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.fechaAlta = LocalDate.now();
    }

    public Long getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getContrasena() { return contrasena; }
    public void setContrasena(String contrasena) { this.contrasena = contrasena; }

    public TipoDeUsuario getTipoDeUsuario() { return tipoDeUsuario; }
    public void setTipoDeUsuario(TipoDeUsuario tipoDeUsuario) { this.tipoDeUsuario = tipoDeUsuario; }

    public LocalDate getFechaAlta() { return fechaAlta; }

    public List<ListaDeCafe> getListasDeCafes() { return listasDeCafes; }
    public List<Resena> getResenas() { return resenas; }
    public List<Cafe> getCafes() { return cafes; }
}

