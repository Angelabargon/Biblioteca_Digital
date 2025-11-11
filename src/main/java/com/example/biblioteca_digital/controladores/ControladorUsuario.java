package com.example.biblioteca_digital.controladores;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ControladorUsuario<T, ID> implements Crud<T, ID>
{

    public void initializeCuenta(Usuario cuenta) {

    }

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
                usuario.setNombre(rs.getString("nombre_usuario"));
                usuario.setNombre(rs.getString("nombre"));
                usuario.setPrimerApellido(rs.getString("primer_apellido"));
                usuario.setCorreo(rs.getString("correo"));
                usuario.setContrasena(rs.getString("contrasena"));
                usuario.setRol(rs.getString("rol"));
                usuario.setFechaRegistro(rs.getDate("fecha_registro").toLocalDate());

                return Optional.of(usuario);
            }

        } catch (SQLException e) {
            System.out.println("Error al autenticar: " + e.getMessage());
        }

        return Optional.empty();
    }

    /**
     * @param entity
     * @return
     */
    @Override
    public T guardar(T entity) {
        return null;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Optional<T> buscarPorId(ID id) {
        return Optional.empty();
    }

    /**
     * @return
     */
    @Override
    public List<T> listar() {
        return List.of();
    }

    /**
     * @param id
     */
    @Override
    public void eliminarPorId(ID id) {

    }

    /**
     * @param entity
     */
    @Override
    public void eliminar(T entity) {

    }
}
