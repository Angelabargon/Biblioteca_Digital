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
 * Creamos la clase tras la lógica del controlador.
 */
public class ControladorLogin {

    /**
     * Definimos los elementos con los que interactúa la vista.
     *
     * - ToggleButton tbt_usuario: Marcará el rol elegido como usuario.
     * - ToggleButton tbt_admin: Marcará el rol elegido como usuario.
     * - ToggleGroup groupRol: Unirá ambos ToggleButton.
     * - TextField tf_email: Recibirá el correo del usuario.
     * - PasswordField pf_contraseña: Recibirá la contraseña del usuario.
     * - Button bt_inicioUsuario: Recibirá la contraseña del usuario por segunda vez.
     * - Button bt_ayuda: Mostrará una guía para iniciar sesión al usuario.
     * - Button bt_volver: Devolverá al usuario a la página de inicio.
     */
    @FXML
    private ToggleButton tbt_usuario;
    @FXML
    private ToggleButton tbt_admin;
    @FXML
    private ToggleGroup grupoRol;
    @FXML
    private TextField tf_email;
    @FXML
    private PasswordField pf_contraseña;
    @FXML
    private Button bt_inicioUsuario;
    @FXML
    private Button bt_ayuda;
    @FXML
    private Button bt_volver;

    /**
     * Inicializa el controlador configurando el grupo de roles
     * y ajustando los textos según el rol seleccionado.
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
        }

    /**
     * Muestra la ventana de ayuda sobre la actual.
     *
     * @param event
     */
    @FXML
    public void mostrarAyuda(ActionEvent event) {
        ControladorAyuda.mostrarAyuda("/com/example/biblioteca_digital/vistas/Vista-Ayuda-Login.fxml", "Login");
    }

    /**
     * Verifica la existencia del usuario con los datos introducidas.
     * Si son correctas, guarda la sesión y navega al menú correspondient.
     *
     * @param event
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

        Optional<Usuario> cuentaAutenticada = LoginDAO.autenticar(email, contraseña, rol);
        if (cuentaAutenticada.isPresent()) {

            Sesion.setUsuario(cuentaAutenticada.get());

            String vistaDestino = rol.equals("usuario")
                    ? "/com/example/biblioteca_digital/vistas/usuario/Vista-Menu-Usuario.fxml"
                        : "/com/example/biblioteca_digital/vistas/admin/Vista-Administrador.fxml";

            Navegacion.cambiarVista(event, vistaDestino, "Bienvenido");

        } else {
            System.out.println("Credenciales incorrectas, comprueba tu email o contraseña.");
        }
    }

    /**
     * Este último método cambiará de vista a Vista-Pagina-Inicio.
     * @param event
     */
    @FXML
    private void volverAtras(ActionEvent event) {
        Navegacion.cambiarVista(event, "/com/example/biblioteca_digital/vistas/Vista-Pagina-Inicio.fxml", "Página de Inicio");
    }
}


