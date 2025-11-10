package com.example.biblioteca_digital.modelos;

/*
Creamos la clas Usuario que representará a los clientes que entren a esta aplicación.
 */
public class Usuario {

    /*
    Definimos los atributos del usuario.
     */
    private int id;
    private String nombre;
    private String correo;
    private String contrasena;
    private String rol;
    private String fechaRegistro;

    /*
    Creamos el constructor e iniciamos sus atributos.
     */
    public Usuario(int id, String nombre, String correo, String contrasena, String rol, String fechaRegistro) {

        this.id = id;
        this.nombre = nombre;
        this.correo = correo;
        this.contrasena = contrasena;
        this.rol = rol;
        this.fechaRegistro = fechaRegistro;

    }

    /*
    Creamos un segundo constructor vacío por si acaso.
     */
    public Usuario() {

    }

    /*
    Generamos los getters y setters.
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getContrasena() {
        return contrasena;
    }

    public void setContrasena(String contrasena) {
        this.contrasena = contrasena;
    }

    public String getRol() {
        return rol;
    }

    public void setRol(String rol) {
        this.rol = rol;
    }

    public String getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(String fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    /*
    Creamos un toString para mostrar los datos.
     */
    @Override
    public String toString() {

        return "Usuario: " + id +
                "\nNombre: " + nombre +
                "\nCorreo: " + correo +
                "\nContraseña: " + contrasena +
                "\nRol: " + rol +
                "\nFecha de registro: " + fechaRegistro;
    }
}
