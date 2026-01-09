package com.example.biblioteca_digital.modelos;

/*
 *Clase de libros favoritos de un usuario
 */
public class Favoritos {

    /*
     *Atributos de un libro favorito
     */
    private int id, id_libro, id_usuario;
    Libro libro;
    Usuario usuario;

    /*
     *Constructor vacío de un libro favorito
     */
    public Favoritos() {}

    /*
     *Constructor de libro favorito con atributos
     *@param id
     *@param id_libro
     *@param id_usuario
     */
    public Favoritos(int id, int id_libro, int id_usuario) {
        this.id = id;
        this.id_libro = id_libro;
        this.id_usuario = id_usuario;
    }

    /**
     *Constructor de libro favorito con los objetos
     *@param id
     *@param id_libro
     *@param id_usuario
     */
    public Favoritos(int id, int id_libro, int id_usuario, Libro libro, Usuario usuario) {
        this.id = id;
        this.id_libro = id_libro;
        this.id_usuario = id_usuario;
        this.usuario = usuario;
        this.libro = libro;
    }

    /**
     *Getters de los atributos de un libro favorito
     *@return
     */
    public int getId() { return id; }
    public int getId_libro() { return id_libro; }
    public int getId_usuario() { return id_usuario; }
    public Libro getLibro() { return libro; }
    public Usuario getUsuario() { return usuario; }

    /*
     *Setters de los atributos de un libro favorito
     */
    public void setId(int id) { this.id = id; }
    public void setId_libro(int id_libro) { this.id_libro = id_libro; }
    public void setId_usuario(int id_usuario) { this.id_usuario = id_usuario; }
    public void setLibro(Libro libro) { this.libro = libro; }
    public void setUsuario(Usuario usuario) { this.usuario = usuario; }

    /*
     *ToString para imprimir la clase y sus atributos
     *@return
     */
    @Override
    public String toString() {
        return "Favoritos{" +
                "id=" + id +
                ", libro=" + libro +
                ", id_libro=" + id_libro +
                ", usuario=" + usuario +
                ", id_usuario=" + id_usuario +
                ", libro=" + (libro != null ? libro.getTitulo() : "null") +
                ", usuario=" + (usuario != null ? usuario.getNombre() : "null") +
                '}';
    }
}
