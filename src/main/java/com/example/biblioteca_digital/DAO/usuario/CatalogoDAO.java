package com.example.biblioteca_digital.DAO.usuario;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Libro;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CatalogoDAO
{
    private List<Libro> obtenerTodosLosLibros() {
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery())
        {
            while (rs.next())
            {
                Libro libro = new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("descripcion"),
                        rs.getString("genero"),
                        rs.getString("isbn"),
                        rs.getString("foto"),
                        rs.getInt("cantidad"),
                        rs.getBoolean("disponible")
                );
                lista.add(libro);
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return lista;
    }
}
