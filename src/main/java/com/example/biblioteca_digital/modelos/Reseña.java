package com.example.biblioteca_digital.modelos;
/*
 *Imports de la clase Reseña
 */
import java.util.Date;
/*
 *Clase Reseña
 */
public class Reseña
{
    /*
     *Atributos de una reseña
     */
    Libro libro;
    Usuario usuario;
    int id;
    int id_libro;
    int id_usuario;
    Date fecha;
    String contenido;
    int calificacion;

    /*
     *Constructor reseña vacío
     */
    public Reseña() {}

    /*
     *Constructor de reseña con atributos:
     *@param ID
     *@param ID_Libro
     *@param ID_Usuario
     *@param Fecha
     *@param Contenido
     *@param Calificacion
     */
    public Reseña(int id, int id_libro, int id_usuario, Date fecha, String contenido, int calificacion) {
        this.id = id;
        this.id_libro = id_libro;
        this.id_usuario = id_usuario;
        this.fecha = fecha;
        this.contenido = contenido;
        this.calificacion = calificacion;
    }

    /*
     *Getters de los atributos
     *@return
     */
    public int getId() {return id;}
    public int getId_libro() {return id_libro;}
    public int getId_usuario() {return id_usuario;}
    public Date getFecha() {return fecha;}
    public String getContenido() {return contenido;}
    public int getCalificacion() {return calificacion;}

    /*
     *Setters de los atributos
     */
    public void setId(int id) {this.id = id;}
    public void setId_libro(int id_libro) {this.id_libro = id_libro;}
    public void setId_usuario(int id_usuario) {this.id_usuario = id_usuario;}
    public void setFecha(Date fecha) {this.fecha = fecha;}
    public void setContenido(String contenido) {this.contenido = contenido;}
    public void setCalificacion(int calificacion) {this.calificacion = calificacion;}

    /*
     *ToString de una reseña
     *@return
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
