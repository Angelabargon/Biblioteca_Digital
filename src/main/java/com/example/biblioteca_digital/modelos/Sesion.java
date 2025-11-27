package com.example.biblioteca_digital.modelos;

public class Sesion
{
    public static Usuario usuarioActual;
    public static void setUsuario(Usuario usuario) {usuarioActual = usuario;}
    public static Usuario getUsuario() {return usuarioActual;}
    public static void cerrarSesion() {usuarioActual = null;}
}
