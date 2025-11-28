package com.example.biblioteca_digital.DAO.admin;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Usuario;
import com.example.biblioteca_digital.modelos.Rol;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class UsuarioAdminDAO {

    public List<Usuario> obtenerTodos() {
        List<Usuario> lista = new ArrayList<>();
        String sql = "SELECT id, nombre_usuario, nombre, primer_apellido, correo, contrasena, rol, fecha_registro FROM usuarios ORDER BY nombre_usuario";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Usuario u = new Usuario();
                u.setId(rs.getInt("id"));
                u.setNombreUsuario(rs.getString("nombre_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setPrimerApellido(rs.getString("primer_apellido"));
                u.setCorreo(rs.getString("correo"));
                u.setContrasena(rs.getString("contrasena"));
                u.setRol(Rol.valueOf(rs.getString("rol")));
                u.setFechaRegistro(rs.getDate("fecha_registro").toLocalDate());
                lista.add(u);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean insertarUsuario(Usuario u) {
        String sql = "INSERT INTO usuarios (nombre_usuario, nombre, primer_apellido, correo, contrasena, rol, fecha_registro) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getPrimerApellido());
            ps.setString(4, u.getCorreo());
            ps.setString(5, u.getContrasena());
            ps.setString(6, u.getRol() != null ? u.getRol().name() : Rol.usuario.name());
            ps.setDate(7, u.getFechaRegistro() != null ? Date.valueOf(u.getFechaRegistro()) : Date.valueOf(LocalDate.now()));
            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) u.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            // handle unique constraint violations etc
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizarUsuario(Usuario u) {
        String sql = "UPDATE usuarios SET nombre_usuario=?, nombre=?, primer_apellido=?, correo=?, contrasena=?, rol=? WHERE id = ?";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, u.getNombreUsuario());
            ps.setString(2, u.getNombre());
            ps.setString(3, u.getPrimerApellido());
            ps.setString(4, u.getCorreo());
            ps.setString(5, u.getContrasena());
            ps.setString(6, u.getRol() != null ? u.getRol().name() : Rol.usuario.name());
            ps.setInt(7, u.getId());
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public long contarUsuarios() {
        String sql = "SELECT COUNT(*) FROM usuarios";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public boolean agregarUsuario(Usuario u) {
        return insertarUsuario(u);
    }

    public boolean eliminarUsuario(int id) {
        String sql = "DELETE FROM usuarios WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

}
