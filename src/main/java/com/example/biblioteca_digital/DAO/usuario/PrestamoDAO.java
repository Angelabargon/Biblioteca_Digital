package com.example.biblioteca_digital.DAO.usuario;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Prestamo;
import com.example.biblioteca_digital.modelos.Estado;
import com.example.biblioteca_digital.modelos.Usuario;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrestamoDAO
{
    /**
     * Guarda un nuevo préstamo en la base de datos.
     */
    public boolean guardarPrestamo(Prestamo prestamo)
    {
        String sql = """
        INSERT INTO prestamos 
        (id_usuario, nombre_usuario, id_libro, titulo_libro, fecha_inicio, fecha_fin, estado)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql))
        {
            pst.setInt(1, prestamo.getId_usuario());
            pst.setString(2, prestamo.getNombreUsuario());
            pst.setInt(3, prestamo.getId_libro());
            pst.setString(4, prestamo.getTituloLibro());
            pst.setDate(5, Date.valueOf(prestamo.getFecha_inicio()));
            pst.setDate(6, Date.valueOf(prestamo.getFecha_fin()));
            pst.setString(7, prestamo.getEstado());
            return pst.executeUpdate() > 0;
        }
        catch (SQLException e)
        {
            e.printStackTrace();
            return false;
        }
    }
    public List<Prestamo> obtenerPrestamosDeUsuario(int idUsuario)
    {
        List<Prestamo> lista = new ArrayList<>();
        String sql = """
        SELECT * FROM prestamos 
        WHERE id_usuario = ? AND estado = 'activo'
    """;
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql))
        {
            pst.setInt(1, idUsuario);
            try (ResultSet rs = pst.executeQuery())
            {
                while (rs.next())
                {
                    Libro libro = new Libro(
                            rs.getInt("id_libro"),
                            rs.getString("titulo_libro")
                    );

                    Usuario user = new Usuario();
                    user.setId(rs.getInt("id_usuario"));
                    user.setNombre(rs.getString("nombre_usuario"));

                    Prestamo p = new Prestamo(
                            rs.getInt("id"),
                            user,
                            libro,
                            rs.getDate("fecha_inicio").toLocalDate(),
                            rs.getDate("fecha_fin").toLocalDate(),
                            Estado.valueOf(rs.getString("estado"))
                    );
                    lista.add(p);
                }
            }

        }
        catch (SQLException e)
        {
            e.printStackTrace();
        }
        return lista;
    }

}
