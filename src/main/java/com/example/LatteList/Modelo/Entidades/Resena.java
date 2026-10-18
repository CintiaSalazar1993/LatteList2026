package com.example.LatteList.Modelo.Entidades;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * MODELO: resena que un CLIENTE deja sobre un cafe.
 */
@Entity
@Table(name = "resenas")
public class Resena {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Integer puntuacionPrecio;

    @Column(nullable = false)
    private Integer puntuacionAtencion;

    @Column(nullable = false)
    private Integer puntuacionGeneral;

    @Column(nullable = false)
    private String comentario;

    @Column(nullable = false, updatable = false)
    private LocalDate fecha;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "cafe_id", nullable = false)
    private Cafe cafe;

    /** La fecha se carga sola al crear la resena. */
    @PrePersist
    protected void onCreate() {
        this.fecha = LocalDate.now();
    }

    public Long getId() { return id; }

    public Integer getPuntuacionPrecio() { return puntuacionPrecio; }
    public void setPuntuacionPrecio(Integer puntuacionPrecio) { this.puntuacionPrecio = puntuacionPrecio; }

    public Integer getPuntuacionAtencion() { return puntuacionAtencion; }
    public void setPuntuacionAtencion(Integer puntuacionAtencion) { this.puntuacionAtencion = puntuacionAtencion; }

    public Integer getPuntuacionGeneral() { return puntuacionGeneral; }
    public void setPuntuacionGeneral(Integer puntuacionGeneral) { this.puntuacionGeneral = puntuacionGeneral; }

    public String getComentario() { return comentario; }
    public void setComentario(String comentario) { this.comentario = comentario; }

    public LocalDate getFecha() { return fecha; }

    public Usuario getUsuario() { return usuario; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    public Cafe getCafe() { return cafe; }
    public void setCafe(Cafe cafe) { this.cafe = cafe; }
}
