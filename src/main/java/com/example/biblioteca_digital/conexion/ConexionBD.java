package com.example.biblioteca_digital.conexion;

/**
 * Imports de la clase ConexionBD.
 */
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Clase encargada de conectar la base de datos con la aplicación.
 */
public class ConexionBD {

    /** URL de conexión a la base de datos. */
    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca_web";

    /** Usuario de la base de datos. */
    private static final String Usuario = "root";

    /** Contraseña del usuario de la base de datos. */
    private static final String Contrasena = "";

    /** Instancia única de la conexión activa. */
    private static Connection conexion = null;

    /**
     * Obtiene la conexión actual con la base de datos.
     * Si no existe o está cerrada, se crea una nueva conexión.
     *
     * @return objeto {@link Connection} activo contra la base de datos
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

    /**
     * Cierra la conexión actual si está abierta.
     * Libera los recursos asociados a la conexión.
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
