package com.example.biblioteca_digital.modelos;
/*
 *Imports de la clase préstamo
 */
import java.time.LocalDate;
import java.util.Date;

import static com.example.biblioteca_digital.modelos.Estado.Activo;

/*
 *Clase préstamo de un libro
 */
public class Prestamo
{
    /*
     *Atributos de un préstamo
     */
    Libro libro;
    Usuario usuario;
    int id;
    int id_usuario;
    int id_libro;
    Date fecha_inicio, fecha_fin;
    Estado estado;
    /*
     *Constructor vacío de préstamo
    */
    public Prestamo() {
    }
    /*
     *Constructor de préstamo con atributos
     *@param ID
     *@param ID_Usuario
     *@param ID_Libro
     *@param Fecha_Inicio
     *@param Fecha_Fin
     *@param Estado
     */
    public Prestamo(int id, int id_usuario, int id_libro, Date fecha_inicio, Date fecha_fin, Estado estado) {
        this.id = id;
        this.id_usuario = id_usuario;
        this.id_libro = id_libro;
        this.fecha_inicio = fecha_inicio;
        this.fecha_fin = fecha_fin;
        this.estado = estado;
    }

    /*
     *Getters de los atributos de un prestamo
     *@return
     */
    public int getId() {return id;}
    public int getId_usuario() {return id_usuario;}
    public int getId_libro() {return id_libro;}
    public Date getFecha_inicio() {return fecha_inicio;}
    public Date getFecha_fin() {return fecha_fin;}
    public String getEstado() {return estado.toString();}
    public Libro getLibro() {return libro;}


    /*
     *Setters de los atributos de un prestamo
     */
    public void setId(int id) {this.id = id;}
    public void setId_usuario(int id_usuario) {this.id_usuario = id_usuario;}
    public void setId_libro(int id_libro) {this.id_libro = id_libro;}
    public void setFecha_inicio(Date fecha_inicio) {this.fecha_inicio = fecha_inicio;}
    public void setFecha_fin(Date fecha_fin) {this.fecha_fin = fecha_fin;}
    public void setEstado(String estado) {this.estado = Estado.valueOf(estado);}
    public void setLibro(Libro libro) {this.libro = libro;}

    public boolean estaActivo()
    {
        boolean elObjetoEstaActivo = this.estado.equals(Activo);
        java.time.Instant instant = fecha_fin.toInstant();
        java.time.LocalDate fechaFinLocalDate = instant.atZone(java.time.ZoneId.systemDefault()).toLocalDate();
        return elObjetoEstaActivo && java.time.LocalDate.now().isBefore(fechaFinLocalDate.plusDays(1));
    }
    /*
     *ToString para imprimir la clase y sus atributos
     *@return
     */
    @Override
    public String toString() {
        return "Prestamo=\n" +
                "[ID=" + id +
                "]\n[ID de Usuario=" + id_usuario +
                "]\n[ID de Libro=" + id_libro +
                "]\n[Fecha de Inicio=" + fecha_inicio +
                "]\n[Fecha de Fin=" + fecha_fin +
                "]\n[Estado=" + estado +
                "]";
    }
}
