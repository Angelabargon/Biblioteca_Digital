package com.example.biblioteca_digital.DAO.admin;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Usuario;
import com.example.biblioteca_digital.modelos.Rol;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO encargado de gestionar las operaciones administrativas relacionadas con los
 * usuarios del sistema. Permite obtener listados, registrar nuevos usuarios,
 * actualizar información existente, eliminarlos y obtener estadísticas simples.
 *
 * <p>Se conecta a la base de datos mediante {@link ConexionBD} utilizando consultas
 * SQL preparadas para evitar inyección y mejorar el rendimiento.</p>
 */
public class UsuarioAdminDAO {

    /**
     * Obtiene todos los usuarios almacenados en la base de datos,
     * ordenados por nombre de usuario.
     *
     * @return lista de objetos {@link Usuario} con todos los registros existentes.
     *         Nunca es null, pero puede estar vacía.
     */
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

    /**
     * Inserta un nuevo usuario en la base de datos.
     * <p>
     * Si la operación tiene éxito, el ID generado se asigna automáticamente
     * al objeto {@link Usuario}.
     * </p>
     *
     * @param u usuario que se desea registrar.
     * @return true si la operación se realizó correctamente; false si ocurrió algún error.
     */
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

    /**
     * Actualiza los datos de un usuario existente.
     *
     * @param u objeto {@link Usuario} con los valores actualizados.
     * @return true si la actualización afectó al menos una fila; false en caso contrario o si ocurre un error.
     */
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

    /**
     * Obtiene el número total de usuarios registrados en el sistema.
     *
     * @return cantidad de usuarios; devuelve 0 si ocurre un error o no hay registros.
     */
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

    /**
     * Alias de {@link #insertarUsuario(Usuario)} para mantener coherencia con otros DAOs.
     *
     * @param u usuario a agregar.
     * @return true si se insertó correctamente.
     */
    public boolean agregarUsuario(Usuario u) {
        return insertarUsuario(u);
    }

    /**
     * Elimina un usuario por su ID.
     *
     * @param id identificador del usuario a eliminar.
     * @return true si la eliminación fue exitosa; false si no existe o si ocurre un error.
     */
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
