package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.modelos.Libro;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ControladorLibroCatalogoAdmin {

    @FXML private Label lblTitulo;
    @FXML private Label lblAutor;
    @FXML private Label lblGenero;
    @FXML private ImageView imgPortada;
    @FXML private Label lblDisponibles;
    @FXML private Button btnVer;

    private Libro libroActual;
    private ControladorCatalogoAdmin controladorPadre;

    public void setDatos(Libro libro, ControladorCatalogoAdmin padre) {
        this.libroActual = libro;
        this.controladorPadre = padre;

        lblTitulo.setText(libro.getTitulo());
        lblAutor.setText(libro.getAutor());
        lblGenero.setText(libro.getGenero());

        lblDisponibles.setText(
                "Disponibles: " + libro.getCantidadDisponible() + "/" + libro.getCantidad()
        );

        if (libro.getFoto() != null && !libro.getFoto().isEmpty()) {
            try {
                imgPortada.setImage(new Image(
                        getClass().getResourceAsStream(
                                "/com/example/biblioteca_digital/imagenes/libros/" + libro.getFoto()
                        )
                ));
            } catch (Exception ignored) {}
        }
    }

    @FXML
    private void handleVerDetalles() {
        if (libroActual != null) {
            controladorPadre.clickVer(libroActual);
        }
    }
}
