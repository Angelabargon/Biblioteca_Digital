package com.example.biblioteca_digital.controladores;

/*
Hacemos los importes necesarios.
 */
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class ControladorAyuda {

    /*
    Creamos un metodo para mostrar la ayuda encima de la activity actual.
     */
    public static void mostrarAyuda(String rutaFXML, String titulo) {
        try {
            FXMLLoader loader = new FXMLLoader(ControladorAyuda.class.getResource(rutaFXML));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ayuda " + titulo);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();

        } catch (Exception e) {
            System.out.println("Error al cambiar a la vista: " + rutaFXML);
            e.printStackTrace();
        }
    }

    /*
    Creamos un metodo para cerrar la ayuda y vover a la página anterior.
     */
    @FXML
    private void cerrarAyuda(ActionEvent event) {

        Navegacion.cerrarVentana(event);
    }
}
