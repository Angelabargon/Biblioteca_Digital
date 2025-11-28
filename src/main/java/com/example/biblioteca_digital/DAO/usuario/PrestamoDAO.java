package com.example.biblioteca_digital.DAO.usuario;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Prestamo;
import com.example.biblioteca_digital.modelos.Estado;
import com.example.biblioteca_digital.modelos.Usuario;

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
    // Dependencias necesarias para cargar los objetos Usuario y Libro
    private final CatalogoDAO catalogoDAO = new CatalogoDAO();
    private final UsuarioDAO usuarioDAO = new UsuarioDAO(); // ✅ Se asume la existencia de un UsuarioDAO

    /**
     * Guarda un nuevo préstamo en la base de datos.
     */
    public boolean guardarPrestamo(Prestamo prestamo)
    {
        // ✅ CORRECCIÓN 1: La consulta INSERT debe coincidir con la tabla DB real
        String sql = """
        INSERT INTO prestamos 
        (id_usuario, id_libro, fecha_inicio, fecha_fin, estado)
        VALUES (?, ?, ?, ?, ?)
    """;
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql))
        {
            pst.setInt(1, prestamo.getId_usuario());
            pst.setInt(2, prestamo.getId_libro());
            pst.setDate(3, Date.valueOf(prestamo.getFecha_inicio()));
            pst.setDate(4, Date.valueOf(prestamo.getFecha_fin()));
            pst.setString(5, prestamo.getEstado()); // Usamos getEstado() que retorna String/Enum.toString()
            // pst.setString(5, prestamo.getEstado().toString()); // Esto asume que getEstado() retorna el enum.

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

        // ✅ CORRECCIÓN 2: El SELECT debe coincidir con la tabla DB. No hay JOIN en esta consulta,
        // pero cargaremos los objetos de forma separada después.
        String sql = """
        SELECT id, id_usuario, id_libro, fecha_inicio, fecha_fin, estado
        FROM prestamos 
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
                    int idLibro = rs.getInt("id_libro");
                    int id_usuario_actual = rs.getInt("id_usuario");

                    // 1. Crear el objeto Prestamo base
                    Prestamo p = new Prestamo();
                    p.setId(rs.getInt("id"));
                    p.setId_usuario(id_usuario_actual);
                    p.setId_libro(idLibro);
                    p.setFecha_inicio(rs.getDate("fecha_inicio").toLocalDate());
                    p.setFecha_fin(rs.getDate("fecha_fin").toLocalDate());
                    p.setEstado(rs.getString("estado"));

                    // 2. Cargar el objeto Libro DTO
                    // Necesita obtenerLibroPorId en CatalogoDAO
                    Libro libroCompleto = catalogoDAO.obtenerLibroPorId(idLibro);
                    p.setLibro(libroCompleto);

                    // 3. Cargar el objeto Usuario DTO (si es necesario)
                    // Necesita obtenerUsuarioPorId en UsuarioDAO
                    Usuario usuarioCompleto = usuarioDAO.obtenerUsuarioPorId(id_usuario_actual);
                    // ✅ CORRECCIÓN 3: Se usa setUsuario, no getUsuario.setNombre
                    p.setUsuario(usuarioCompleto);

                    lista.add(p);
                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return lista;
    }
}

// Nota: Necesitarás crear la clase UsuarioDAO y el método obtenerUsuarioPorId
// si aún no lo tienes.
