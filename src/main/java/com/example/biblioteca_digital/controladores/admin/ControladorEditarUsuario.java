package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.modelos.Rol;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

/**
 * Controlador encargado de gestionar la ventana emergente para crear o editar usuarios
 * dentro del panel de administración. Permite modificar los datos básicos de un usuario,
 * asignar su rol dentro del sistema y devolver el objeto actualizado al controlador principal.
 *
 * <p>Este controlador es cargado desde un diálogo modal. Contiene validación mínima,
 * ya que la lógica de comprobación se delega al controlador padre.</p>
 */
public class ControladorEditarUsuario {

    /** Campo de texto para el nombre de usuario. */
    @FXML private TextField txtUsuario;

    /** Campo de texto para el nombre del usuario. */
    @FXML private TextField txtNombre;

    /** Campo de texto para el primer apellido del usuario. */
    @FXML private TextField txtPrimerApellido;

    /** Campo de texto para el correo del usuario. */
    @FXML private TextField txtCorreo;

    /** Campo de texto para introducir la contraseña del usuario. */
    @FXML private PasswordField txtContrasena;

    /** ComboBox que muestra la lista de roles disponibles. */
    @FXML private ComboBox<Rol> comboRol;

    /** Usuario que se está editando o creando. */
    private Usuario usuario;

    /** Ventana actual del diálogo. */
    private Stage stage;

    /**
     * Función callback que se ejecuta al pulsar el botón "Guardar".
     * Sirve para notificar al controlador principal que los datos han cambiado.
     */
    private Runnable onGuardarCallback;

    /**
     * Inicializa el controlador y carga los roles disponibles en el ComboBox.
     * Este metodo se ejecuta automáticamente al cargar el FXML.
     */
    @FXML private void initialize() { comboRol.getItems().setAll(Rol.values()); }

    /**
     * Asigna la ventana actual para poder cerrarla desde el controlador.
     *
     * @param s ventana (Stage) asociada al diálogo de edición.
     */
    public void setStage(Stage s) { this.stage = s; }

    /**
     * Carga en los campos de texto los datos del usuario que se desea editar.
     * Si el parámetro es {@code null}, se interpreta como creación de usuario nuevo.
     *
     * @param u usuario existente cuyos datos se van a mostrar en el formulario.
     */
    public void setUsuario(Usuario u) {
        this.usuario = u;
        if (u!=null) {
            txtUsuario.setText(u.getNombreUsuario());
            txtNombre.setText(u.getNombre());
            txtPrimerApellido.setText(u.getPrimerApellido());
            txtCorreo.setText(u.getCorreo());
            txtContrasena.setText(u.getContrasena());
            comboRol.setValue(u.getRol());
        }
    }

    /**
     * Obtiene un objeto {@link Usuario} con los datos introducidos en el formulario.
     * Si no existía previamente, se crea uno nuevo.
     *
     * @return usuario con los valores actualizados desde el formulario.
     */
    public Usuario getUsuarioResultado() {
        if (usuario==null) usuario = new Usuario();
        usuario.setNombreUsuario(txtUsuario.getText());
        usuario.setNombre(txtNombre.getText());
        usuario.setPrimerApellido(txtPrimerApellido.getText());
        usuario.setCorreo(txtCorreo.getText());
        usuario.setContrasena(txtContrasena.getText());
        usuario.setRol(comboRol.getValue());
        return usuario;
    }

    /**
     * Ejecuta el callback de guardado y cierra la ventana actual.
     * Este metodo se llama al pulsar el botón "Guardar".
     */
    @FXML private void guardar() { if (onGuardarCallback!=null) onGuardarCallback.run(); if (stage!=null) stage.close(); }

    /**
     * Cierra la ventana sin realizar cambios. Se ejecuta al pulsar "Cancelar".
     */
    @FXML private void cancelar() { if (stage!=null) stage.close(); }

    /**
     * Define una función callback que se ejecutará automáticamente
     * cuando el usuario confirme los cambios pulsando "Guardar".
     *
     * @param cb función sin parámetros que será ejecutada tras el guardado.
     */
    public void setOnGuardarCallback(Runnable cb) { this.onGuardarCallback = cb; }
}
