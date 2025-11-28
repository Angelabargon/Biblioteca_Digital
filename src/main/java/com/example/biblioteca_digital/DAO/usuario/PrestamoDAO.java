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
            pst.setInt(3, prestamo.getId_libro());
            pst.setDate(5, Date.valueOf(prestamo.getFecha_inicio()));
            pst.setDate(6, Date.valueOf(prestamo.getFecha_fin()));
            pst.setString(7, prestamo.getEstado().toString());
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
        SELECT p.id, p.id_usuario, p.id_libro, p.fecha_inicio, p.fecha_fin, p.estado,
                   u.nombre_usuario, l.titulo
            FROM prestamos p
            JOIN usuarios u ON p.id_usuario = u.id
            JOIN libros l ON p.id_libro = l.id
            WHERE p.id_usuario = ? AND p.estado = 'activo'
        """;
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql))
        {
            pst.setInt(1, idUsuario);
            try (ResultSet rs = pst.executeQuery())
            {
                while (rs.next())
                {
                    // Usuario (solo datos necesarios)
                    Usuario usuario = new Usuario();
                    usuario.setId(rs.getInt("id_usuario"));
                    usuario.setNombreUsuario(rs.getString("nombre_usuario"));

                    // Libro (solo datos necesarios)
                    Libro libro = new Libro();
                    libro.setId(rs.getInt("id_libro"));
                    libro.setTitulo(rs.getString("titulo"));

                    // Prestamo
                    Prestamo p = new Prestamo();
                    p.setId(rs.getInt("id"));
                    p.setId_usuario(rs.getInt("id_usuario"));
                    p.setId_libro(rs.getInt("id_libro"));
                    p.setFecha_inicio(rs.getDate("fecha_inicio").toLocalDate());
                    p.setFecha_fin(rs.getDate("fecha_fin").toLocalDate());
                    p.setEstado(rs.getString("estado"));

                    lista.add(p);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }

}
