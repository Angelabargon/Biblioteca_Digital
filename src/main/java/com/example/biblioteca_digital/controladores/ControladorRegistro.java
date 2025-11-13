package com.example.biblioteca_digital.controladores;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

public class ControladorRegistro
{
    /**
     * Clase para agrupar las consultas SQL al guardar un nuevo usuario
     */
    static class Consultas {
        /**
         * Guarda el usuario en la base de datos.
         * @param usuario
         */
        public void guardarUsuario(Usuario usuario)
        {
            String sql = "INSERT INTO Usuarios (nombre, primerApellido, correo, contrasena, rol, fechaRegistro) VALUES (?, ?, ?, ?, ?, ?)";
            try (Connection conn = ConexionBD.getConexion();
                 PreparedStatement stmt = (conn != null) ? conn.prepareStatement(sql) : null) {
                if (stmt == null)
                {
                    System.err.println("Error: La conexión es nula. No se pudo preparar la sentencia SQL.");
                    return;
                }
                stmt.setString(1, usuario.getNombre());
                stmt.setString(2, usuario.getPrimerApellido());
                stmt.setString(3, usuario.getCorreo());
                stmt.setString(4, usuario.getContrasena());
                stmt.setString(5, usuario.getRol());
                stmt.setDate(6, Date.valueOf(usuario.getFechaRegistro()));
                stmt.executeUpdate();
            } catch (SQLException e)
            {
                System.err.println("Error al guardar el usuario en la BD (SQL o Conexión): " + e.getMessage());
            }
        }
        /**
         * Verifica si ya existe un usuario en la base de datos con el nombre proporcionado.
         * @param nombre
         * @return
         */
        public boolean existeUsuarioPorNombre(String nombre)
        {
            String sql = "SELECT COUNT(*) FROM Usuarios WHERE nombre = ?";
            try (Connection conn = ConexionBD.getConexion();
                 PreparedStatement stmt = (conn != null) ? conn.prepareStatement(sql) : null) {
                if (stmt == null)
                {
                    System.err.println("Error no se pudo verificar existencia en la base de datos ya que no hay conexión.");
                    return false;
                }
                stmt.setString(1, nombre);
                try (ResultSet rs = stmt.executeQuery())
                {
                    if (rs.next())
                    {
                        return rs.getInt(1) > 0;
                    }
                }
            } catch (SQLException e)
            {
                System.err.println("Error al verificar existencia de usuario (SQL o Conexión): " + e.getMessage());
            }
            return false;
        }
    }
    @FXML private TextField nombreUsuario; // Asumo que este campo existe
    @FXML private TextField nombre;
    @FXML private TextField primerApellido;
    @FXML private TextField correo;
    @FXML private TextField contrasena;
    @FXML private PasswordField repetirContrasena;
    @FXML private CheckBox aceptoTerminos;
    @FXML private Label mensajeError;
    private final Consultas consultas = new Consultas();
    private int proximoId = 1;
    /**
     * Maneja el clic del botón de registro, realiza validaciones y guarda el usuario.
     * Si el registro es exitoso, navega a la página de inicio.
     * @param event Evento de acción del botón.
     */
    public void guardarRegistro(javafx.event.ActionEvent event)
    {
        mensajeError.setText("");
        String nombreUsuario1 = nombreUsuario.getText().trim();
        String nombre1 = nombre.getText().trim();
        String primerApellido1 = primerApellido.getText().trim();
        String correo1 = correo.getText().trim();
        String contrasena1 = contrasena.getText().trim();
        String repetirContrasena1 = repetirContrasena.getText();
        if (!validarCamposNoVacios(nombreUsuario1, nombre1, primerApellido1, correo1, contrasena1, repetirContrasena1))
        {
            mensajeError.setText("Todos los campos son obligatorios.");
            return;
        }
        if (!compararContrasenas(String.valueOf(contrasena), String.valueOf(repetirContrasena)))
        {
            mensajeError.setText("Las contraseñas no coinciden.");
            return;
        }
        if (verificarUsuarioExistente(String.valueOf(nombreUsuario)))
        {
            mensajeError.setText("Ya existe un usuario con ese nombre. Por favor, elige otro.");
            return;
        }
        if (!verificarCheckboxTickeado())
        {
            mensajeError.setText("Debes aceptar los términos y condiciones.");
            return;
        }
        Usuario nuevoUsuario = new Usuario(proximoId++, nombreUsuario, primerApellido, correo, contrasena, "USUARIO", LocalDate.now());
        guardarDatosUsuario(nuevoUsuario);
        volver(event);
    }

    /**
     * Manejo de botón de guardarRegistro
     * @param event
     */
    public void volver(javafx.event.ActionEvent event)
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-PaginaInicio.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
            System.out.println("Navegando a la pantalla de inicio.");

        } catch (IOException e)
        {
            System.err.println("Error al cargar la vista de inicio: " + e.getMessage());
            e.printStackTrace();
        }
    }
    private void guardarDatosUsuario(Usuario usuario)
    {
        consultas.guardarUsuario(usuario);
    }

    private Usuario construirObjetoUsuario(int id, String nombreUsuario, String primerApellido, String correo, String contrasena, String rol, LocalDate fechaRegistro)
    {
        return new Usuario(id, nombreUsuario, primerApellido, correo, contrasena, rol, fechaRegistro);
    }
    private boolean compararContrasenas(String contrasena, String repetir)
    {
        return contrasena.equals(repetir);
    }
    private boolean verificarUsuarioExistente(String nombreUsuario)
    {
        return consultas.existeUsuarioPorNombre(nombreUsuario);
    }
    private boolean verificarCheckboxTickeado()
    {
        return aceptoTerminos != null && aceptoTerminos.isSelected();
    }
    private boolean validarCamposNoVacios(String... campos)
    {
        for (String campo : campos)
        {
            if (campo == null || campo.isEmpty())
            {
                return false;
            }
        }
        return true;
    }
}
