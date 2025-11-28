package com.example.biblioteca_digital.DAO.admin;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Estado;
import com.example.biblioteca_digital.modelos.Prestamo;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class PrestamoAdminDAO {

    public List<Prestamo> obtenerTodos() {
        List<Prestamo> lista = new ArrayList<>();
        String sql = "SELECT id, id_usuario, id_libro, fecha_inicio, fecha_fin, estado FROM prestamos ORDER BY fecha_inicio DESC";
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
                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }


    public boolean crearPrestamo(Prestamo prestamo) {
        String insertSql = "INSERT INTO prestamos (id_usuario, id_libro, fecha_inicio, fecha_fin, estado) VALUES (?, ?, ?, ?, ?)";
        String selectSql = "SELECT cantidad FROM libros WHERE id = ? FOR UPDATE";
        String updateLibroSql = "UPDATE libros SET cantidad = cantidad - 1, disponible = (cantidad - 1) > 0 WHERE id = ?";

        Connection con = null;
        try {
            con = ConexionBD.getConexion();
            con.setAutoCommit(false);

            // check cantidad
            try (PreparedStatement ps = con.prepareStatement(selectSql)) {
                ps.setInt(1, prestamo.getId_libro());
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next() || rs.getInt("cantidad") <= 0) {
                        con.rollback();
                        return false;
                    }
                }
            }

            // insert prestamo
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

            // actualizar libro
            try (PreparedStatement ps = con.prepareStatement(updateLibroSql)) {
                ps.setInt(1, prestamo.getId_libro());
                ps.executeUpdate();
            }

            con.commit();
            return true;
        } catch (SQLException ex) {
            ex.printStackTrace();
            if (con != null) {
                try {
                    con.rollback();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            return false;
        } finally {
            if (con != null) {
                try {
                    con.setAutoCommit(true);
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean eliminarPrestamo(int id) {
        String sql = "DELETE FROM prestamos WHERE id = ?";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            int filas = ps.executeUpdate();
            return filas > 0;
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
        String sql = "SELECT COUNT(*) FROM prestamos WHERE estado = 'activo' AND fecha_fin < ?";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(LocalDate.now()));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getLong(1);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
}
