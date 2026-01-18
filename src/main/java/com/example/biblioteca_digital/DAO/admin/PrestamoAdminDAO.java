package com.example.biblioteca_digital.DAO.admin;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Prestamo;
import com.example.biblioteca_digital.modelos.Rol;
import com.example.biblioteca_digital.modelos.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO de administración de préstamos.
 * <p>
 * Esta clase se encarga de gestionar todas las operaciones relacionadas con
 * los préstamos en la base de datos, incluyendo:
 * <ul>
 *     <li>Listado completo de préstamos</li>
 *     <li>Creación de nuevos préstamos</li>
 *     <li>Actualización de préstamos existentes</li>
 *     <li>Eliminación de préstamos y devolución automática del libro</li>
 *     <li>Obtención de estadísticas</li>
 * </ul>
 */
public class PrestamoAdminDAO {

    /**
     * Obtiene todos los préstamos registrados en el sistema.
     * <p>
     * Cada préstamo incluye su usuario asociado y el libro correspondiente.
     *
     * @return lista de préstamos ordenados por fecha de inicio descendente
     */
    public List<Prestamo> obtenerTodos() {

        List<Prestamo> lista = new ArrayList<>();

        String sql = """
            SELECT 
                p.id, p.fecha_inicio, p.fecha_fin, p.estado,

                u.id AS uid, u.nombre_usuario, u.nombre, u.primer_apellido, u.correo, u.rol,
                
                l.id AS lid, l.titulo, l.autor, l.descripcion, 
                l.genero, l.isbn, l.foto, l.cantidad, l.cantidad_disponible, l.disponible

            FROM prestamos p
            JOIN usuarios u ON p.id_usuario = u.id
            JOIN libros l ON p.id_libro = l.id
            ORDER BY p.fecha_inicio DESC
        """;

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {

                // Construcción del usuario
                Usuario u = new Usuario();
                u.setId(rs.getInt("uid"));
                u.setNombreUsuario(rs.getString("nombre_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setPrimerApellido(rs.getString("primer_apellido"));
                u.setCorreo(rs.getString("correo"));
                u.setRol(Rol.valueOf(rs.getString("rol")));

                // Construcción del libro
                Libro l = new Libro();
                l.setId(rs.getInt("lid"));
                l.setTitulo(rs.getString("titulo"));
                l.setAutor(rs.getString("autor"));
                l.setDescripcion(rs.getString("descripcion"));
                l.setGenero(rs.getString("genero"));
                l.setIsbn(rs.getString("isbn"));
                l.setFoto(rs.getString("foto"));
                l.setCantidad(rs.getInt("cantidad"));
                l.setCantidadDisponible(rs.getInt("cantidad_disponible"));
                l.setDisponible(rs.getBoolean("disponible"));

                // Construcción del préstamo
                Prestamo p = new Prestamo();
                p.setId(rs.getInt("id"));
                p.setUsuario(u);
                p.setLibro(l);
                p.setFecha_inicio(rs.getDate("fecha_inicio").toLocalDate());
                p.setFecha_fin(rs.getDate("fecha_fin").toLocalDate());
                p.setEstado(rs.getString("estado"));

                lista.add(p);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Crea un nuevo préstamo en la base de datos.
     * <p>
     * Este método:
     * <ul>
     *     <li>Bloquea el registro del libro</li>
     *     <li>Comprueba que haya disponibilidad</li>
     *     <li>Inserta el préstamo</li>
     *     <li>Reduce la cantidad disponible del libro</li>
     * </ul>
     *
     * @param prestamo préstamo a crear
     * @return true si se creó correctamente, false en caso contrario
     */
    public boolean crearPrestamo(Prestamo prestamo) {

        String bloquearLibro =
                "SELECT cantidad_disponible FROM libros WHERE id = ? FOR UPDATE";

        String insertarPrestamo =
                "INSERT INTO prestamos (id_usuario, id_libro, fecha_inicio, fecha_fin, estado) VALUES (?, ?, ?, ?, ?)";

        String actualizarLibro =
                "UPDATE libros SET cantidad_disponible = cantidad_disponible - 1, " +
                        "disponible = (cantidad_disponible - 1) > 0 WHERE id = ?";

        Connection con = null;

        try {
            con = ConexionBD.getConexion();
            con.setAutoCommit(false);

            // Bloqueo y comprobación de disponibilidad
            try (PreparedStatement ps = con.prepareStatement(bloquearLibro)) {
                ps.setInt(1, prestamo.getLibro().getId());
                ResultSet rs = ps.executeQuery();

                if (!rs.next() || rs.getInt("cantidad_disponible") <= 0) {
                    con.rollback();
                    return false;
                }
            }

            // Inserción del préstamo
            try (PreparedStatement ps = con.prepareStatement(insertarPrestamo, Statement.RETURN_GENERATED_KEYS)) {

                ps.setInt(1, prestamo.getUsuario().getId());
                ps.setInt(2, prestamo.getLibro().getId());
                ps.setDate(3, Date.valueOf(prestamo.getFecha_inicio()));
                ps.setDate(4, Date.valueOf(prestamo.getFecha_fin()));
                ps.setString(5, prestamo.getEstado());

                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) {
                    prestamo.setId(keys.getInt(1));
                }
            }

            // Actualización del libro
            try (PreparedStatement ps = con.prepareStatement(actualizarLibro)) {
                ps.setInt(1, prestamo.getLibro().getId());
                ps.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {

            e.printStackTrace();
            try {
                if (con != null) con.rollback();
            } catch (SQLException ignored) {}

            return false;

        } finally {
            try {
                if (con != null) con.setAutoCommit(true);
            } catch (SQLException ignored) {}
        }
    }

    /**
     * Actualiza los datos de un préstamo existente.
     *
     * @param p préstamo con los datos actualizados
     * @return true si se actualizó correctamente, false en caso contrario
     */
    public boolean actualizarPrestamo(Prestamo p) {

        String sql = """
            UPDATE prestamos
            SET id_usuario = ?, id_libro = ?, fecha_fin = ?, estado = ?
            WHERE id = ?
        """;

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, p.getUsuario().getId());
            ps.setInt(2, p.getLibro().getId());
            ps.setDate(3, Date.valueOf(p.getFecha_fin()));
            ps.setString(4, p.getEstado());
            ps.setInt(5, p.getId());

            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Elimina un préstamo y devuelve automáticamente el libro asociado.
     *
     * @param idPrestamo identificador del préstamo
     * @return true si se eliminó correctamente, false en caso contrario
     */
    public boolean eliminarPrestamo(int idPrestamo) {

        String obtenerLibro =
                "SELECT id_libro FROM prestamos WHERE id = ?";

        String eliminar =
                "DELETE FROM prestamos WHERE id = ?";

        String devolver = """
            UPDATE libros
            SET cantidad_disponible = cantidad_disponible + 1,
                disponible = TRUE
            WHERE id = ?
        """;

        try (Connection con = ConexionBD.getConexion()) {

            con.setAutoCommit(false);

            int idLibro = -1;

            // Obtener el libro asociado
            try (PreparedStatement ps = con.prepareStatement(obtenerLibro)) {
                ps.setInt(1, idPrestamo);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) {
                    idLibro = rs.getInt("id_libro");
                }
            }

            if (idLibro == -1) {
                con.rollback();
                return false;
            }

            // Eliminar préstamo
            try (PreparedStatement ps = con.prepareStatement(eliminar)) {
                ps.setInt(1, idPrestamo);
                ps.executeUpdate();
            }

            // Devolver libro
            try (PreparedStatement ps = con.prepareStatement(devolver)) {
                ps.setInt(1, idLibro);
                ps.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Cuenta los préstamos activos y no vencidos.
     *
     * @return número de préstamos activos
     */
    public long contarPrestamosActivos() {

        String sql = """
            SELECT COUNT(*)
            FROM prestamos
            WHERE estado = 'activo'
            AND fecha_fin >= CURRENT_DATE()
        """;

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getLong(1) : 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    /**
     * Cuenta los préstamos activos que están vencidos.
     *
     * @return número de préstamos vencidos
     */
    public long contarPrestamosVencidos() {

        String sql = """
            SELECT COUNT(*)
            FROM prestamos
            WHERE estado = 'activo'
            AND fecha_fin < CURRENT_DATE()
        """;

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            return rs.next() ? rs.getLong(1) : 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }
}