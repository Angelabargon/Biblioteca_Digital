package com.example.biblioteca_digital.DAO.usuario;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Usuario;
import java.sql.*;

public class UsuarioDAO {

    /**
     * Método auxiliar para la conexión a la base de datos.
     */
    private Connection conectar() {
        return ConexionBD.getConexion();
    }

    /**
     * Obtiene todos los detalles de un usuario dado su ID.
     * Requerido por PrestamoDAO para cargar el objeto Usuario.
     */
    public Usuario obtenerUsuarioPorId(int idUsuario) {
        Usuario usuario = null;
        // Asumo que la tabla es 'usuarios' y tiene columnas relevantes
        String sql = "SELECT id, nombre, nombre_usuario, email FROM usuarios WHERE id = ?";

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Crear y popular el objeto Usuario
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                    // Asumiendo que la clase Usuario tiene estos setters
                    // usuario.setEmail(rs.getString("email"));
                }
            }
        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return usuario;
    }

    // ... (Otros métodos como login, registro, etc.)
}