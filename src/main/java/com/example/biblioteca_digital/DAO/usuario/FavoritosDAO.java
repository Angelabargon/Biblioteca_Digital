package com.example.biblioteca_digital.DAO.usuario;

/**
 * Imports necesarios de la clase.
 */
import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Libro;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO encargado de gestionar operaciones relacionadas con los libros en favoritos.
 * Este DAO se utiliza en la vista de favoritos
 */
public class FavoritosDAO {

    /** Conexión con la base de datos */
    private static Connection conectar()
    {
        return ConexionBD.getConexion();
    }

    /**
     * Método que obtiene todos los libros marcados como favoritos por un usuario
     * @param idUsuario ID del usuario
     * @return Lista de objetos Libro
     */
    public List<Libro> obtenerFavoritos(int idUsuario) {
        List<Libro> lista = new ArrayList<>();
        String sql = """
                SELECT l.* FROM libros l 
                JOIN favoritos f ON l.id = f.id_libro
                WHERE f.id_usuario = ?
                """;

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
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
                        rs.getString("contenido"),
                        rs.getInt("duracion_prestamo")
                );
                lista.add(libro);
            }

        } catch (Exception e) {
            System.err.println("Error al obtener favoritos: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Método que elimina un libro de la lista de favoritos de un usuario
     *
     * @param idUsuario ID del usuario
     * @param idLibro   ID del libro a borrar
     */
    public void borrarFavorito(int idUsuario, int idLibro) {
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM favoritos WHERE id_usuario = ? AND id_libro = ?"))
        {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idLibro);
            int filasAfectadas = ps.executeUpdate();

        } catch (Exception e) {
            System.err.println("Error al borrar favorito: " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Método que comprueba si el libro es favorito en la sesion del usuario
     * @param idUsuario
     * @param idLibro
     * @return
     */
    public boolean esFavorito(int idUsuario, int idLibro) {
        String sql = "SELECT 1 FROM favoritos WHERE id_usuario = ? AND id_libro = ?";

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idLibro);

            try (ResultSet rs = ps.executeQuery())
            {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Mñetodo que cambia el estado de favorito dependiendo de si presiona el botón o no
     * @param idUsuario
     * @param idLibro
     */
    public void alternarFavorito(int idUsuario, int idLibro) {
        if (esFavorito(idUsuario, idLibro)) {
            borrarFavorito(idUsuario, idLibro);

        } else {
            agregarFavorito(idUsuario, idLibro);
        }
    }

    /**
     * Método que añade un libro a la lista favoritos
     *
     * @param idUsuario
     * @param idLibro
     */
    public static void agregarFavorito(int idUsuario, int idLibro) {
        String sql = "INSERT INTO favoritos(id_usuario, id_libro) VALUES (?, ?)";

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql))
        {
            ps.setInt(1, idUsuario);
            ps.setInt(2, idLibro);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}