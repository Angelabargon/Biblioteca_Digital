package com.example.biblioteca_digital.DAO;

/**
 * Imports de la clase.
 */
import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Usuario;
import java.sql.*;

/**
 * DAO encargado de gestionar las operaciones relacionadas con el registro
 * de nuevos usuarios en la base de datos.
 */
public class RegistroDAO {

    /**
     * Obtiene el siguiente ID disponible en la tabla usuarios.
     *
     * @return siguiente ID (MAX(id) + 1), o 1 si la tabla está vacía o ocurre un error.
     */
    public int siguienteId() {
        String sql = "SELECT MAX(id) FROM usuarios";
        int nextId = 1;

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {

            if (rs.next()) {
                nextId = rs.getInt(1) + 1;
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener el siguiente ID: " + e.getMessage());
        }

        return nextId;
    }

    /**
     * Guarda un nuevo usuario en la base de datos.
     *
     * @param usuario objeto Usuario a insertar.
     */
    public void guardarUsuario(Usuario usuario) {
        String sql = "INSERT INTO usuarios (id, nombre_usuario, nombre, primer_apellido, correo, contrasena, rol, fecha_registro) " +
                "VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setInt(1, usuario.getId());
            stmt.setString(2, usuario.getNombreUsuario());
            stmt.setString(3, usuario.getNombre());
            stmt.setString(4, usuario.getPrimerApellido());
            stmt.setString(5, usuario.getCorreo());
            stmt.setString(6, usuario.getContrasena());
            stmt.setString(7, usuario.getRol().toString());
            stmt.setDate(8, Date.valueOf(usuario.getFechaRegistro()));

            stmt.executeUpdate();

        } catch (SQLException e) {
            System.err.println("Error al guardar el usuario: " + e.getMessage());
        }
    }

    /**
     * Verifica si ya existe un usuario con el nombre de usuario indicado.
     *
     * @param nombreUsuario nombre de usuario a comprobar.
     * @return true si existe, false si no.
     */
    public boolean existeUsuarioPorNombre(String nombreUsuario) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE nombre_usuario = ?";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, nombreUsuario);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar usuario por nombre: " + e.getMessage());
        }

        return false;
    }

    /**
     * Verifica si ya existe un usuario con el correo indicado.
     *
     * @param correo correo a comprobar.
     * @return true si existe, false si no.
     */
    public boolean existeUsuarioPorCorreo(String correo) {
        String sql = "SELECT COUNT(*) FROM usuarios WHERE correo = ?";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, correo);

            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al verificar usuario por correo: " + e.getMessage());
        }

        return false;
    }
}
