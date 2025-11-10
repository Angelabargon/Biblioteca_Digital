package com.example.biblioteca_digital.conexion;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConexionBD {

    private static final String URL = "jdbc:mysql://localhost:3306/biblioteca_web";
    private static final String Usuario = "root";
    private static final String Contrasena = "";

    private static Connection conexion = null;

    public static Connection getConexion() {
        try {
            if (conexion == null || conexion.isClosed()) {
                conexion = DriverManager.getConnection(URL, Usuario, Contrasena);
                System.out.println("Conexion establecida");
            }
        } catch (SQLException e) {
            System.out.println("Error al conectar con la base de datos");
        }
        return conexion;
    }

    public static void cerrarConexion() {
        try {
            if (conexion != null && !conexion.isClosed()) {
                conexion.close();
                System.out.println("Conexion cerrada");
            }
        } catch (SQLException e) {
            System.out.println("Error al cerrar la conexión");
        }
    }

}
