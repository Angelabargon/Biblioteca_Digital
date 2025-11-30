package com.example.biblioteca_digital.modelos;

public class Sesion
{
    private static Sesion instancia;
    // CAMBIO CLAVE 1: Debe ser campo de INSTANCIA y PRIVADO, no estático.
    private Usuario usuarioActual;

    private Sesion() {}

    public static Sesion getInstancia()
    {
        if (instancia == null)
        {instancia = new Sesion();}
        return instancia;
    }

    public Usuario getUsuario()
    {return this.usuarioActual;}

    // CAMBIO CLAVE 3: setUsuario es estático y usa la instancia.
    public static void setUsuario(Usuario usuario)
    {Sesion.getInstancia().usuarioActual = usuario;}

    public static void cerrarSesion()
    {Sesion.getInstancia().usuarioActual = null;}
}
