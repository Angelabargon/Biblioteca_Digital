package com.example.biblioteca_digital.controladores;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.stage.Stage;

public class Navegacion {

    public static void cerrarVentana(ActionEvent event) {
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

    public static void cambiarVista(ActionEvent event, String fxml, String titulo) {
        try {
            javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                    Navegacion.class.getResource(fxml)
            );
            javafx.scene.Parent root = loader.load();
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(new javafx.scene.Scene(root));
            stage.setTitle(titulo);
            stage.show();
        } catch (Exception e) {
            System.out.println("Error al cambiar a la vista: " + fxml);
            e.printStackTrace();
        }
    }
}
