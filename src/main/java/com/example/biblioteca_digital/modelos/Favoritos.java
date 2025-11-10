package com.example.biblioteca_digital.modelos;

public class Favoritos {

    int id;
    Libro libro;
    int id_libro = libro.getId();

    Usuario usuario;
    int id_usuario = usuario.getId();

    //Constructor vacio
    public Favoritos() {
    }

    //Constructor
    public Favoritos(int id, int id_libro, int id_usuario) {
        this.id = id;
        this.id_libro = id_libro;
        this.id_usuario = id_usuario;
    }


    //Getters
    public int getId() {
        return id;
    }

    public int getId_libro() {
        return id_libro;
    }

    public int getId_usuario() {
        return id_usuario;
    }

    //Setters
    public void setId(int id) {
        this.id = id;
    }


    public void setId_libro(int id_libro) {
        this.id_libro = id_libro;
    }

    public void setId_usuario(int id_usuario) {
        this.id_usuario = id_usuario;
    }

    //toString

    @Override
    public String toString() {
        return "Favoritos{" +
                "id=" + id +
                ", libro=" + libro +
                ", id_libro=" + id_libro +
                ", usuario=" + usuario +
                ", id_usuario=" + id_usuario +
                '}';
    }
}
