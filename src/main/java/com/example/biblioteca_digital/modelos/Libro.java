package com.example.biblioteca_digital.modelos;
/*
 *Imports de la clase libro
 */
import java.util.Date;

/*
 *Clase libro
 */
public class Libro {

    /*
     *Atributos de un libro
     */
    private int id, cantidad;
    private String titulo, autor, genero, isbn, foto, descripcion;
    private boolean disponible;

    /*
     *Constructor vacío de libro
     */
    public Libro() {}

    /*
     *Constructor de libro con atributos
     *@param id
     *@param titulo
     *@param autor
     *@param genero
     *@param isbn
     *@param foto
     *@param cantidad
     *@param disponible
     */
    public Libro (int id ,String titulo, String autor, String genero, String descripcion, String isbn, String foto, int cantidad, boolean disponible) {

        this.id = id;
        this.titulo = titulo;
        this.autor = autor;
        this.genero =  genero;
        this.descripcion = descripcion;
        this.isbn = isbn;
        this.foto = foto;
        this.cantidad = cantidad;
        this.disponible = disponible;
    }

    /*
     *Getters de los atributos de un libro
     *@return
     */
    public int getId() { return id; }
    public String getTitulo() { return titulo; }
    public String getAutor() { return autor; }
    public String getGenero() { return genero; }
    public String getDescripcion() { return descripcion; }
    public String getIsbn() { return isbn; }
    public String getFoto() { return foto; }
    public int getCantidad() { return cantidad; }
    public boolean getDisponible() { return disponible; }

    /*
     *Setters de los atributos de un libro
     */
    public void setId(int id) { this.id = id; }
    public void setTitulo(String titulo) { this.titulo = titulo; }
    public void setAutor(String autor) { this.autor = autor; }
    public void setGenero(String genero) { this.genero = genero; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public void setIsbn(String isbn) { this.isbn = isbn; }
    public void setFoto(String foto) { this.foto = foto; }
    public void setCantidad(int cantidad) { this.cantidad = cantidad; }
    public void setDisponible(boolean disponible) { this.disponible = disponible; }

    /*
     *ToString para imprimir la clase y sus atributos
     *@return
     */

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", cantidad=" + cantidad +
                ", titulo='" + titulo + '\'' +
                ", autor='" + autor + '\'' +
                ", genero='" + genero + '\'' +
                ", genero='" + descripcion + '\'' +
                ", isbn='" + isbn + '\'' +
                ", foto='" + foto + '\'' +
                ", disponible=" + disponible +
                '}';
    }
}
