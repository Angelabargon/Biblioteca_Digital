package com.example.biblioteca_digital.DAO.usuario; // Asumiendo el paquete 'dao'

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Libro; // Necesitas el modelo Libro
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogoDAO {

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
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Libro libro = new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("genero"),
                        rs.getString("isbn"),
                        rs.getString("descripcion"),
                        rs.getString("foto"),
                        rs.getInt("cantidad"),
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
        // Asumimos que la lógica de límite de préstamos se verifica en el Controlador.
        // Aquí solo manejamos la transacción de la BD.

        try (Connection conn = conectar()) {
            // Desactivamos auto-commit para asegurar que ambas operaciones (INSERT y UPDATE)
            // se realicen juntas (transacción atómica).
            conn.setAutoCommit(false);

            // 1. Verificar si hay unidades disponibles
            String sqlCheck = "SELECT disponibles FROM libros WHERE id = ?";
            int disponibles = 0;

            try (PreparedStatement psCheck = conn.prepareStatement(sqlCheck)) {
                psCheck.setInt(1, idLibro);
                try (ResultSet rs = psCheck.executeQuery()) {
                    if (rs.next()) {
                        disponibles = rs.getInt("disponibles");
                    }
                }
            }

            if (disponibles <= 0) {
                conn.rollback();
                return false; // No hay unidades disponibles
            }

            // 2. Insertar el nuevo préstamo
            // Asumiendo que la fecha_prestamo es la fecha actual y la fecha_vencimiento es calculada
            // (por simplicidad, no la calculamos aquí, se puede hacer en SQL o en Java)
            String sqlInsert = "INSERT INTO prestamos (id_usuario, id_libro, fecha_prestamo, fecha_vencimiento) VALUES (?, ?, CURDATE(), DATE_ADD(CURDATE(), INTERVAL 14 DAY))";

            try (PreparedStatement psInsert = conn.prepareStatement(sqlInsert)) {
                psInsert.setInt(1, idUsuario);
                psInsert.setInt(2, idLibro);
                psInsert.executeUpdate();
            }

            // 3. Actualizar la disponibilidad del libro (disponibles = disponibles - 1)
            String sqlUpdate = "UPDATE libros SET disponibles = disponibles - 1 WHERE id = ?";
            try (PreparedStatement psUpdate = conn.prepareStatement(sqlUpdate)) {
                psUpdate.setInt(1, idLibro);
                psUpdate.executeUpdate();
            }

            conn.commit(); // Confirmar la transacción
            return true;

        } catch (SQLException e) {
            System.err.println("Error en la transacción de préstamo: " + e.getMessage());
            // Si algo falla, revertir los cambios
            try (Connection conn = conectar()) {
                conn.rollback();
            } catch (SQLException rollbackException) {
                System.err.println("Error al realizar rollback: " + rollbackException.getMessage());
            }
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene el autor de un libro dado su ID.
     * @param idLibro ID del libro.
     * @return Nombre del autor o null si no se encuentra.
     */
    public String obtenerAutorPorIdLibro(int idLibro) {
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
