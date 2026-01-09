package com.example.biblioteca_digital.DAO.usuario;

/**
 * Imports necesarios de la clase.
 */
import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Usuario;
import java.sql.*;

/**
 * DAO encargado de gestionar operaciones relacionadas con los usuarios.
 * Este DAO se utiliza en vistas donde se necesita mostrar información
 * del usuario sin cargar toda la sesión completa.
 */
public class UsuarioDAO
{

    /**
     * Obtiene un usuario por su ID.
     *
     * @param idUsuario Identificador del usuario.
     * @return Objeto Usuario con los datos cargados o null si no existe.
     */
    public Usuario obtenerUsuarioPorId(int idUsuario) {

        Usuario usuario = null;

        String sql = "SELECT id, nombre, nombre_usuario, correo FROM usuarios WHERE id = ?";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                    usuario.setCorreo(rs.getString("correo"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return usuario;
    }
}