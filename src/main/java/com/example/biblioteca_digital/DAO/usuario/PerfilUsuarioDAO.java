package com.example.biblioteca_digital.DAO.usuario;

/**
 * Hacemos los imports necesarios.
 */

import com.example.biblioteca_digital.conexion.ConexionBD;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

/**
 * Creamos la clase PerfilUsuarioDAO que almacenará los métodos del perfil relacionados con la base de datos.
 */
public class PerfilUsuarioDAO
{
    /**
     * Devuelve el número de favoritos asociados a un usuario.
     *
     * @param idUsuario Identificador del usuario.
     * @return Cantidad de favoritos del usuario.
     */
    public static int contarFavoritos(int idUsuario)
    {
        String sql = "SELECT COUNT(*) FROM favoritos WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql))
        {
            pst.setInt(1, idUsuario);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        catch (Exception e) { e.printStackTrace(); }
        return 0;
    }
    /**
     * Devuelve el número de préstamos activos asociados a un usuario.
     *
     * @param idUsuario Identificador del usuario.
     * @return Cantidad de préstamos del usuario.
     */
    public static int contarPrestamos(int idUsuario)
    {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql))
        {
            pst.setInt(1, idUsuario);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    /**
     * Actualiza la contraseña de un usuario en la base de datos.
     *
     * @param idUsuario   Identificador del usuario.
     * @param nuevaPass   Nueva contraseña.
     * @return true si la actualización fue exitosa, false en caso contrario.
     */
    public static boolean actualizarContrasena(int idUsuario, String nuevaPass) {
        String sql = "UPDATE usuarios SET contrasena = ? WHERE id = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setString(1, nuevaPass);
            pst.setInt(2, idUsuario);
            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}
