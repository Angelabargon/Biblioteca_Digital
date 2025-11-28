package com.example.biblioteca_digital.DAO.usuario;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Prestamo;
import com.example.biblioteca_digital.modelos.Usuario;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO
{
    // Instancias de DAO necesarias para obtener objetos completos (Libro y Usuario)
    private final CatalogoDAO catalogoDAO = new CatalogoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO(); // Asumiendo que existe una clase UsuarioDAO

    /**
     * Crea un nuevo préstamo y decrementa el stock del libro en una transacción atómica.
     * @param idUsuario ID del usuario.
     * @param idLibro ID del libro.
     * @return true si la transacción fue exitosa, false en caso contrario.
     */
    public boolean crearPrestamo(int idUsuario, int idLibro)
    {
        String sqlPrestamo = "INSERT INTO prestamos (id_usuario, id_libro, fecha_inicio, fecha_fin, estado) " +
                "VALUES (?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 15 DAY), 'activo')";
        // Se añade una condición de stock > 0 para hacer la operación más segura a nivel SQL
        String sqlUpdateStock = "UPDATE libros SET cantidad_disponible = cantidad_disponible - 1 WHERE id = ? AND cantidad_disponible > 0";

        Connection conn = null;

        try {
            conn = ConexionBD.getConexion();
            conn.setAutoCommit(false); // 1. INICIAR TRANSACCIÓN

            // --- 1. Crear el Préstamo ---
            try (PreparedStatement ps = conn.prepareStatement(sqlPrestamo)) {
                ps.setInt(1, idUsuario);
                ps.setInt(2, idLibro);
                ps.executeUpdate();
            }

            // --- 2. Restar Stock del Libro ---
            try (PreparedStatement ps2 = conn.prepareStatement(sqlUpdateStock)) {
                ps2.setInt(1, idLibro);
                int filasAfectadas = ps2.executeUpdate();

                if (filasAfectadas == 0) {
                    // Si no se afectó la fila, es porque el libro no existe o no había stock disponible.
                    throw new SQLException("Error: No se pudo restar el stock del libro o no hay unidades disponibles.");
                }
            }

            conn.commit(); // 3. CONFIRMAR TRANSACCIÓN
            return true;

        } catch (SQLException e) {
            System.err.println("Error en la transacción de préstamo. Realizando rollback: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback(); // 4. REVERTIR SI HAY ERROR
                } catch (SQLException rollbackException) {
                    System.err.println("Error al realizar rollback: " + rollbackException.getMessage());
                }
            }
            e.printStackTrace();
            return false;
        } finally {
            // Asegúrate de devolver el autocommit a true y cerrar la conexión
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException closeException) {
                    System.err.println("Error al cerrar conexión: " + closeException.getMessage());
                }
            }
        }
    }

    /**
     * Versión corregida y transaccional del método guardarPrestamo.
     * Delega la lógica atómica al método crearPrestamo.
     * @param prestamo Objeto Prestamo con idUsuario y idLibro.
     * @return true si la transacción fue exitosa, false en caso contrario.
     */
    public boolean guardarPrestamo(Prestamo prestamo)
    {
        // El método original tenía errores de sintaxis y omitía la transacción.
        // Se corrige delegando al método crearPrestamo, que ya implementa la atomicidad.
        return crearPrestamo(prestamo.getUsuario().getId(), prestamo.getLibro().getId());
    }

    /**
     * Obtiene todos los préstamos activos de un usuario, incluyendo los datos completos del Libro y Usuario.
     */
    public List<Prestamo> obtenerPrestamosDeUsuario(int idUsuario)
    {
        List<Prestamo> lista = new ArrayList<>();
        String sql = """
        SELECT id, id_usuario, id_libro, fecha_inicio, fecha_fin, estado
        FROM prestamos 
        WHERE id_usuario = ? AND estado = 'activo'
        """;
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql))
        {
            pst.setInt(1, idUsuario);
            try (ResultSet rs = pst.executeQuery())
            {
                while (rs.next())
                {
                    int idLibro = rs.getInt("id_libro");
                    int id_usuario_actual = rs.getInt("id_usuario");
                    Prestamo p = new Prestamo();
                    p.setId(rs.getInt("id"));
                    p.getUsuario().setId(id_usuario_actual);
                    p.getLibro().setId(idLibro);
                    p.setFecha_inicio(rs.getDate("fecha_inicio").toLocalDate());
                    p.setFecha_fin(rs.getDate("fecha_fin").toLocalDate());
                    p.setEstado(rs.getString("estado"));

                    // Se usan las instancias de campo para obtener los objetos completos
                    Libro libroCompleto = catalogoDAO.obtenerLibroPorId(idLibro);
                    Usuario usuarioCompleto = usuarioDAO.obtenerUsuarioPorId(id_usuario_actual);

                    p.setLibro(libroCompleto);
                    p.setUsuario(usuarioCompleto);

                    lista.add(p);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Cuenta el número de préstamos activos de un usuario.
     */
    public static int contarPrestamosActivos(int idUsuario)
    {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_usuario = ? AND estado = 'activo'";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql))
        {
            pst.setInt(1, idUsuario);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Coger los ID de los libros que el usuario tiene prestado
     * @param idUsuario
     * @return
     */
    public List<Integer> obtenerIdsLibrosPrestados(int idUsuario) {
        List<Integer> ids = new ArrayList<>();
        // Asegúrate de que tu tabla 'prestamos' tiene una columna 'id_libro' y 'fecha_devolucion' es NULL para los activos
        String sql = "SELECT id_libro FROM prestamos WHERE id_usuario = ? AND fecha_devolucion IS NULL";
        // ... (Lógica de conexión y ejecución de consulta)
        return ids;
    }
}