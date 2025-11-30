package com.example.biblioteca_digital.DAO.usuario;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Estado;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Prestamo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class CatalogoDAO
{
    private PrestamoDAO prestamoDAO;

    public void setPrestamoDAO(PrestamoDAO dao) {
        this.prestamoDAO = dao;
    }
    private static Connection conectar() {
        return ConexionBD.getConexion();
    }

    /**
     * Método qye obtiene libros del catálogo aplicando filtros opcionales de título, autor y género.
     * @param titulo Filtro por título (puede ser nulo o vacío).
     * @param autor Filtro por autor (puede ser nulo o vacío).
     * @param genero Filtro por género (puede ser "Todas", nulo o vacío).
     * @return Lista de objetos Libro que cumplen con los filtros.
     */
    public List<Libro> cargarCatalogo(String titulo, String autor, String genero)
    {
        List<Libro> libros = new ArrayList<>();
        StringBuilder sqlBuilder = new StringBuilder("SELECT * FROM libros WHERE 1=1");
        List<Object> parametros = new ArrayList<>();

        if (titulo != null && !titulo.trim().isEmpty())
        {
            sqlBuilder.append(" AND LOWER(titulo) LIKE LOWER(?)");
            parametros.add("%" + titulo.trim() + "%");
        }
        if (autor != null && !autor.trim().isEmpty())
        {
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
            for (int i = 0; i < parametros.size(); i++)
            {
                ps.setObject(i + 1, parametros.get(i));
            }

            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
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
                            rs.getString("contenido")
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
     * Método que obtiene todos los libros del catálogo.
     * NOTA: Ahora es un método de instancia.
     * @return Lista de todos los objetos Libro.
     */
    public List<Libro> obtenerTodosLosLibros()
    {
        return cargarCatalogo(null, null, null);
    }

    /**
     * Obtiene todos los detalles de un libro dado su ID.
     * NOTA: Ahora es un método de instancia.
     */
    public Libro obtenerLibroPorId(int idLibro)
    {
        Libro libro = null;
        String sql = """
        SELECT id, titulo, autor, genero, descripcion, isbn, foto, cantidad_disponible, cantidad, disponible, contenido 
        FROM libros 
        WHERE id = ?
        """;
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, idLibro);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    libro = new Libro(
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
                            rs.getString("contenido")
                    );
                }
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al obtener libro por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return libro;
    }

    /**
     * Cuenta el número de préstamos activos para un usuario.
     * @param idUsuario ID del usuario.
     * @return Número de préstamos activos.
     */
    public int contarPrestamosActivos(int idUsuario)
    {
        int count = 0;
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_usuario = ? AND estado = 'activo'";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, idUsuario);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    count = rs.getInt(1);
                }
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al contar préstamos activos: " + e.getMessage());
            e.printStackTrace();
        }
        return count;
    }

    /**
     * Obtiene el autor de un libro dado su ID.
     * @param idLibro ID del libro.
     * @return Nombre del autor o null si no se encuentra.
     */
    public String obtenerAutorPorIdLibro(int idLibro)
    {
        // ... (Este método se mantiene igual, ya que estaba correcto)
        String autor = null;
        String sql = "SELECT autor FROM libros WHERE id = ?";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, idLibro);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    autor = rs.getString("autor");
                }
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al obtener autor por ID: " + e.getMessage());
            e.printStackTrace();
        }
        return autor;
    }
}