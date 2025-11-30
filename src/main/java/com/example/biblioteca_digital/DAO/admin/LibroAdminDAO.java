package com.example.biblioteca_digital.DAO.admin;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Libro;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase DAO encargada de gestionar todas las operaciones relacionadas con
 * la tabla <b>libros</b> en la base de datos. Proporciona métodos CRUD
 * (crear, leer, actualizar, eliminar) y utilidades adicionales como
 * contar libros o recuperar la lista de géneros existentes.
 *
 * <p>Utiliza la clase {@link ConexionBD} para obtener la conexión con la base
 * de datos. Todas las operaciones se ejecutan mediante sentencias
 * preparadas para mayor seguridad.</p>
 */
public class LibroAdminDAO {

    /**
     * Obtiene todos los libros registrados en la base de datos ordenados por título.
     *
     * @return una lista con todos los libros existentes; nunca es null, aunque puede estar vacía.
     */
    public List<Libro> obtenerTodos() {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT id, titulo, autor, descripcion, genero, isbn, foto, " +
                "cantidad_disponible, cantidad, disponible, contenido FROM libros ORDER BY titulo";

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

    /**
     * Obtiene un libro concreto según su identificador.
     *
     * @param id identificador numérico del libro.
     * @return el libro encontrado o {@code null} si no existe.
     */
    public Libro obtenerPorId(int id) {
        String sql = "SELECT id, titulo, autor, descripcion, genero, isbn, foto, " +
                "cantidad_disponible, cantidad, disponible, contenido FROM libros WHERE id = ?";

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
                    l.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                    l.setDisponible(rs.getBoolean("disponible"));
                    l.setContenido(rs.getString("contenido"));
                    return l;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Inserta un nuevo libro en la base de datos.
     * También establece automáticamente el ID generado y marca
     * la disponibilidad según la cantidad indicada.
     *
     * @param libro objeto {@link Libro} que se desea registrar.
     * @return true si la operación tuvo éxito; false en caso contrario.
     */
    public boolean agregarLibro(Libro libro) {
        String sql = "INSERT INTO libros (titulo, autor, descripcion, genero, isbn, foto, " +
                "cantidad_disponible, cantidad, disponible, contenido) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getDescripcion());
            ps.setString(4, libro.getGenero());
            ps.setString(5, libro.getIsbn());
            ps.setString(6, libro.getFoto());
            ps.setInt(7, libro.getCantidad());
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

    /**
     * Actualiza todos los datos de un libro existente en la base de datos.
     *
     * @param libro objeto {@link Libro} con los datos modificados.
     * @return true si el libro fue actualizado; false si no existe o falla la operación.
     */
    public boolean actualizarLibro(Libro libro) {
        String sql = "UPDATE libros SET titulo=?, autor=?, descripcion=?, genero=?, isbn=?, foto=?, " +
                "cantidad_disponible=?, cantidad=?, disponible=?, contenido=? WHERE id = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, libro.getTitulo());
            ps.setString(2, libro.getAutor());
            ps.setString(3, libro.getDescripcion());
            ps.setString(4, libro.getGenero());
            ps.setString(5, libro.getIsbn());
            ps.setString(6, libro.getFoto());
            ps.setInt(7, libro.getCantidad());
            ps.setInt(8, libro.getCantidad());
            ps.setBoolean(9, libro.getCantidad() > 0);
            ps.setString(10, libro.getContenido());
            ps.setInt(11, libro.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Elimina un libro de la base de datos según su identificador.
     *
     * @param id identificador numérico del libro a eliminar.
     * @return true si el libro fue eliminado; false si no existe o la operación falla.
     */
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

    /**
     * Obtiene una lista con todos los géneros registrados en la base de datos.
     * Solo devuelve valores distintos y no vacíos.
     *
     * @return lista ordenada alfabéticamente de géneros existentes.
     */
    public List<String> obtenerGeneros() {
        List<String> generos = new ArrayList<>();
        String sql = "SELECT DISTINCT genero FROM libros WHERE genero IS NOT NULL AND genero <> '' ORDER BY genero";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) generos.add(rs.getString("genero"));

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return generos;
    }

    /**
     * Cuenta la cantidad total de libros registrados en la base de datos.
     *
     * @return número total de libros; 0 si ocurre un error o la tabla está vacía.
     */
    public long contarLibros() {
        String sql = "SELECT COUNT(*) FROM libros";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next())
                return rs.getLong(1); }
        catch (SQLException e) { e.printStackTrace(); } return 0;
    }
}