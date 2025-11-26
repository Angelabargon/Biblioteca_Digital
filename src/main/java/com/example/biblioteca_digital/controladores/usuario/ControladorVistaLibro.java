package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.modelos.Libro;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ControladorVistaLibro
{
    @FXML private Label tituloLabel;
    @FXML private Label autorLabel;
    @FXML private ImageView imagenLibro;
    @FXML private Button btnFavorito;
    @FXML private Button btnPedir;
    @FXML private Button btnVer;
    private Libro libro;
    private boolean esFavorito;
    private ControladorCatalogoUsuario controladorPadre;

    public void setDatos(Libro libro, boolean esFavorito, ControladorCatalogoUsuario padre) {
        this.libro = libro;
        this.esFavorito = esFavorito;
        this.controladorPadre = padre;

        tituloLabel.setText(libro.getTitulo());
        autorLabel.setText(libro.getAutor());

        if (libro.getFoto() != null) {
            imagenLibro.setImage(new Image(libro.getFoto()));
        }

        btnFavorito.setText(esFavorito ? "♥" : "♡");

        btnFavorito.setOnAction(e -> controladorPadre.clickFavorito(libro));
        btnPedir.setOnAction(e -> controladorPadre.clickPedirPrestamo(libro));
        btnVer.setOnAction(e -> controladorPadre.clickVer(libro));
    }
}
