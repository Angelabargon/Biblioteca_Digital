package com.example.biblioteca_digital.controladores;

/**
 * Hacemos los imports necesarios.
 */
import com.example.biblioteca_digital.DAO.LoginDAO;
import com.example.biblioteca_digital.modelos.Sesion;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.event.ActionEvent;
import java.util.Optional;

/**
 * Controlador de la vista de inicio de sesión.
 *
 * Gestiona la autenticación de usuarios y administradores,
 * configurando la interfaz según el rol seleccionado y
 * redirigiendo a la vista correspondiente tras un login exitoso.
 */
public class ControladorLogin {

    /** ToggleButton para seleccionar el rol de usuario. */
    @FXML private ToggleButton tbt_usuario;

    /** ToggleButton para seleccionar el rol de administrador. */
    @FXML private ToggleButton tbt_admin;

    /** Grupo que une ambos ToggleButton para que sean excluyentes. */
    @FXML private ToggleGroup grupoRol;

    /** Campo de texto para introducir el correo electrónico. */
    @FXML private TextField tf_email;

    /** Campo de contraseña para introducir la clave de acceso. */
    @FXML private PasswordField pf_contraseña;

    /** Botón para iniciar sesión con las credenciales introducidas. */
    @FXML private Button bt_inicioUsuario;

    /** Botón para mostrar la ayuda de inicio de sesión. */
    @FXML private Button bt_ayuda;

    /** Botón para volver a la página de inicio. */
    @FXML private Button bt_volver;

    /** Instancia del DAO. */
    private final LoginDAO loginDAO = new LoginDAO();

    /**
     * Inicializa el controlador configurando el grupo de roles
     * y ajustando los textos y placeholders según el rol seleccionado.
     */
    @FXML
    public void initialize() {

        grupoRol = new ToggleGroup();
        tbt_usuario.setToggleGroup(grupoRol);
        tbt_admin.setToggleGroup(grupoRol);
        tbt_usuario.setSelected(true);

        grupoRol.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {

            if (newToggle != null) {
                ToggleButton seleccionado = (ToggleButton) newToggle;
                String rol = seleccionado.getText().toLowerCase();

                if (rol.equals("admin")) {
                    tf_email.setPromptText("Email de Administrador");
                    pf_contraseña.setPromptText("Contraseña de Administrador");
                    bt_inicioUsuario.setText("Iniciar Sesión como Administrador");

                } else {
                    tf_email.setPromptText("Email");
                    pf_contraseña.setPromptText("Contraseña");
                    bt_inicioUsuario.setText("Iniciar Sesión como Usuario");
                }
            }
        });

        tf_email.setOnAction(event -> iniciarSesion(event));
        pf_contraseña.setOnAction(event -> iniciarSesion(event));
    }

    /**
     * Muestra la ventana de ayuda sobre la actual.
     *
     * @param event evento de acción generado al pulsar el botón de ayuda.
     */
    @FXML
    public void mostrarAyuda(ActionEvent event) {
        ControladorAyuda.mostrarAyuda("/com/example/biblioteca_digital/vistas/Vista-Ayuda-Login.fxml", "Login");
    }

    /**
     * Verifica la existencia del usuario con los datos introducidos.
     *
     * Si las credenciales son correctas, guarda la sesión y envía el usuario
     * al menú correspondiente (usuario o administrador).
     *
     * @param event evento de acción generado al pulsar el botón de inicio de sesión.
     */
    @FXML
    private void iniciarSesion(ActionEvent event) {
        String email = tf_email.getText().trim();
        String contraseña = pf_contraseña.getText().trim();
        String rol = tbt_usuario.isSelected() ? "usuario" : "admin";

        if (email.isEmpty() || contraseña.isEmpty()) {
            System.out.println("Rellena todos los campos.");
            return;
        }

        Optional<Usuario> cuentaAutenticada = loginDAO.autenticar(email, contraseña, rol);
        if (cuentaAutenticada.isPresent()) {
            Sesion.setUsuario(cuentaAutenticada.get());

            String vistaDestino = rol.equals("usuario")
                    ? "/com/example/biblioteca_digital/vistas/usuario/Vista-Menu-Usuario.fxml"
                    : "/com/example/biblioteca_digital/vistas/admin/Vista-Administrador.fxml";

            Navegacion.cambiarVista(event, vistaDestino, "Bienvenido");

        } else {
            System.out.println("Credenciales incorrectas, comprueba tu email o contraseña.");
            mostrarAlertaErrorLogin();
        }
    }

    /**
     * Cambia la vista actual a la página de inicio.
     *
     * @param event evento de acción generado al pulsar el botón de volver.
     */
    @FXML
    private void volverAtras(ActionEvent event) {
        Navegacion.cambiarVista(event, "/com/example/biblioteca_digital/vistas/Vista-Pagina-Inicio.fxml", "Página de Inicio");
    }

    /**
     * Muestra una ventana de error con el mensaje de credenciales incorrectas.
     */
    private void mostrarAlertaErrorLogin() {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error de Inicio de Sesión");
        alert.setHeaderText("Credenciales Incorrectas");
        alert.setContentText("Comprueba tu email o contraseña.");
        alert.showAndWait();
    }
}


