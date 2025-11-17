package com.example.biblioteca_digital.controladores;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class ControladorPaginaInicio
{

    @FXML
    private Button registro;

    @FXML
    private Button login;

    public ControladorPaginaInicio()
    {}

    /**
     * Manejo de botón de inicio de sesión
     * @param event
     */
    public void irALogin(javafx.event.ActionEvent event)
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Login.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
            System.out.println("Navegando a la pantalla de Login.");

        } catch (IOException e)
        {
            System.err.println("Error al cargar la vista de Login: " + e.getMessage());
            e.printStackTrace();
        }
    }
    /**
     * Manejo del botón de registro de un nuevo usuario.
     * @param event
     */
    public void irARegistro(javafx.event.ActionEvent event)
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Registro.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Registro");
            stage.setScene(scene);
            stage.show();
            System.out.println("Navegando a la pantalla de Registro.");
        } catch (IOException e)
        {
            System.err.println("Error al cargar la vista de Registro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
