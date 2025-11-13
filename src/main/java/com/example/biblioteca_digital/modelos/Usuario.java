package com.example.biblioteca_digital.modelos;

/*
Importamos lo necesario para usuario.
 */
import java.time.LocalDate;

/*
Creamos la clas Usuario que representará a los clientes que entren a esta aplicación.
 */
public class Usuario {

    /*
    Definimos los atributos del usuario:
    
    * id
    * nombre
    * correo
    * contrasena
    * rol
    * fechaRegistro
     */
    private int id;
    private String nombre;
    private String primerApellido;
    private String correo;
    private String contrasena;
    private String rol;
    private LocalDate fechaRegistro;

    /*
    Creamos el constructor e iniciamos sus atributos:
    
    * id
    * nombre
    * correo
    * contrasena
    * rol
    * fechaRegistro
     */
    public Usuario(int id, String nombre, String primerApellido, String correo, String contrasena, String rol, LocalDate fechaRegistro) {

        this.id = id;
        this.nombre = nombre;
        this.primerApellido = primerApellido;
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
    Generamos los getters y setters:
    
    * getId y setId
    * getNombre y setNombre
    * getPrimerApellido y setPrimerApellido
    * getCorreo y setCorreo
    * getContrasena y setContrasena
    * getRol y setRol
    * getFechaRegistro y setFechaRegistro
     */
    public int getId() {return id;}
    public void setId(int id) {this.id = id;}
    public String getNombre() {return nombre;}
    public void setNombre(String nombre) {this.nombre = nombre;}

    public String getPrimerApellido() {return primerApellido;}
    public void setPrimerApellido(String primerApellido) {this.primerApellido = primerApellido;}
    public String getCorreo() {return correo;}
    public void setCorreo(String correo) {this.correo = correo;}
    public String getContrasena() {return contrasena;}
    public void setContrasena(String contrasena) {this.contrasena = contrasena;}
    public String getRol() {return rol;}
    public void setRol(String rol) {this.rol = rol;}
    public LocalDate getFechaRegistro() {return fechaRegistro;}
    public void setFechaRegistro(LocalDate fechaRegistro) {this.fechaRegistro = fechaRegistro;}

    /*
    Creamos un toString para mostrar los datos.
     */
    @Override
    public String toString() {

        return "Usuario: " + id +
                "\nNombre: " + nombre +
                "\nPrimer Apellido " + primerApellido +
                "\nCorreo: " + correo +
                "\nContraseña: " + contrasena +
                "\nRol: " + rol +
                "\nFecha de registro: " + fechaRegistro;
    }
}
