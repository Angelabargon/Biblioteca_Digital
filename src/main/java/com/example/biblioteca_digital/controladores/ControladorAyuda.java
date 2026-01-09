package com.example.biblioteca_digital.controladores;

/**
 * Hacemos los importes necesarios.
 */
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;

/**
 * Controlador encargado de gestionar las ventanas de ayuda.
 *
 * Permite abrir una vista de ayuda en modo modal sobre la ventana actual
 * y cerrarla cuando el usuario lo quiera.
 */
public class ControladorAyuda {

    /**
     * Muestra una ventana de ayuda en modo modal(Ventana Emergente) sobre la actividad actual.
     *
     * @param rutaFXML ruta del archivo FXML de la vista de ayuda.
     * @param titulo   título descriptivo que se mostrará en la ventana.
     */
    public static void mostrarAyuda(String rutaFXML, String titulo) {

        try {
            FXMLLoader loader = new FXMLLoader(ControladorAyuda.class.getResource(rutaFXML));
            Parent root = loader.load();

            Stage stage = new Stage();
            stage.setTitle("Ayuda " + titulo);
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setResizable(false);
            stage.getIcons().add(new Image(ControladorAyuda.class.getResourceAsStream("/com/example/biblioteca_digital/imagenes/icono-app-login.png")));
            stage.showAndWait();

        } catch (Exception e) {
            System.out.println("Error al cambiar a la vista: " + rutaFXML);
            e.printStackTrace();
        }
    }

    /**
     * Cierra la ventana de ayuda y regresa a la pantalla anterior.
     *
     * @param event evento de acción generado al pulsar el botón de cerrar.
     */
    @FXML
    private void cerrarAyuda(ActionEvent event) {
        Navegacion.cerrarVentana(event);

    }
}
