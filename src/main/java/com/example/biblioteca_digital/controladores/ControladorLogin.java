package com.example.biblioteca_digital.controladores;

import com.example.biblioteca_digital.controladores.Crud;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.util.List;
import java.util.Optional;



public class ControladorLogin<T, ID> implements Crud<T, ID> {

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

    /**
     * @param entity
     * @return
     */
    @Override
    public T guardar(T entity) {
        return null;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Optional<T> buscarPorId(ID id) {
        return Optional.empty();
    }

    /**
     * @return
     */
    @Override
    public List<T> listar() {
        return List.of();
    }

    /**
     * @param id
     */
    @Override
    public void eliminarPorId(ID id) {

    }

    /**
     * @param entity
     */
    @Override
    public void eliminar(T entity) {

    }
}

