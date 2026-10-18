package com.example.LatteList.Modelo.Entidades;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * MODELO: lista personalizada de cafes de un CLIENTE.
 * Cada cliente tiene automaticamente una lista "Favoritos" (favoritos = true).
 * OJO: sin cascade sobre los cafes, borrar una lista NUNCA borra cafes.
 */
@Entity
@Table(name = "listas_de_cafes")
public class ListaDeCafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 50)
    private String nombre;

    @Column(nullable = false, updatable = false)
    private LocalDate fechaCreacion;

    @Column(nullable = false)
    private boolean favoritos = false;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToMany
    @JoinTable(
            name = "lista_cafe",
            joinColumns = @JoinColumn(name = "lista_id"),
            inverseJoinColumns = @JoinColumn(name = "cafe_id")
    )
    private List<Cafe> cafes = new ArrayList<>();

    @PrePersist
    protected void onCreate() {
        this.fechaCreacion = LocalDate.now();
    }

    public Long getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public LocalDate getFechaCreacion() { return fechaCreacion; }

    public boolean isFavoritos() { return favoritos; }
    public void setFavoritos(boolean favoritos) { this.favoritos = favoritos; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public List<Cafe> getCafes() { return cafes; }
    public void setCafes(List<Cafe> cafes) { this.cafes = cafes; }
}
