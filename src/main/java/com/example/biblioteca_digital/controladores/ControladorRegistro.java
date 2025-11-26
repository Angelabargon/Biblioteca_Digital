package com.example.biblioteca_digital.controladores;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Rol;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import java.io.IOException;
import java.sql.*;
import java.time.LocalDate;

import static com.example.biblioteca_digital.modelos.Rol.usuario;

public class ControladorRegistro
{
    /**
     * Clase para agrupar las consultas SQL al guardar un nuevo usuario
     */
    static class Consultas
    {
        /**
         * Consulta para obtener el ID mayor para que los ID de los usuarios sean uno mayor
         * @return
         */
        public int siguienteId()
        {
            String sql = "SELECT MAX(id) FROM Usuarios";
            Connection conn = null;
            int nextId = 1;
            try
            {
                conn = ConexionBD.getConexion();
                try (PreparedStatement stmt = conn.prepareStatement(sql);
                     ResultSet rs = stmt.executeQuery())
                {
                    if (rs.next())
                    {
                        nextId = rs.getInt(1) + 1;
                    }
                }
            }
            catch (SQLException e)
            {
                System.err.println("Error al obtener el siguiente ID de la BD: " + e.getMessage());
                return 1;
            }
            finally
            {
                ConexionBD.cerrarConexion();
            }
            return nextId;
        }
        /**
         * Guarda el usuario en la base de datos.
         * @param usuario
         */
        public void guardarUsuario(Usuario usuario)
        {
            String sql = "INSERT INTO Usuarios (id, nombre_usuario , nombre, primer_apellido, correo, contrasena, rol, fecha_registro) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            try (Connection conn = ConexionBD.getConexion();
                 PreparedStatement stmt = (conn != null) ? conn.prepareStatement(sql) : null)
            {
                if (stmt == null)
                {
                    System.err.println("Error: La conexión es nula. No se pudo preparar la sentencia SQL.");
                    return;
                }
                stmt.setInt(1, usuario.getId());
                stmt.setString(2, usuario.getNombreUsuario());
                stmt.setString(3, usuario.getNombre());
                stmt.setString(4, usuario.getPrimerApellido());
                stmt.setString(5, usuario.getCorreo());
                stmt.setString(6, usuario.getContrasena());
                stmt.setString(7, usuario.getRol().toString());
                stmt.setDate(8, Date.valueOf(usuario.getFechaRegistro()));
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
                 PreparedStatement stmt = (conn != null) ? conn.prepareStatement(sql) : null)
            {
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
            }
            catch (SQLException e)
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
    @FXML private PasswordField contrasena;
    @FXML private PasswordField repetirContrasena;
    @FXML private CheckBox aceptoTerminos;
    @FXML private Label mensajeError; // Debe ser introducido en la vista
    @FXML private Button registrar;
    @FXML private Button bt_volver;
    private final Consultas consultas = new Consultas();
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
        if(contrasena.getCharacters().length() <= 8)
        {
            mensajeError.setText("La contraseña debe de tener al menos 8 caracteres");
            return;
        }
        if (!compararContrasenas(contrasena1, repetirContrasena1))
        {
            mensajeError.setText("Las contraseñas no coinciden.");
            return;
        }
        if (verificarUsuarioExistente(nombreUsuario1))
        {
            mensajeError.setText("Ya existe un usuario con ese nombre. Por favor, elige otro.");
            return;
        }
        if (!verificarCheckboxTickeado())
        {
            mensajeError.setText("Debes aceptar los términos y condiciones.");
            return;
        }
        LocalDate fechaRegistro = LocalDate.now();
        int idUsuario = consultas.siguienteId();
        Usuario nuevoUsuario = construirObjetoUsuario(idUsuario, nombre1, nombreUsuario1, primerApellido1, correo1, contrasena1, usuario, fechaRegistro);
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
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
            System.out.println("Navegando a la pantalla de inicio de sesion.");

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
    private Usuario construirObjetoUsuario(int id, String nombreUsuario, String nombre, String primerApellido, String correo, String contrasena, Rol rol, LocalDate fechaRegistro)
    {
        return new Usuario(id, nombreUsuario, nombre, primerApellido, correo, contrasena, usuario, fechaRegistro);
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

    public void mostrarAyuda(ActionEvent event) {
        ControladorAyuda.mostrarAyuda("/com/example/biblioteca_digital/vistas/Vista-Ayuda-Registro.fxml", "Registro");
    }

    @FXML
    private void volverAtras(ActionEvent event) {
        Navegacion.cambiarVista(event, "/com/example/biblioteca_digital/vistas/Vista-Pagina-Inicio.fxml", "Página de Inicio");
    }

    public void precargarDatos(Usuario usuario) {

        if (usuario == null) return;
        nombreUsuario.setText(usuario.getNombreUsuario());
        nombre.setText(usuario.getNombre());
        correo.setText(usuario.getCorreo());

        contrasena.clear();
        repetirContrasena.clear();
    }
}
