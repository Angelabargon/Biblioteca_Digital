package com.example.biblioteca_digital.controladores;

import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.stage.Stage;

import javafx.event.ActionEvent;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

public class ControladorLogin {

    @FXML
    private ToggleButton tbt_usuario;

    @FXML
    private ToggleButton tbt_admin;

    @FXML
    private ToggleGroup grupoRol;

    @FXML
    private TextField tf_email;

    @FXML
    private TextField tf_contraseña;

    @FXML
    private Button bt_inicioUsuario;

    @FXML
    private Button bt_ayuda;

        @FXML
        public void initializeLogin() {
            grupoRol = new ToggleGroup();
            tbt_usuario.setToggleGroup(grupoRol);
            tbt_admin.setToggleGroup(grupoRol);

            grupoRol.selectedToggleProperty().addListener((obs, oldToggle, newToggle) -> {
                if (newToggle != null) {
                    ToggleButton seleccionado = (ToggleButton) newToggle;
                    String rol = seleccionado.getText();

                    if (rol.equals("Administrador")) {
                        tf_email.setPromptText("Email de Administrador");
                        tf_contraseña.setPromptText("Contraseña de Administrador");
                        bt_inicioUsuario.setText("Iniciar Sesión como Administrador");
                    } else {
                        tf_email.setPromptText("Email");
                        tf_contraseña.setPromptText("Contraseña");
                        bt_inicioUsuario.setText("Iniciar Sesión como Usuario");
                    }
                }
            });
        }

        public void mostrarAyuda(ActionEvent event) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Ayuda.fxml"));
                Parent root = loader.load();

                Stage stage = new Stage();
                stage.setTitle("Ayuda");
                stage.setScene(new Scene(root));
                stage.show();

            } catch (IOException e) {
                System.out.println("Error al cargar la ayuda.");
            }
        }

        @FXML
        private void iniciarSesion(ActionEvent event) {
            String email = tf_email.getText().trim();
            String contraseña = tf_contraseña.getText().trim();
            String rol = tbt_usuario.isSelected() ? "Usuario" : "Administrador";

            if (email.isEmpty() || contraseña.isEmpty()) {
                System.out.println("Rellena todos los campos.");
                return;
            }

            Optional<Usuario> cuentaAutenticada = ControladorUsuario.autenticar(email, contraseña, rol);

            if (cuentaAutenticada.isPresent()) {
                String vistaDestino = rol.equals("Usuario") ?
                        "/com/example/biblioteca_digital/vistas/Vista-Usuario.fxml" :
                        "/com/example/biblioteca_digital/vistas/Vista-Administrador.fxml";

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource(vistaDestino));
                    Parent root = loader.load();

                    if (rol.equals("Usuario")) {
                    ControladorUsuario controlador = loader.getController();
                    controlador.initializeCuenta(cuentaAutenticada.get());
                    } else if (rol.equals("Administrador")) {
                        ControladorAdministrador controlador = loader.getController();
                        controlador.initializeCuenta(cuentaAutenticada.get());
                    }

                    Stage stage = new Stage();
                    stage.setTitle("Bienvenido");
                    stage.setScene(new Scene(root));
                    stage.show();

                    ((Stage) bt_inicioUsuario.getScene().getWindow()).close();

                } catch (IOException e) {
                    System.out.println("Error, no se pudo cargar la vista de " + rol.toLowerCase() + ".");
                }
            } else {
                System.out.println("Credenciales incorrectas, comprueba tu email o contraseña.");
            }
        }
    }


