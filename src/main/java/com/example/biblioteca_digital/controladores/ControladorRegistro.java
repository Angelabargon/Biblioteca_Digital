package com.example.biblioteca_digital.controladores;

/**
 * Imports necesarios.
 */
import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.DAO.RegistroDAO;
import com.example.biblioteca_digital.modelos.Rol;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.time.LocalDate;
import static com.example.biblioteca_digital.modelos.Rol.usuario;

/**
 * Controlador encargado de gestionar la vista de registro de nuevos usuarios.
 */
public class ControladorRegistro {

    /**
     * Referencias a los elementos de la vista
     */
    @FXML private TextField nombreUsuario;
    @FXML private TextField nombre;
    @FXML private TextField primerApellido;
    @FXML private TextField correo;
    @FXML private PasswordField contrasena;
    @FXML private PasswordField repetirContrasena;
    @FXML
    CheckBox aceptoTerminos;
    @FXML private Label mensajeError;

    /** DAO encargado de las consultas SQL del registro. */
    private final RegistroDAO registroDAO = new RegistroDAO();

    /**
     * Maneja el clic del botón de registro, realiza validaciones y guarda el usuario.
     * Si el registro es exitoso, se envía el usuario a la página de Login.
     *
     * @param event Evento de acción del botón.
     */
    public void guardarRegistro(javafx.event.ActionEvent event) {

        mensajeError.setText("");

        // Se obtienen los valores del formulario.
        String nombreUsuario1 = nombreUsuario.getText().trim();
        String nombre1 = nombre.getText().trim();
        String primerApellido1 = primerApellido.getText().trim();
        String correo1 = correo.getText().trim();
        String contrasena1 = contrasena.getText().trim();
        String repetirContrasena1 = repetirContrasena.getText();

        // Validaciones.
        if (!validarCamposNoVacios(nombreUsuario1, nombre1, primerApellido1, correo1, contrasena1, repetirContrasena1)) {
            mensajeError.setText("Todos los campos son obligatorios.");
            return;
        }

        if (contrasena.getCharacters().length() < 8) {
            mensajeError.setText("La contraseña debe tener al menos 8 caracteres.");
            return;
        }

        if (!compararContrasenas(contrasena1, repetirContrasena1)) {
            mensajeError.setText("Las contraseñas no coinciden.");
            return;
        }

        if (registroDAO.existeUsuarioPorNombre(nombreUsuario1)) {
            mensajeError.setText("Ya existe un usuario con ese nombre. Por favor, elige otro.");
            return;
        }

        if (registroDAO.existeUsuarioPorCorreo(correo1)) {
            mensajeError.setText("Ya existe un usuario con ese correo. Por favor, inserte otro.");
            return;
        }

        if (!verificarCheckboxTickeado()) {
            mensajeError.setText("Debes aceptar los términos y condiciones.");
            return;
        }

        // Crea un usuario.
        LocalDate fechaRegistro = LocalDate.now();
        int idUsuario = registroDAO.siguienteId();
        Usuario nuevoUsuario = construirObjetoUsuario(idUsuario, nombre1, nombreUsuario1, primerApellido1, correo1, contrasena1, usuario, fechaRegistro);
        registroDAO.guardarUsuario(nuevoUsuario);
        volver(event);
    }

    /** Cambia a la página del Login. */
    public void volver(javafx.event.ActionEvent event) {
        Navegacion.cambiarVista(event, "/com/example/biblioteca_digital/vistas/Vista-Login.fxml", "Login");
    }

    /** Construye un objeto Usuario con los datos proporcionados. */
    Usuario construirObjetoUsuario(int id, String nombreUsuario, String nombre, String primerApellido, String correo, String contrasena, Rol rol, LocalDate fechaRegistro) {
        return new Usuario(id, nombreUsuario, nombre, primerApellido, correo, contrasena, rol, fechaRegistro);
    }

    /** Compara dos contraseñas. */
    boolean compararContrasenas(String contrasena, String repetir)
    {
        return contrasena.equals(repetir);
    }

    /** Verifica si el usuario ha aceptado los términos. */
    boolean verificarCheckboxTickeado() {
        return aceptoTerminos != null && aceptoTerminos.isSelected();
    }

    /** Verifica que ningún campo esté vacío. */
    boolean validarCamposNoVacios(String... campos) {
        for (String campo : campos) {
            if (campo == null || campo.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /** Muestra la ventana de ayuda del registro. */
    public void mostrarAyuda(ActionEvent event) {
        ControladorAyuda.mostrarAyuda("/com/example/biblioteca_digital/vistas/Vista-Ayuda-Registro.fxml", "Registro");
    }

    /** Vuelve a la página de inicio. */
    @FXML
    private void volverAtras(ActionEvent event) {
        Navegacion.cambiarVista(event, "/com/example/biblioteca_digital/vistas/Vista-Pagina-Inicio.fxml", "Página de Inicio");
    }
}
