package com.example.biblioteca_digital.controladores.usuario;

/**
 * Imports necesarios de la clase.
 */
import com.example.biblioteca_digital.modelos.Prestamo;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;

/**
 * Controlador encargado de gestionar la vista de lectura de un libro prestado.
 * El controlador recibe un objeto Prestamo, desde el cual obtiene
 * los datos del libro y la fecha de devolución.
 */
public class ControladorLeerLibro {

    /** Etiqueta que muestra el título del libro. */
    @FXML private Label lblTitulo;
    /** Etiqueta que muestra el autor del libro. */
    @FXML private Label lblAutor;
    /** Etiqueta que muestra los días restantes del préstamo. */
    @FXML private Label lblDiasRestantes;
    /** Área de texto donde se muestra el contenido completo del libro. */
    @FXML private TextArea areaContenido;

    /**
     * Metodo que carga el contenido del préstamo en la vista de lectura.
     *
     * @param prestamo Objeto Prestamo que contiene el libro y la fecha de fin.
     */
    public void cargarContenido(Prestamo prestamo) {
        // Muestra los datos del libro.
        lblTitulo.setText(prestamo.getLibro().getTitulo());
        lblAutor.setText(
                prestamo.getLibro().getAutor() != null
                        ? "por " + prestamo.getLibro().getAutor()
                        : "Autor desconocido"
        );

        // Muestra el contenido del libro.
        areaContenido.setText(
                prestamo.getLibro().getContenido() != null
                        ? prestamo.getLibro().getContenido()
                        : "Este libro no tiene contenido disponible."
        );

        // Calcula los días que quedan del préstamo.
        long dias = java.time.temporal.ChronoUnit.DAYS.between(java.time.LocalDate.now(), prestamo.getFecha_fin());
        if (dias > 0) {
            lblDiasRestantes.setText(dias + " días restantes");
            lblDiasRestantes.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black; -fx-padding: 5px; -fx-border-radius: 5px;");

        } else if (dias == 0) {
            lblDiasRestantes.setText("Vence hoy");
            lblDiasRestantes.setStyle("-fx-background-color: #ffc107; -fx-text-fill: black; -fx-padding: 5px; -fx-border-radius: 5px;");

        } else {
            lblDiasRestantes.setText("Vencido hace " + Math.abs(dias) + " días");
            lblDiasRestantes.setStyle("-fx-background-color: #dc3545; -fx-text-fill: white; -fx-padding: 5px; -fx-border-radius: 5px;");
        }
    }

    /**
     * Metodo para cerrar la ventana de leerlibro.
     */
    @FXML
    private void cerrarVentana() {
        Stage stage = (Stage) lblTitulo.getScene().getWindow();
        stage.close();
    }
}
