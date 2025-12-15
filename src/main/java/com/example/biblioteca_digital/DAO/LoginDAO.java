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
     * Este metodo se encarga de verificar que el usuario que está iniciando sesión existe en la BDD.
     *
     * @param correo      Correo electrónico del usuario.
     * @param contrasena  Contraseña del usuario.
     * @param rol         Rol esperado (ej. "usuario" o "admin").
     * @return Optional con el usuario autenticado si existe coincidencia,
     * Optional.empty() si no se encuentra ningún usuario válido.
     */
    public static Optional<Usuario> autenticar(String correo, String contrasena, String rol) {

        String sql = "SELECT * FROM usuarios WHERE correo = ? AND contrasena = ? AND rol = ?";

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            // Se asignan parámetros a la consulta.
            stmt.setString(1, correo);
            stmt.setString(2, contrasena);
            stmt.setString(3, rol);

            ResultSet rs = stmt.executeQuery();

            // De existir un usuario con esas credenciales, se construye el objeto.
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

        // Si no se encontró coincidencia, se devuelve Optional vacío.
        return Optional.empty();
    }
}
