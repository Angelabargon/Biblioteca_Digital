package com.example.biblioteca_digital.DAO.usuario;

/**
 * Imports necesarios.
 */
import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Reseña;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO encargado de gestionar las operaciones relacionadas con las reseñas
 * en la base de datos.
 *
 * Este DAO se utiliza desde ControladorReseñas y desde la vista individual del libro.
 */
public class ReseñasDAO {

    /**
     * Inserta una nueva reseña en la base de datos.
     * Usa CURRENT_DATE para la fecha.
     *
     *  @param reseña Objeto Reseña con los datos a guardar.
     *  @return true si la inserción fue exitosa, false si ocurrió un error.
     */
    public boolean guardarReseña(Reseña reseña) {
        String sql = "INSERT INTO resenas (id_usuario, id_libro, contenido, calificacion, fecha) " +
                "VALUES (?, ?, ?, ?, CURRENT_DATE)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, reseña.getId_usuario());
            ps.setInt(2, reseña.getId_libro());
            ps.setString(3, reseña.getContenido());
            ps.setInt(4, reseña.getCalificacion());

            int filas = ps.executeUpdate();

            // Al insertarse, se obtendrá el ID generado.
            if (filas > 0) {
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) reseña.setId(keys.getInt(1));
                }
                return true;
            }
            return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Obtiene todas las reseñas de un libro, incluyendo nombre de usuario y título del libro.
     * Ordenadas por fecha descendente.
     *
     * @param idLibro ID del libro.
     * @return Lista de reseñas del libro.
     */
    public List<Reseña> obtenerReseñasPorLibro(int idLibro) {
        List<Reseña> lista = new ArrayList<>();

        String sql = "SELECT r.id, r.id_usuario, r.id_libro, r.contenido, r.calificacion, r.fecha, " +
                "u.nombre_usuario, l.titulo " +
                "FROM resenas r " +
                "JOIN usuarios u ON r.id_usuario = u.id " +
                "JOIN libros l ON r.id_libro = l.id " +
                "WHERE r.id_libro = ? ORDER BY r.fecha DESC";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idLibro);

            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {

                    // Construcción del objeto reseña con los datos principales.
                    Reseña r = new Reseña(
                            rs.getInt("id"),
                            rs.getInt("id_libro"),
                            rs.getInt("id_usuario"),
                            rs.getDate("fecha"),
                            rs.getString("contenido"),
                            rs.getInt("calificacion")
                    );

                    // Rellenamos los campos extra del modelo.
                    r.setNombreUsuario(rs.getString("nombre_usuario"));
                    r.setTituloLibro(rs.getString("titulo"));

                    lista.add(r);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Elimina una reseña por ID.
     *
     * @param idReseña ID de la reseña a eliminar.
     * @return true si se eliminó correctamente, false si ocurrió un error.
     */
    public boolean eliminarReseña(int idReseña) {
        String sql = "DELETE FROM resenas WHERE id = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idReseña);
            return ps.executeUpdate() > 0;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public double obtenerPuntuacionMedia(int idLibro) {
        String sql = "SELECT AVG(calificacion) FROM resenas WHERE id_libro = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, idLibro);

            try (ResultSet rs = ps.executeQuery()) {

                if (rs.next()) {
                    return rs.getDouble(1); // devuelve la media
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } return 0.0; // si no hay reseñas
    }
}