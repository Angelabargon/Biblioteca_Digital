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
    // Inicializo los DAOs utilizados
    private CatalogoDAO catalogoDAO = new CatalogoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO();

    /**
     * Método que crea un nuevo préstamo y decrementa el stock del libro en una transacción atómica
     * @param idUsuario ID del usuario
     * @param idLibro ID del libro
     * @return true si la transacción fue exitosa, false en caso contrario.
     */
    public boolean crearPrestamo(int idUsuario, int idLibro)
    {
        String sqlPrestamo = "INSERT INTO prestamos (id_usuario, id_libro, fecha_inicio, fecha_fin, estado) " +
                "VALUES (?, ?, NOW(), DATE_ADD(NOW(), INTERVAL (SELECT duracion_prestamo FROM libros WHERE id = ?) DAY), 'activo')";
        String sqlUpdateStock = "UPDATE libros SET cantidad_disponible = cantidad_disponible - 1 WHERE id = ? AND cantidad_disponible > 0";

        Connection conn = null;

        try
        {
            conn = ConexionBD.getConexion();
            conn.setAutoCommit(false);

            // 1. Crear el Préstamo
            try (PreparedStatement ps = conn.prepareStatement(sqlPrestamo)) {
                ps.setInt(1, idUsuario);
                ps.setInt(2, idLibro);
                ps.setInt(3, idLibro);
                ps.executeUpdate();
            }

            // 2. Restar Stock del Libro
            try (PreparedStatement ps2 = conn.prepareStatement(sqlUpdateStock)) {
                ps2.setInt(1, idLibro);
                int filasAfectadas = ps2.executeUpdate();

                if (filasAfectadas == 0)
                {
                    throw new SQLException("Error: No se pudo restar el stock del libro o no hay unidades disponibles.");
                }
            }

            conn.commit();
            return true;

        } catch (SQLException e)
        {
            System.err.println("Error en la transacción de préstamo. Realizando rollback: " + e.getMessage());
            if (conn != null) {
                try {
                    conn.rollback();
                } catch (SQLException rollbackException)
                {
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
                try
                {
                    conn.setAutoCommit(true);
                    conn.close();
                }
                catch (SQLException closeException)
                {
                    System.err.println("Error al cerrar conexión: " + closeException.getMessage());
                }
            }
        }
    }

    /**
     * Método que obtiene todos los préstamos activos de un usuario.
     */
    public List<Prestamo> obtenerPrestamosDeUsuario(int idUsuario)
    {
        List<Prestamo> prestamos = new ArrayList<>();
        String sql = """
        SELECT 
        p.id AS idPrestamo, p.fecha_inicio, p.fecha_fin, p.estado,
        l.id AS idLibro, l.titulo AS tituloLibro, l.autor AS autorLibro, 
        l.contenido AS contenidoLibro, l.duracion_prestamo, -- Añadimos la columna
        u.id AS idUsuario, u.nombre AS nombreUsuario
    FROM prestamos p
    JOIN libros l ON p.id_libro = l.id
    JOIN usuarios u ON p.id_usuario = u.id
    WHERE p.id_usuario = ? AND p.estado = 'activo'
    """;
        // Abrir Connection y PreparedStatement usando try-with-resources
        try (Connection conn = ConexionBD.getConexion();
             PreparedStatement pst = conn.prepareStatement(sql))
        {
            pst.setInt(1, idUsuario);
            try (ResultSet rs = pst.executeQuery())
            {
                while (rs.next())
                {
                    Prestamo p = new Prestamo();
                    p.setId(rs.getInt("idPrestamo"));
                    p.setFecha_inicio(rs.getDate("fecha_inicio").toLocalDate());
                    p.setFecha_fin(rs.getDate("fecha_fin").toLocalDate());
                    p.setEstado(rs.getString("estado"));

                    Libro libro = new Libro();
                    libro.setId(rs.getInt("idLibro"));
                    libro.setTitulo(rs.getString("tituloLibro"));
                    libro.setAutor(rs.getString("autorLibro"));
                    libro.setContenido(rs.getString("contenidoLibro"));
                    libro.setDuracion(rs.getInt("duracion_prestamo"));
                    p.setLibro(libro);

                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("idUsuario"));
                    usuario.setNombre(rs.getString("nombreUsuario"));
                    p.setUsuario(usuario);

                    prestamos.add(p);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prestamos;
    }

    /**
     * Metodo que comprueba si el libro está en préstamo por el usuario o no
     * @param idUsuario
     * @param idLibro
     * @return
     */
    public boolean esLibroPrestadoPorUsuario(int idUsuario, int idLibro)
    {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_usuario = ? AND id_libro = ? AND estado = 'activo'";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql))
        {
            pst.setInt(1, idUsuario);
            pst.setInt(2, idLibro);
            try (ResultSet rs = pst.executeQuery())
            {
                if (rs.next())
                {
                    return rs.getInt(1) > 0;
                }
            }
        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return false;
    }
    public void setCatalogoDAO(CatalogoDAO catalogoDAO)
    {
        this.catalogoDAO = catalogoDAO;
    }

    public void eliminarPrestamo(int idPrestamo) throws SQLException
    {
        String sqlObtenerLibro =
                "SELECT id_libro FROM prestamos WHERE id = ?";
        String sqlActualizarPrestamo =
                "UPDATE prestamos SET estado = 'devuelto' WHERE id = ?";
        String sqlActualizarStock =
                "UPDATE libros SET cantidad_disponible = cantidad_disponible + 1 WHERE id = ?";
        Connection conn = null;
        try
        {
            conn = ConexionBD.getConexion();
            conn.setAutoCommit(false);
            int idLibro;
            try (PreparedStatement pst = conn.prepareStatement(sqlObtenerLibro))
            {
                pst.setInt(1, idPrestamo);
                try (ResultSet rs = pst.executeQuery())
                {
                    if (!rs.next())
                    {
                        throw new SQLException("No se encontró el préstamo con id " + idPrestamo);
                    }
                    idLibro = rs.getInt("id_libro");
                }
            }
            try (PreparedStatement pst = conn.prepareStatement(sqlActualizarPrestamo))
            {
                pst.setInt(1, idPrestamo);
                pst.executeUpdate();
            }
            try (PreparedStatement pst = conn.prepareStatement(sqlActualizarStock))
            {
                pst.setInt(1, idLibro);
                pst.executeUpdate();
            }
            conn.commit();
        }
        catch (SQLException e)
        {
            if (conn != null)
            {
                try
                {
                    conn.rollback();
                }
                catch (SQLException rollbackEx)
                {
                    rollbackEx.addSuppressed(e);
                    throw rollbackEx;
                }
            }
            throw e;
        }
        finally
        {
            if (conn != null)
            {
                try
                {
                    conn.setAutoCommit(true);
                    conn.close();
                }
                catch (SQLException closeEx)
                {
                    throw closeEx;
                }
            }
        }
    }
}