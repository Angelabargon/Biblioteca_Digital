package com.example.biblioteca_digital.modelos;

import java.time.LocalDate;

public class Prestamo {

    private int id;
    private int id_usuario;
    private int id_libro;
    private LocalDate fecha_inicio;
    private LocalDate fecha_fin;
    private Estado estado;

    private Usuario usuario;
    private Libro libro;

    public Prestamo() {}

    public Prestamo(int id, int id_usuario, int id_libro, LocalDate fecha_inicio, LocalDate fecha_fin, Estado estado) {
        this.id = id;
        this.id_usuario = id_usuario;
        this.id_libro = id_libro;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.estado = estado;
    }

    // ---------------------------------
    // GETTERS NORMALES
    // ---------------------------------

    public int getId() { return id; }
    public int getId_usuario() { return id_usuario; }
    public int getId_libro() { return id_libro; }
    public LocalDate getFecha_inicio() { return fecha_inicio; }
    public LocalDate getFecha_fin() { return fecha_fin; }
    public String getEstado() { return estado.toString(); }

    public Usuario getUsuario() { return usuario; }
    public Libro getLibro() { return libro; }

    // ---------------------------------
    // SETTERS NORMALES
    // ---------------------------------

    public void setId(int id) { this.id = id; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }
    public void setId_libro(int id_libro) { this.id_libro = id_libro; }
    public void setFecha_inicio(LocalDate fecha_inicio) { this.fecha_inicio = fecha_inicio; }
    public void setFecha_fin(LocalDate fecha_fin) { this.fecha_fin = fecha_fin; }
    public void setEstado(String estado) { this.estado = Estado.valueOf(estado); }

    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setLibro(Libro libro) { this.libro = libro; }
}

