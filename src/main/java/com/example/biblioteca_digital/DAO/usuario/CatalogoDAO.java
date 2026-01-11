package com.example.biblioteca_digital.DAO.usuario;

/**
 * Imports necesarios de la clase.
 */
import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Libro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO encargado de gestionar operaciones relacionadas con los libros en el catálogo.
 * Este DAO se utiliza en la vista del catálogo.
 */
public class CatalogoDAO {

    /** Variable para conectar con métodos del DAO de los préstamos */
    private PrestamoDAO prestamoDAO;
    public void setPrestamoDAO(PrestamoDAO dao)
    {
        this.prestamoDAO = dao;
    }
    /** Conexión con la base de datos */
    private static Connection conectar() {
        return ConexionBD.getConexion();
    }

    /**
     * Método qye obtiene libros del catálogo aplicando filtros opcionales de título, autor y género.
     * @param titulo Filtro por título (puede ser nulo o vacío).
     * @param autor Filtro por autor (puede ser nulo o vacío).
     * @param genero Filtro por género (puede ser "Todas", nulo o vacío).
     */
    public List<Libro> cargarCatalogo(String titulo, String autor, String genero) {

        List<Libro> libros = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM libros WHERE 1=1");
        List<Object> parametros = new ArrayList<>();

        if (titulo != null && !titulo.trim().isEmpty()) {
            sqlBuilder.append(" AND LOWER(titulo) LIKE LOWER(?)");
            parametros.add("%" + titulo.trim() + "%");
        }

        if (autor != null && !autor.trim().isEmpty()) {
            sqlBuilder.append(" AND LOWER(autor) LIKE LOWER(?)");
            parametros.add("%" + autor.trim() + "%");
        }

        if (genero != null && !genero.trim().isEmpty() && !genero.equalsIgnoreCase("Todas")) {
            sqlBuilder.append(" AND LOWER(genero) = LOWER(?)");
            parametros.add(genero);
        }

        sqlBuilder.append(" ORDER BY titulo");
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sqlBuilder.toString()))
        {
            for (int i = 0; i < parametros.size(); i++) {
                ps.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Libro libro = new Libro(
                            rs.getInt("id"),
                            rs.getString("titulo"),
                            rs.getString("autor"),
                            rs.getString("genero"),
                            rs.getString("isbn"),
                            rs.getString("descripcion"),
                            rs.getString("foto"),
                            rs.getInt("cantidad_disponible"),
                            rs.getInt("cantidad"),
                            rs.getBoolean("disponible"),
                            rs.getString("contenido"),
                            rs.getInt("duracion_prestamo")
                    );
                    libros.add(libro);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al cargar el catálogo con filtros: " + e.getMessage());
            e.printStackTrace();
        }

        return libros;
    }

    /**
     * Método que cuenta el número de préstamos activos para un usuario.
     * @param idUsuario ID del usuario.
     * @return Número de préstamos activos.
     */
    public int contarPrestamosActivos(int idUsuario) {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_usuario = ? AND estado = 'activo'";

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, idUsuario);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    count = rs.getInt(1);
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al contar préstamos activos: " + e.getMessage());
            e.printStackTrace();
        }

        return count;
    }

    /**
     * Método que obtiene el autor de un libro dado su ID.
     * @param idLibro ID del libro.
     * @return Nombre del autor o null si no se encuentra.
     */
    public String obtenerAutorPorIdLibro(int idLibro) {
        String autor = null;
        String sql = "SELECT autor FROM libros WHERE id = ?";

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, idLibro);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    autor = rs.getString("autor");
                }
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener autor por ID: " + e.getMessage());
            e.printStackTrace();
        }

        return autor;
    }
}