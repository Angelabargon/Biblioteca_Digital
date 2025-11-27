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

        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql);
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
                String estadoStr = rs.getString("estado");
                if (estadoStr != null) {
                    switch (estadoStr.toLowerCase()) {
                        case "activo": p.setEstado(String.valueOf(Estado.Activo)); break;
                        case "devuelto": p.setEstado(String.valueOf(Estado.Devuelto)); break;
                        case "bloqueado": p.setEstado(String.valueOf(Estado.Bloqueado)); break;
                        default: p.setEstado(String.valueOf(Estado.Activo));
                    }
                } else {
                    p.setEstado(String.valueOf(Estado.Activo));
                }
                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public boolean agregarPrestamo(Prestamo p) {
        String sql = "INSERT INTO prestamos (id_usuario, id_libro, fecha_inicio, fecha_fin, estado) VALUES (?,?,?,?,?)";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getId_usuario());
            ps.setInt(2, p.getId_libro());
            ps.setDate(3, Date.valueOf(p.getFecha_inicio()));
            ps.setDate(4, Date.valueOf(p.getFecha_fin()));
            ps.setString(5, p.getEstado().toLowerCase());

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizarPrestamo(Prestamo p) {
        String sql = "UPDATE prestamos SET id_usuario=?, id_libro=?, fecha_inicio=?, fecha_fin=?, estado=? WHERE id=?";
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, p.getId_usuario());
            ps.setInt(2, p.getId_libro());
            ps.setDate(3, Date.valueOf(p.getFecha_inicio()));
            ps.setDate(4, Date.valueOf(p.getFecha_fin()));
            ps.setString(5, p.getEstado().toLowerCase());
            ps.setInt(6, p.getId());

            int filas = ps.executeUpdate();
            return filas > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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
