package com.example.biblioteca_digital.conexion;

/*
Imports de la clase ConexionBD.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/*
Creamos la clase encargada de conectar la base de datos con la aplicación.
 */
public class ConexionBD {

    /*
    Especificamos:

    - La URL de la conexión con la base de datos.
    - El nombre del usuario para acceder a la base de datos.
    - La contraseña del usuario.
     */
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca_web";
    private static final String Usuario = "root";
    private static final String Contrasena = "";

    private static Connection conexion = null;

    /*
    Establecemos la conexión con la base de datos y la devolvemos.
    En caso de existir la conexión previamente, se reutilizará.
     */
    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, Usuario, Contrasena);
                System.out.println("Conexion establecida.");
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos.");
        }
        return conexion;
    }

    /*
    Creamos otro metodo para cerrar la conexión con la base de datos en caso de estar abierta.
     */
    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexión cerrada.");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión.");
        }
    }

}
