package com.example.biblioteca_digital.modelos;

import java.time.LocalDate;

public class Prestamo {

    private int id;
    private LocalDate fecha_inicio;
    private LocalDate fecha_fin;
    private Estado estado;

    private Usuario usuario;
    private Libro libro;

    public Prestamo() {}

    public Prestamo(int id, LocalDate fecha_inicio, LocalDate fecha_fin, Estado estado) {
        this.id = id;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.estado = estado;
    }

    // ---------------------------------
    // GETTERS NORMALES
    // ---------------------------------

    public int getId() { return id; }
    public LocalDate getFecha_inicio() { return fecha_inicio; }
    public LocalDate getFecha_fin() { return fecha_fin; }
    public String getEstado() { return estado.toString(); }

    public Usuario getUsuario() { return usuario; }
    public Libro getLibro() { return libro; }

    // ---------------------------------
    // SETTERS NORMALES
    // ---------------------------------

    public void setId(int id) { this.id = id; }
    public void setFecha_inicio(LocalDate fecha_inicio) { this.fecha_inicio = fecha_inicio; }
    public void setFecha_fin(LocalDate fecha_fin) { this.fecha_fin = fecha_fin; }
    public void setEstado(String estado) { this.estado = Estado.valueOf(estado); }

    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setLibro(Libro libro) { this.libro = libro; }
}

