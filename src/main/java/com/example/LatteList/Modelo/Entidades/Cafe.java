package com.example.LatteList.Modelo.Entidades;

import com.example.LatteList.Modelo.Enum.CostoPromedio;
import com.example.LatteList.Modelo.Enum.Etiquetas;

import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * MODELO: cafeteria. Pertenece a un DUENIO y puede tener muchas resenas.
 */
@Entity
@Table(name = "cafes")
public class Cafe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String direccion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CostoPromedio costoPromedio;

    // El logo puede ser un link (https://...) o la imagen misma en base64 (data:image/...),
    // por eso necesita una columna grande. OJO: en una base ya creada hay que correr una vez
    // ALTER TABLE cafes MODIFY logo MEDIUMTEXT;  (ddl-auto=update no cambia columnas existentes)
    @Column(columnDefinition = "MEDIUMTEXT")
    private String logo;

    private String instagramUrl;

    // Las etiquetas SI se guardan en la BD (tabla cafe_etiquetas)
    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "cafe_etiquetas", joinColumns = @JoinColumn(name = "cafe_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "etiqueta", nullable = false)
    private Set<Etiquetas> etiquetas = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "duenio_id", nullable = false)
    private Usuario duenio;

    @OneToMany(mappedBy = "cafe", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Resena> resenas = new ArrayList<>();

    // Lado inverso de la relacion con ListaDeCafe
    @ManyToMany(mappedBy = "cafes")
    private Set<ListaDeCafe> listas = new HashSet<>();

    /** Antes de borrar un cafe, lo saca de todas las listas que lo contienen. */
    @PreRemove
    protected void quitarDeListas() {
        for (ListaDeCafe lista : new ArrayList<>(listas)) {
            lista.getCafes().remove(this);
        }
        listas.clear();
    }

    public Long getId() { return id; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getDireccion() { return direccion; }
    public void setDireccion(String direccion) { this.direccion = direccion; }

    public CostoPromedio getCostoPromedio() { return costoPromedio; }
    public void setCostoPromedio(CostoPromedio costoPromedio) { this.costoPromedio = costoPromedio; }

    public String getLogo() { return logo; }
    public void setLogo(String logo) { this.logo = logo; }

    public String getInstagramUrl() { return instagramUrl; }
    public void setInstagramUrl(String instagramUrl) { this.instagramUrl = instagramUrl; }

    public Set<Etiquetas> getEtiquetas() { return etiquetas; }
    public void setEtiquetas(Set<Etiquetas> etiquetas) { this.etiquetas = etiquetas; }

    public Usuario getDuenio() { return duenio; }
    public void setDuenio(Usuario duenio) { this.duenio = duenio; }

    public List<Resena> getResenas() { return resenas; }

    public Set<ListaDeCafe> getListas() { return listas; }
}