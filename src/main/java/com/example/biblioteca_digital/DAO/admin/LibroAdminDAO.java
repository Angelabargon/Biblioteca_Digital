package com.example.biblioteca_digital.DAO.admin;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LibroAdminDAO {

    public List<Libro> obtenerTodos() {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT id, titulo, autor, descripcion, genero, isbn, foto, cantidad_disponible, cantidad, disponible, contenido FROM libros ORDER BY titulo";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Libro l = new Libro();
                l.setId(rs.getInt("id"));
                l.setTitulo(rs.getString("titulo"));
                l.setAutor(rs.getString("autor"));
                l.setDescripcion(rs.getString("descripcion"));
                l.setGenero(rs.getString("genero"));
                l.setIsbn(rs.getString("isbn"));
                l.setFoto(rs.getString("foto"));
                l.setCantidad(rs.getInt("cantidad"));
                l.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                l.setDisponible(rs.getBoolean("disponible"));
                l.setContenido(rs.getString("contenido"));
                lista.add(l);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    public Libro obtenerPorId(int id) {
        String sql = "SELECT id, titulo, autor, descripcion, genero, isbn, foto, cantidad_disponible, cantidad, disponible, contenido FROM libros WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Libro l = new Libro();
                    l.setId(rs.getInt("id"));
                    l.setTitulo(rs.getString("titulo"));
                    l.setAutor(rs.getString("autor"));
                    l.setDescripcion(rs.getString("descripcion"));
                    l.setGenero(rs.getString("genero"));
                    l.setIsbn(rs.getString("isbn"));
                    l.setFoto(rs.getString("foto"));
                    l.setCantidad(rs.getInt("cantidad"));
                    l.setDisponible(rs.getBoolean("disponible"));
                    return l;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean actualizarCantidadYDisponibilidad(int idLibro, int nuevaCantidad) {
        String sql = "UPDATE libros SET titulo=?, autor=?, descripcion=?, genero=?, isbn=?, foto=?, cantidad_disponible=?, cantidad=?, disponible=?, contenido=? WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, nuevaCantidad);
            ps.setBoolean(2, nuevaCantidad > 0);
            ps.setInt(3, idLibro);
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }


    public boolean agregarLibro(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor, descripcion, genero, isbn, foto, cantidad_disponible, cantidad, disponible, contenido) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getDescripcion());
            ps.setString(4, libro.getGenero());
            ps.setString(5, libro.getIsbn());
            ps.setString(6, libro.getFoto());
            ps.setInt(7, libro.getCantidadDisponible());
            ps.setInt(8, libro.getCantidad());
            ps.setBoolean(9, libro.getCantidad() > 0);
            ps.setString(10, libro.getContenido());
            int filas = ps.executeUpdate();
            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) libro.setId(keys.getInt(1));
                }
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean actualizarLibro(Libro libro) {
        String sql = "UPDATE libros SET titulo=?, autor=?, descripcion=?, genero=?, isbn=?, foto=?, cantidad=?, disponible=? WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getDescripcion());
            ps.setString(4, libro.getGenero());
            ps.setString(5, libro.getIsbn());
            ps.setString(6, libro.getFoto());
            ps.setInt(7, libro.getCantidadDisponible());
            ps.setInt(8, libro.getCantidad());
            ps.setBoolean(9, libro.getCantidad() > 0);
            ps.setString(10, libro.getContenido());
            ps.setInt(11, libro.getId());
            int filas = ps.executeUpdate();
            return filas > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean eliminarLibro(int id) {
        String sql = "DELETE FROM libros WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, id);
            return ps.executeUpdate() > 0;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    public long contarLibros() {
        String sql = "SELECT COUNT(*) FROM libros";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getLong(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

}