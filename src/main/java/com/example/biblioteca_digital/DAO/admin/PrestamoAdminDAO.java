package com.example.biblioteca_digital.DAO.admin;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Prestamo;
import com.example.biblioteca_digital.modelos.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoAdminDAO {

    /**
     * Obtiene todos los préstamos (incluyendo nombre de usuario y título de libro mediante JOIN).
     */
    public List<Prestamo> obtenerTodos() {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT p.id, p.id_usuario, p.id_libro, p.fecha_inicio, p.fecha_fin, p.estado, u.nombre_usuario, u.nombre, l.titulo " +
                "FROM prestamos p " +
                "JOIN usuarios u ON p.id_usuario = u.id " +
                "JOIN libros l ON p.id_libro = l.id " +
                "ORDER BY p.fecha_inicio DESC";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Prestamo p = new Prestamo();
                p.setId(rs.getInt("id"));
                p.setId_usuario(rs.getInt("id_usuario"));
                p.setId_libro(rs.getInt("id_libro"));
                Date di = rs.getDate("fecha_inicio");
                Date df = rs.getDate("fecha_fin");
                if (di != null) p.setFecha_inicio(di.toLocalDate());
                if (df != null) p.setFecha_fin(df.toLocalDate());
                p.setEstado(rs.getString("estado"));

                // usuario
                Usuario u = new Usuario();
                u.setId(rs.getInt("id_usuario"));
                // prefer nombre_usuario if present, else nombre
                String nombreUsuario = null;
                try { nombreUsuario = rs.getString("nombre_usuario"); } catch (SQLException ex) {}
                u.setNombreUsuario(nombreUsuario != null ? nombreUsuario : rs.getString("nombre"));

                // libro
                Libro l = new Libro();
                l.setId(rs.getInt("id_libro"));
                l.setTitulo(rs.getString("titulo"));

                p.setUsuario(u);
                p.setLibro(l);

                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    /**
     * Crear préstamo en transacción: insertar en prestamos y decrementar cantidad del libro.
     */
    public boolean crearPrestamo(Prestamo prestamo) {
        String selectSql = "SELECT cantidad FROM libros WHERE id = ? FOR UPDATE";
        String insertSql = "INSERT INTO prestamos (id_usuario, id_libro, fecha_inicio, fecha_fin, estado) VALUES (?, ?, ?, ?, ?)";
        String updateLibroSql = "UPDATE libros SET cantidad = cantidad - 1, disponible = (cantidad - 1) > 0 WHERE id = ?";

        Connection con = null;
        try {
            con = ConexionBD.getConexion();
            con.setAutoCommit(false);

            try (PreparedStatement ps = con.prepareStatement(selectSql)) {
                ps.setInt(1, prestamo.getId_libro());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || rs.getInt("cantidad") <= 0) {
                        con.rollback();
                        return false;
                    }
                }
            }

            try (PreparedStatement ps = con.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, prestamo.getId_usuario());
                ps.setInt(2, prestamo.getId_libro());
                ps.setDate(3, Date.valueOf(prestamo.getFecha_inicio()));
                ps.setDate(4, Date.valueOf(prestamo.getFecha_fin()));
                ps.setString(5, prestamo.getEstado());
                ps.executeUpdate();
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) prestamo.setId(keys.getInt(1));
                }
            }

            try (PreparedStatement ps = con.prepareStatement(updateLibroSql)) {
                ps.setInt(1, prestamo.getId_libro());
                ps.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            if (con != null) {
                try { con.rollback(); } catch (SQLException e) { e.printStackTrace(); }
            }
            return false;
        } finally {
            if (con != null) {
                try { con.setAutoCommit(true); con.close(); } catch (SQLException e) { e.printStackTrace(); }
            }
        }
    }

    public boolean eliminarPrestamo(int id) {
        String sql = "DELETE FROM prestamos WHERE id = ?";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public long contarPrestamosActivos() {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE estado = 'activo'";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }


    public long contarPrestamosVencidos() {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE fecha_fin < CURRENT_DATE() AND estado = 'activo'";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}
