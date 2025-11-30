package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.DAO.usuario.FavoritosDAO;
import com.example.biblioteca_digital.DAO.usuario.PrestamoDAO;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class ControladorLibrosIndividual
{
    @FXML private Label tituloLabel;
    @FXML private Label autorLabel;
    @FXML private Label categoriaLabel;
    @FXML private Label isbnLabel;
    @FXML private Label disponiblesLabel;
    @FXML private ImageView imagenLibro;
    @FXML private TextArea descripcionArea;
    @FXML private Button btnAgregarFavorito;
    @FXML private Button btnPedirPrestado;

    private Libro libroActual;
    private Usuario usuarioActual;
    private final FavoritosDAO favoritosDAO = new FavoritosDAO();

    /**
     * Método de elección de libros
     * @param libro
     * @param usuario
     */
    public void setLibro(Libro libro, Usuario usuario)
    {
        this.libroActual = libro;
        this.usuarioActual = usuario;

        tituloLabel.setText(libro.getTitulo());
        autorLabel.setText(libro.getAutor());
        categoriaLabel.setText(libro.getGenero());
        isbnLabel.setText(libro.getIsbn());
        disponiblesLabel.setText(String.format("Disponibles: %d", libro.getCantidad()));

        if (descripcionArea instanceof TextArea)
        {
            ((TextArea) descripcionArea).setText(libro.getDescripcion());
        }

        if (libro.getFoto() != null)
            imagenLibro.setImage(new Image(libro.getFoto()));

        btnPedirPrestado.setDisable(libro.getCantidad() <= 0);
        actualizarBotonFavorito(); // Nuevo método para mostrar el estado
    }
    /**
     * Método de actualización de favoritos
     */
    private void actualizarBotonFavorito()
    {
        boolean esFavorito = favoritosDAO.esFavorito(usuarioActual.getId(), libroActual.getId());
        btnAgregarFavorito.setText(esFavorito ? "❤ En Favoritos" : "♡ Añadir a Favoritos");
    }
}