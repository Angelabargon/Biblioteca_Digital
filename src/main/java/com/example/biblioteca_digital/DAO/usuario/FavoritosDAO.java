package com.example.biblioteca_digital.DAO.usuario;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Usuario;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import static com.example.biblioteca_digital.modelos.Sesion.usuarioActual;

public class FavoritosDAO
{
    Usuario usuarioactual = usuarioActual;
    private List<Integer> obtenerFavoritosUsuario(int idUsuario) {
        List<Integer> lista = new ArrayList<>();
        String sql = "SELECT id_libro FROM favoritos WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql))
        {
            pst.setInt(1, idUsuario);
            ResultSet rs = pst.executeQuery();
            while (rs.next())
            {
                lista.add(rs.getInt("id_libro"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return lista;
    }

    private void alternarFavorito(int idLibro)
    {
        if (favoritos.contains(idLibro))
        {
            eliminarFavorito(idLibro);
        }
        else
        {
            insertarFavorito(idLibro);
        }
        // Actualizar la lista local y el contador
        favoritos = obtenerFavoritosUsuario(usuarioActual.getId());
        if (labelContadorFavoritos != null) labelContadorFavoritos.setText(String.valueOf(favoritos.size()));

        // Refrescar el catálogo para actualizar el ícono de corazón
        mostrarLibrosFiltrados();
    }

    private void insertarFavorito(int idLibro) { /* ... Tu lógica de DB ... */
        String sql = "INSERT INTO favoritos (id_usuario, id_libro) VALUES (?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql))
        {
            pst.setInt(1, usuarioActual.getId());
            pst.setInt(2, idLibro);
            pst.executeUpdate();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void eliminarFavorito(int idLibro)
    {
        String sql = "DELETE FROM favoritos WHERE id_usuario = ? AND id_libro = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql))
        {
            pst.setInt(1, usuarioActual.getId());
            pst.setInt(2, idLibro);
            pst.executeUpdate();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
