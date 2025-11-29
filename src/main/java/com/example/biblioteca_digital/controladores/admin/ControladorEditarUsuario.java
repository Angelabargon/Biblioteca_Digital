package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.modelos.Rol;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class ControladorEditarUsuario {

    @FXML private TextField txtUsuario;
    @FXML private TextField txtNombre;
    @FXML private TextField txtPrimerApellido;
    @FXML private TextField txtCorreo;
    @FXML private PasswordField txtContrasena;
    @FXML private ComboBox<Rol> comboRol;

    private Usuario usuario;
    private Stage stage;
    private Runnable onGuardarCallback;

    @FXML private void initialize() { comboRol.getItems().setAll(Rol.values()); }

    public void setStage(Stage s) { this.stage = s; }
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
    @FXML private void guardar() { if (onGuardarCallback!=null) onGuardarCallback.run(); if (stage!=null) stage.close(); }
    @FXML private void cancelar() { if (stage!=null) stage.close(); }
    public void setOnGuardarCallback(Runnable cb) { this.onGuardarCallback = cb; }
}
