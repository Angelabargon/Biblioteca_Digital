package com.example.biblioteca_digital.DAO.usuario;

/**
 * Imports necesarios.
 */
import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Usuario;
import java.sql.*;

/**
 * DAO encargado de gestionar operaciones relacionadas con los usuarios.
 *
 * Este DAO se utiliza en vistas donde se necesita mostrar información
 * del usuario sin cargar toda la sesión completa.
 */
public class UsuarioDAO {

    /**
     * Metodo auxiliar para la conexión a la base de datos.
     *
     * @return conexión activa o null si falla.
     */
    private static Connection conectar()
    {
        return ConexionBD.getConexion();
    }

    /**
     * Metodo que obtiene todos los detalles de un usuario dado su ID.
     *
     * @param idUsuario ID del usuario a buscar.
     * @return Objeto Usuario con los datos cargados, o null si no existe.
     */
    public static Usuario obtenerUsuarioPorId(int idUsuario) {

        Usuario usuario = null;

        String sql = "SELECT id, nombre, nombre_usuario, correo FROM usuarios WHERE id = ?";

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    usuario = new Usuario();
                    usuario.setId(rs.getInt("id"));
                    usuario.setNombre(rs.getString("nombre"));
                    usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener usuario por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return usuario;
    }
}