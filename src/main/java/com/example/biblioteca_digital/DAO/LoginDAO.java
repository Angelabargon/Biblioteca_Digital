package com.example.biblioteca_digital.DAO;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Rol;
import com.example.biblioteca_digital.modelos.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class LoginDAO {

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
