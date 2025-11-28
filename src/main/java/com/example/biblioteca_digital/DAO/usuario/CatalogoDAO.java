package com.example.biblioteca_digital.DAO.usuario;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Estado;
import com.example.biblioteca_digital.modelos.Libro; // Necesitas el modelo Libro
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
     *  Llama a prestamos para guardar un préstamo al presionar el botón
     */
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();
    /**
     * Obtiene la conexión a la base de datos.
     */
    private Connection conectar() {
        return ConexionBD.getConexion();
    }

    /**
     * Obtiene todos los libros del catálogo.
     * @return Lista de todos los objetos Libro.
     */
    public List<Libro> obtenerTodosLosLibros() {
        List<Libro> libros = new ArrayList<>();
        String sql = "SELECT * FROM libros";

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery())
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
                        rs.getInt("cantidad"),
                        rs.getInt("cantidad_disponible"),
                        rs.getBoolean("disponible")
                );
                libros.add(libro);
            }

        } catch (SQLException e) {
            System.err.println("Error al obtener todos los libros: " + e.getMessage());
            e.printStackTrace();
        }
        return libros;
    }

    /**
     * Obtiene todos los detalles de un libro dado su ID.
     * Requerido por PrestamoDAO para cargar el objeto Libro.
     */
    public Libro obtenerLibroPorId(int idLibro) {
        Libro libro = null;
        String sql = """
        SELECT id, titulo, autor, genero, descripcion, isbn, foto, cantidad, cantidad_disponible, disponible 
        FROM libros 
        WHERE id = ?
    """;

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, idLibro);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Se asume el constructor/setters de Libro
                    libro = new Libro(
                            rs.getInt("id"),
                            rs.getString("titulo"),
                            rs.getString("autor"),
                            rs.getString("genero"),
                            rs.getString("descripcion"),
                            rs.getString("isbn"),
                            rs.getString("foto"),
                            rs.getInt("cantidad"),
                            rs.getInt("cantidad_disponible"),
                            rs.getBoolean("disponible")
                    );
                }
            }
        } catch (SQLException e) {
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
    public int contarPrestamosActivos(int idUsuario) {
        int count = 0;
        // Asumiendo que un préstamo activo no tiene fecha de devolución (fecha_devolucion IS NULL)
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_usuario = ? AND fecha_devolucion IS NULL";

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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
     * Realiza la solicitud de préstamo para un libro, actualizando la disponibilidad.
     * @param idUsuario ID del usuario que solicita el préstamo.
     * @param idLibro ID del libro solicitado.
     * @return true si el préstamo fue exitoso, false en caso contrario.
     */
    public boolean pedirPrestado(int idUsuario, int idLibro) {

        // Consultas
        String sqlCheck = "SELECT cantidad_disponible FROM libros WHERE id = ?";
        String sqlUpdate = "UPDATE libros SET cantidad_disponible = cantidad_disponible - 1 WHERE id = ?";

        // --- Datos para construir el objeto Prestamo ---
        String[] datosPrestamo = obtenerDatosUsuarioYLibro(idUsuario, idLibro);
        if (datosPrestamo == null) {
            System.err.println("Error: No se pudieron obtener datos del usuario o libro para el préstamo.");
            return false;
        }

        String nombreUsuario = datosPrestamo[0];
        String tituloLibro = datosPrestamo[1];

        LocalDate fechaInicio = LocalDate.now();
        LocalDate fechaFin = fechaInicio.plusDays(14);

        try (Connection conn = conectar())
        {
            conn.setAutoCommit(false);
            int disponibles = 0;

            // 2. VERIFICAR STOCK
            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                psCheck.setInt(1, idLibro);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        disponibles = rs.getInt("cantidad_disponible");
                    }
                }
            }

            if (disponibles <= 0) {
                conn.rollback();
                return false; // 🛑 Stock insuficiente
            }
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

            // 4. RESTAR 1 AL STOCK DISPONIBLE
            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                psUpdate.setInt(1, idLibro);
                int filasAfectadas = psUpdate.executeUpdate();
                if (filasAfectadas == 0) throw new SQLException("Fallo al actualizar stock disponible");
            }

            conn.commit(); // 5. Confirmar la transacción
            return true;
        }
        catch (SQLException e) {
            System.err.println("Error en la transacción de préstamo: " + e.getMessage());
            // Revertir si la operación falla
            try (Connection rollbackConn = conectar()) {
                rollbackConn.rollback();
            } catch (SQLException rollbackException) {
                System.err.println("Error al realizar rollback: " + rollbackException.getMessage());
            }
            e.printStackTrace();
            return false;
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
        String autor = null;
        String sql = "SELECT autor FROM libros WHERE id = ?";
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

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
