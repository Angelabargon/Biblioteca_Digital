package com.example.biblioteca_digital.DAO.usuario;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Reseña;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReseñasDAO {

    /**
     * Inserta una nueva reseña en la base de datos.
     * Usa CURRENT_DATE para la fecha.
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
                    Reseña r = new Reseña(
                            rs.getInt("id"),
                            rs.getInt("id_libro"),
                            rs.getInt("id_usuario"),
                            rs.getDate("fecha"),
                            rs.getString("contenido"),
                            rs.getInt("calificacion")
                    );
                    // Rellenamos los campos extra del modelo
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
}