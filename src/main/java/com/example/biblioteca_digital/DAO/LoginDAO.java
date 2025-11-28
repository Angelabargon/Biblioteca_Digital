package com.example.biblioteca_digital.DAO;

/**
 * Hacemos los imports necesarios.
 */
import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Rol;
import com.example.biblioteca_digital.modelos.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

/**
 * Creamos la clase LoginDAO que almacenará los métodos del Login relacionados con la base de datos.
 */
public class LoginDAO {

    /**
     * Este método se encarga de verificar que el usuario que está iniciando sesión existe en la BDD.
     *
     * @param correo Correo electrónico del usuario.
     * @param contrasena Contraseña del usuario.
     * @param rol Rol del usuario (ej. "usuario" o "admin").
     * @return Un Optional con el usuario autenticado si las credenciales son correctas,
     * Optional.empty() si no se encuentra coincidencia.
     */
    public static Optional<Usuario> autenticar(String correo, String contrasena, String rol) {

        String sql = "SELECT * FROM usuarios WHERE correo = ? AND contrasena = ? AND rol = ?";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {
            stmt.setString(1, correo);
            stmt.setString(2, contrasena);
            stmt.setString(3, rol);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                Usuario usuario = new Usuario();
                usuario.setId(rs.getInt("id"));
                usuario.setNombreUsuario(rs.getString("nombre_usuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setPrimerApellido(rs.getString("primer_apellido"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setContrasena(rs.getString("contrasena"));
                usuario.setRol(Rol.valueOf(rs.getString("rol")));
                usuario.setFechaRegistro(rs.getDate("fecha_registro").toLocalDate());
                return Optional.of(usuario);
            }

        } catch (SQLException e) {
            System.out.println("Error al autenticar: " + e.getMessage());
        }

        return Optional.empty();
    }
}
