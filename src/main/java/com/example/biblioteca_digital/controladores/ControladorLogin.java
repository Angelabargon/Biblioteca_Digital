package com.example.biblioteca_digital.controladores;

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
        public void initialize() {
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
    }


