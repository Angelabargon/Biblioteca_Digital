package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.modelos.Prestamo;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

public class ControladorLeerLibro
{
    @FXML private Label lblTitulo;
    @FXML private Label lblAutor;
    @FXML private Label lblDiasRestantes; // El contador en la esquina superior
    @FXML private TextArea areaContenido;
    @FXML private HBox headerBar; // Para la barra de color y el botón de cerrar

    /**
     * Carga el contenido del préstamo en la vista de lectura.
     */
    public void cargarContenido(Prestamo prestamo)
    {
        // Cargar datos del libro
        lblTitulo.setText(prestamo.getLibro().getTitulo());
        lblAutor.setText("por " + prestamo.getLibro().getAutor());

        areaContenido.setText(prestamo.getLibro().getDescripcion());

        long dias = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), prestamo.getFecha_fin());
        if (dias >= 0)
        {
            lblDiasRestantes.setText(String.format("%d días restantes", dias));
        }
        else
        {
            lblDiasRestantes.setText(String.format("%d días restantes", dias));
        }
        if (dias <= 0)
        {
            lblDiasRestantes.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 5px; -fx-border-radius: 5px;");
        }
        else
        {
            lblDiasRestantes.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black; -fx-padding: 5px; -fx-border-radius: 5px;");
        }
    }

    @FXML
    private void cerrarVentana()
    {
        Stage stage = (Stage) lblTitulo.getScene().getWindow();
        stage.close();
    }
}
