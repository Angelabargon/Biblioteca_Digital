package com.example.biblioteca_digital.DAO.admin;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Prestamo;
import com.example.biblioteca_digital.modelos.Rol;
import com.example.biblioteca_digital.modelos.Usuario;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO encargado de gestionar todas las operaciones relacionadas con los
 * préstamos de libros en el sistema. Incluye métodos para obtener listados
 * completos con JOIN, crear préstamos con control transaccional, eliminarlos
 * devolviendo automáticamente la disponibilidad del libro, y estadísticas de uso.
 *
 * <p>Utiliza {@link ConexionBD} para gestionar la conexión con la base de datos
 * y aprovecha transacciones SQL para asegurar la integridad en operaciones
 * críticas como crear o eliminar un préstamo.</p>
 */
public class PrestamoAdminDAO {

    /**
     * Obtiene todos los préstamos registrados en el sistema, incluyendo
     * información del usuario y del libro asociado mediante sentencias JOIN.
     *
     * @return lista de objetos {@link Prestamo}; nunca es null, aunque puede estar vacía.
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
                Usuario u = new Usuario();
                u.setId(rs.getInt("uid"));
                u.setNombreUsuario(rs.getString("nombre_usuario"));
                u.setNombre(rs.getString("nombre"));
                u.setPrimerApellido(rs.getString("primer_apellido"));
                u.setCorreo(rs.getString("correo"));
                u.setRol(Rol.valueOf(rs.getString("rol")));

                // libro
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

                // prestamo
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
     * El proceso es transaccional e incluye:
     * <ul>
     *   <li>Bloqueo del libro mediante <code>SELECT ... FOR UPDATE</code></li>
     *   <li>Verificación de disponibilidad</li>
     *   <li>Inserción del préstamo</li>
     *   <li>Actualización de la cantidad disponible del libro</li>
     * </ul>
     * Si cualquiera de estos pasos falla, se ejecuta un rollback para mantener la integridad.
     *
     * @param prestamo objeto {@link Prestamo} que contiene los datos del préstamo a registrar.
     * @return true si el préstamo se creó correctamente; false si no había stock o ocurrió un error.
     */
    public boolean crearPrestamo(Prestamo prestamo) {
        String bloquearLibro = "SELECT cantidad_disponible FROM libros WHERE id = ? FOR UPDATE";
        String insertarPrestamo =
                "INSERT INTO prestamos (id_usuario, id_libro, fecha_inicio, fecha_fin, estado) VALUES (?, ?, ?, ?, ?)";
        String actualizarLibro =
                "UPDATE libros SET cantidad_disponible = cantidad_disponible - 1, disponible = (cantidad_disponible - 1) > 0 WHERE id = ?";

        Connection con = null;
        try {
            con = ConexionBD.getConexion();
            con.setAutoCommit(false);

            // Bloquear fila del libro
            try (PreparedStatement ps = con.prepareStatement(bloquearLibro)) {
                ps.setInt(1, prestamo.getLibro().getId());
                ResultSet rs = ps.executeQuery();
                if (!rs.next() || rs.getInt("cantidad_disponible") <= 0) {
                    con.rollback();
                    return false;
                }
            }

            // Insertar préstamo
            try (PreparedStatement ps = con.prepareStatement(insertarPrestamo, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, prestamo.getUsuario().getId());
                ps.setInt(2, prestamo.getLibro().getId());
                ps.setDate(3, Date.valueOf(prestamo.getFecha_inicio()));
                ps.setDate(4, Date.valueOf(prestamo.getFecha_fin()));
                ps.setString(5, prestamo.getEstado() != null ? prestamo.getEstado() : null);

                ps.executeUpdate();

                ResultSet keys = ps.getGeneratedKeys();
                if (keys.next()) prestamo.setId(keys.getInt(1));
            }

            // Actualizar libro
            try (PreparedStatement ps = con.prepareStatement(actualizarLibro)) {
                ps.setInt(1, prestamo.getLibro().getId());
                ps.executeUpdate();
            }

            con.commit();
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            try { if (con != null) con.rollback(); } catch (SQLException ex) {}
            return false;
        } finally {
            try { if (con != null) con.setAutoCommit(true); } catch (SQLException e) {}
        }
    }

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
     * Elimina un préstamo existente y devuelve automáticamente el libro,
     * incrementando su cantidad disponible.
     * <p>
     * La operación es transaccional: si alguna parte falla, se cancela todo.
     *
     * @param idPrestamo identificador del préstamo que se desea eliminar.
     * @return true si el préstamo fue eliminado correctamente; false si no existe o ocurre un error.
     */
    public boolean eliminarPrestamo(int idPrestamo) {
        String obtenerLibro = "SELECT id_libro FROM prestamos WHERE id = ?";
        String eliminar = "DELETE FROM prestamos WHERE id = ?";
        String devolver = """
            UPDATE libros 
            SET cantidad_disponible = cantidad_disponible + 1,
                disponible = TRUE
            WHERE id = ?
        """;

        try (Connection con = ConexionBD.getConexion()) {

            con.setAutoCommit(false);

            int idLibro = -1;

            // obtener id_libro
            try (PreparedStatement ps = con.prepareStatement(obtenerLibro)) {
                ps.setInt(1, idPrestamo);
                ResultSet rs = ps.executeQuery();
                if (rs.next()) idLibro = rs.getInt("id_libro");
            }

            if (idLibro == -1) { con.rollback(); return false; }

            // eliminar préstamo
            try (PreparedStatement ps = con.prepareStatement(eliminar)) {
                ps.setInt(1, idPrestamo);
                ps.executeUpdate();
            }

            // devolver libro
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
     * Cuenta el número total de préstamos que se encuentran actualmente activos.
     *
     * @return número de préstamos con estado "activo"; 0 en caso de error.
     */
    public long contarPrestamosActivos() {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE estado = 'activo'";
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
     * Cuenta la cantidad de préstamos vencidos, es decir,
     * aquellos cuya fecha de fin ha pasado y siguen en estado "activo".
     *
     * @return número de préstamos vencidos; 0 si ocurre un error.
     */
    public long contarPrestamosVencidos() {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE fecha_fin < CURRENT_DATE() AND estado = 'activo'";
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