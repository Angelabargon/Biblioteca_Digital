package com.example.biblioteca_digital.controladores;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.awt.event.ActionEvent;
import java.io.IOException;

public class ControladorPaginaInicio
{
    /**
     * Manejo de botón de inicio de sesión
     * @param event
     */
    public void irALogin(ActionEvent event)
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("com/example/biblioteca_digital/vistas/VistaPaginaInicio.fxml"));
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
    public void goToRegister(ActionEvent event)
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("com/example/biblioteca_digital/vistas/VistaRegistro.fxml"));
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
