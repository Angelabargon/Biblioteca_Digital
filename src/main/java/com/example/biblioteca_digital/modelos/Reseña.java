package com.example.biblioteca_digital.modelos;

/**
 * Imports de la clase Reseña.
 */
import java.util.Date;

/**
 * Creamos la clase Reseña que representará la opinión de los usuarios sobre un libro.
 */
public class Reseña {

    /**
     * Atributos de una reseña:
     *
     * id
     * idLibro
     * idUsuario
     * fecha
     * contenido
     * calificacion
     */
    private int id;
    private int id_libro;
    private int id_usuario;
    private Date fecha;
    private String contenido;
    private int calificacion;

    /**
     * Atributos extra para la interfaz.
     */
    private String nombreUsuario;
    private String tituloLibro;

    /**
     * Constructor reseña vacío
     */
    public Reseña() {}

    /**
     * Constructor de reseña.
     *
     * @param id
     * @param id_libro
     * @param id_usuario
     * @param fecha
     * @param contenido
     * @param calificacion
     */
    public Reseña(int id, int id_libro, int id_usuario, Date fecha, String contenido, int calificacion) {
        this.id = id;
        this.id_libro = id_libro;
        this.id_usuario = id_usuario;
        this.fecha = fecha;
        this.contenido = contenido;
        this.calificacion = calificacion;
    }

    /**
     * Getters de los atributos.
     */
    public int getId() {return id;}
    public int getId_libro() {return id_libro;}
    public int getId_usuario() {return id_usuario;}
    public Date getFecha() {return fecha;}
    public String getContenido() {return contenido;}
    public int getCalificacion() {return calificacion;}

    /**
     * Setters de los atributos.
     */
    public void setId(int id) {this.id = id;}
    public void setId_libro(int id_libro) {this.id_libro = id_libro;}
    public void setId_usuario(int id_usuario) {this.id_usuario = id_usuario;}
    public void setFecha(Date fecha) {this.fecha = fecha;}
    public void setContenido(String contenido) {this.contenido = contenido;}
    public void setCalificacion(int calificacion) {this.calificacion = calificacion;}

    /**
     * Getters y Setters extra.
     */
    public String getNombreUsuario() { return nombreUsuario;}
    public void setNombreUsuario(String nombreUsuario) { this.nombreUsuario = nombreUsuario;}

    public String getTituloLibro() { return tituloLibro;}
    public void setTituloLibro(String tituloLibro) { this.tituloLibro = tituloLibro;}

    /**
     * ToString de una reseña
     */
    @Override
    public String toString() {
        return "Reseña:\n" +
                "[ID: " + id +
                "]\n[ID del libro: " + id_libro +
                "]\n[ID del usuario: " + id_usuario +
                "]\n[Fecha: " + fecha +
                "]\n[Contenido: '" + contenido + '\'' +
                "]\n[Calificación: " + calificacion +
                "]";
    }
}
