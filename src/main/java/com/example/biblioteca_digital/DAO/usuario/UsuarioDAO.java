package com.example.biblioteca_digital.DAO.usuario;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Usuario;
import java.sql.*;

public class UsuarioDAO
{

    /**
     * Método auxiliar para la conexión a la base de datos.
     */
    private static Connection conectar()
    {
        return ConexionBD.getConexion();
    }

    /**
     * Obtiene todos los detalles de un usuario dado su ID.
     * Requerido por PrestamoDAO para cargar el objeto Usuario.
     */
    public static Usuario obtenerUsuarioPorId(int idUsuario)
    {
        Usuario usuario = null;
        String sql = "SELECT id, nombre, nombre_usuario, correo  FROM usuarios WHERE id = ?";

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombre(rs.getString("correo "));
                    usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                }
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return usuario;
    }
}