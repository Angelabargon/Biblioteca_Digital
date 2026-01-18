package com.example.biblioteca_digital.modelos;

import java.time.LocalDate;
/**
 * Creamos la clase Prestamo que representará la posibilidad de leer los libros para los usuarios.
 */
public class Prestamo {
    /**
     * Atributos de un prestamo:
     *
     * id
     * fecha_inicio
     * fecha_fin
     * estado
     * usuario
     * libro
     */
    private int id;
    private LocalDate fecha_inicio;
    private LocalDate fecha_fin;
    private Estado estado;

    private Usuario usuario;
    private Libro libro;

    /**
     * Constructor prestamo vacío.
     */
    public Prestamo() {}

    /**
     * Constructor de prestamo.
     *
     * @param id
     * @param fecha_inicio
     * @param fecha_fin
     * @param estado
     */
    public Prestamo(int id, LocalDate fecha_inicio, LocalDate fecha_fin, Estado estado) {
        this.id = id;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.estado = estado;
    }

    /**
     * Getters de los atributos.
     */
    public int getId() { return id; }
    public LocalDate getFecha_inicio() { return fecha_inicio; }
    public LocalDate getFecha_fin() { return fecha_fin; }
    public String getEstado() { return estado.toString(); }

    public Usuario getUsuario() { return usuario; }
    public Libro getLibro() { return libro; }

    /**
     * Setters de los atributos.
     */

    public void setId(int id) { this.id = id; }
    public void setFecha_inicio(LocalDate fecha_inicio) { this.fecha_inicio = fecha_inicio; }
    public void setFecha_fin(LocalDate fecha_fin) { this.fecha_fin = fecha_fin; }
    public void setEstado(String estado) { this.estado = Estado.valueOf(estado); }

    public void setUsuario(Usuario usuario) { this.usuario = usuario; }
    public void setLibro(Libro libro) { this.libro = libro; }
}

