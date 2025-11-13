package com.example.biblioteca_digital.controladores;

import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ControladorRegistro
{
    /**
     * Manejo de botón de guardarRegistro
     * @param event
     */
    public void guardarYVolver(javafx.event.ActionEvent event)
    {
        try
        {
            FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-PaginaInicio.fxml"));
            Scene scene = new Scene(fxmlLoader.load());
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setTitle("Login");
            stage.setScene(scene);
            stage.show();
            System.out.println("Navegando a la pantalla de inicio.");

        } catch (IOException e)
        {
            System.err.println("Error al cargar la vista de inicio: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
