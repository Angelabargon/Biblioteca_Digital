package com.example.biblioteca_digital.modelos;
/**
 * Creamos la clase Sesion que se utilizará para obtener el usuario actual del usuario
 */
public class Sesion
{
    private static Sesion instancia;
    private Usuario usuarioActual;
    /**
     * Constructor sesion vacío.
     */
    private Sesion() {}
    /**
     * Metodo para obtener la instancia de la sesión
     */
    public static Sesion getInstancia()
    {
        if (instancia == null)
        {instancia = new Sesion();}
        return instancia;
    }
    /**
     * Getter del atributo.
     * Metodo para obtener el usuario actual.
     */
    public Usuario getUsuario()
    {return this.usuarioActual;}

    /**
     * Setter del atributo.
     * Metodo para decir quien es el usuario actual.
     */
    public static void setUsuario(Usuario usuario)
    {Sesion.getInstancia().usuarioActual = usuario;}

    /**
     * Metodo para cerrar la sesión del usuario actual.
     */
    public static void cerrarSesion()
    {Sesion.getInstancia().usuarioActual = null;}
}
