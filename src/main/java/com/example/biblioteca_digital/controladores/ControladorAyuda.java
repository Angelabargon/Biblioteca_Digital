package com.example.biblioteca_digital.controladores;

/*
Hacemos los importes necesarios.
 */
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.stage.Stage;

public class ControladorAyuda
{

    /*
    Creamos un metodo para cerrar la ayuda y vover a la página anterior.
     */
    @FXML
    private void cerrarAyuda(ActionEvent event) {

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.close();
    }

}
