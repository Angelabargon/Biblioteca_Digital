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
    /**
     * Llama a prestamos para guardar un préstamo al presionar el botón
     */
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    /**
     * Obtiene la conexión a la base de datos.
     * Se mantiene como static, ya que es un método de utilidad pura.
     */
    private static Connection conectar() {
        return ConexionBD.getConexion();
    }

    /**
     * Obtiene libros del catálogo aplicando filtros opcionales de título, autor y género.
     * NOTA: Ahora es un método de instancia.
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
                            // Usar 'cantidad_disponible' y 'cantidad_total' para consistencia
                            rs.getInt("cantidad_disponible"),
                            rs.getInt("cantidad_total"),
                            rs.getBoolean("disponible")
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
     * Obtiene todos los libros del catálogo.
     * NOTA: Ahora es un método de instancia.
     * @return Lista de todos los objetos Libro.
     */
    public List<Libro> obtenerTodosLosLibros() {
        // Redirigimos a cargarCatalogo sin filtros para evitar duplicar código SQL
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
        SELECT id, titulo, autor, genero, descripcion, isbn, foto, cantidad_total, cantidad_disponible, disponible 
        FROM libros 
        WHERE id = ?
    """; // Agregado 'cantidad_total' para consistencia en la consulta
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
                            // Se asume que el constructor de Libro requiere: disponible, total
                            rs.getInt("cantidad_disponible"),
                            rs.getInt("cantidad_total"),
                            rs.getBoolean("disponible")
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
        // ... (Este método se mantiene igual, ya que estaba correcto)
        int count = 0;
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_usuario = ? AND fecha_devolucion IS NULL";
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
     * Realiza la solicitud de préstamo para un libro, actualizando la disponibilidad.
     * Se ha mejorado el manejo de la transacción.
     * @param idUsuario ID del usuario que solicita el préstamo.
     * @param idLibro ID del libro solicitado.
     * @return true si el préstamo fue exitoso, false en caso contrario.
     */
    public boolean pedirPrestado(int idUsuario, int idLibro)
    {
        String sqlCheck = "SELECT cantidad_disponible FROM libros WHERE id = ?";
        String sqlUpdate = "UPDATE libros SET cantidad_disponible = cantidad_disponible - 1 WHERE id = ?";

        // Nota: 'obtenerDatosUsuarioYLibro' no se usa en la lógica de transacción real,
        // solo para mensajes o logs. Se puede mantener o eliminar si no es necesario.
        // String[] datosPrestamo = obtenerDatosUsuarioYLibro(idUsuario, idLibro);

        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = fechaInicio.plusDays(14);
        Connection conn = null; // Declarar fuera del try-with-resources para el rollback

        try
        {
            conn = conectar();
            conn.setAutoCommit(false); // Iniciar transacción

            int disponibles = 0;
            // Bloque 1: Chequear disponibilidad
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck))
            {
                psCheck.setInt(1, idLibro);
                try (ResultSet rs = psCheck.executeQuery())
                {
                    if (rs.next()) {
                        disponibles = rs.getInt("cantidad_disponible");
                    }
                }
            }

            if (disponibles <= 0)
            {
                conn.rollback();
                return false;
            }

            // Bloque 2: Guardar el registro de préstamo
            Prestamo prestamo = new Prestamo(
                    -1,
                    idUsuario,
                    idLibro,
                    fechaInicio,
                    fechaFin,
                    Estado.Activo
            );
            if (!prestamoDAO.guardarPrestamo(prestamo))
            {
                conn.rollback();
                throw new SQLException("Fallo al guardar registro de préstamo");
            }

            // Bloque 3: Actualizar el stock
            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate))
            {
                psUpdate.setInt(1, idLibro);
                int filasAfectadas = psUpdate.executeUpdate();
                if (filasAfectadas == 0) throw new SQLException("Fallo al actualizar stock disponible");
            }

            conn.commit(); // Confirmar la transacción
            return true;
        }
        catch (SQLException e)
        {
            System.err.println("Error en la transacción de préstamo: " + e.getMessage());
            if (conn != null)
            {
                try {
                    conn.rollback(); // Rollback en caso de error
                } catch (SQLException rollbackException) {
                    System.err.println("Error al realizar rollback: " + rollbackException.getMessage());
                }
            }
            e.printStackTrace();
            return false;
        }
        finally
        {
            if (conn != null)
            {
                try {
                    conn.close(); // Asegurar el cierre de la conexión
                } catch (SQLException closeException) {
                    System.err.println("Error al cerrar la conexión: " + closeException.getMessage());
                }
            }
        }
    }

    private String[] obtenerDatosUsuarioYLibro(int idUsuario, int idLibro)
    {
        String sql = """
            SELECT u.nombre_usuario, l.titulo
            FROM usuarios u, libros l
            WHERE u.id = ? AND l.id = ?
            """;
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idLibro);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    return new String[]{
                            rs.getString("nombre_usuario"),
                            rs.getString("titulo")
                    };
                }
            }
        }
        catch (SQLException e)
        {
            System.err.println("Error al obtener datos usuario/libro: " + e.getMessage());
            e.printStackTrace();
        }
        return null;
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