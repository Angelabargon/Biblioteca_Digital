package com.example.biblioteca_digital.DAO.usuario;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class FavoritosDAO {

    /**
     * Obtiene la conexión a la base de datos.
     */
    private Connection conectar() {
        // Asumiendo que ConexionBD.getConexion() maneja la apertura de la conexión
        return ConexionBD.getConexion();
    }

    /**
     * Obtiene todos los libros marcados como favoritos por un usuario.
     * @param idUsuario ID del usuario.
     * @return Lista de objetos Libro.
     */
    public List<Libro> obtenerFavoritos(int idUsuario) {
        List<Libro> lista = new ArrayList<>();

        String sql = """
                SELECT l.* FROM libros l 
                JOIN favoritos f ON l.id = f.id_libro
                WHERE f.id_usuario = ?
                """;

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                // Se asume que el constructor de Libro tiene la siguiente firma:
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
                lista.add(libro);
            }

        } catch (Exception e) {
            System.err.println("Error al obtener favoritos: " + e.getMessage());
            e.printStackTrace();
        }

        return lista;
    }

    /**
     * Elimina un libro de la lista de favoritos de un usuario.
     * @param idUsuario ID del usuario.
     * @param idLibro ID del libro a borrar.
     */
    public boolean borrarFavorito(int idUsuario, int idLibro) {
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM favoritos WHERE id_usuario = ? AND id_libro = ?")) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idLibro);

            int filasAfectadas = ps.executeUpdate();
            return filasAfectadas > 0; // Retorna true si se eliminó al menos una fila

        } catch (Exception e) {
            System.err.println("Error al borrar favorito: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }
}