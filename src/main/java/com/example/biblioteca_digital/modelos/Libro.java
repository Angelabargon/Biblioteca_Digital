package com.example.biblioteca_digital.modelos;

import java.util.Date;

public class Libro {

    int id;
    String nombre;
    String correo;
    String contraseña;
    Enum rol;
    Date fecha_registro;

    //Constructor vacio
    public void Libro() {

    }

    //Constructor
    public void Libro (int id ,String nombre, String correo, String contraseña, Enum rol, Date fecha_registro) {

        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contraseña =  contraseña;
        this.rol = rol;
        this.fecha_registro = fecha_registro;

    }

    //Getters
    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getContraseña() {
        return contraseña;
    }

    public String getRol() {
        return rol;
    }

    public Date getFecha_registro() {
        return fecha_registro;
    }


    //Setters

    public void setId(int id) {
        this.id = id;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public void setContraseña(String contraseña) {
        this.contraseña = contraseña;
    }

    public void setRol(Enum rol) {
        this.rol = rol;
    }

    public void setFecha_registro(Date fecha_registro) {
        this.fecha_registro = fecha_registro;
    }

    //toString

    @Override
    public String toString() {
        return "Libro{" +
                "id=" + id +
                ", nombre='" + nombre + '\'' +
                ", correo='" + correo + '\'' +
                ", contraseña='" + contraseña + '\'' +
                ", rol=" + rol +
                ", fecha_registro=" + fecha_registro +
                '}';
    }
}
