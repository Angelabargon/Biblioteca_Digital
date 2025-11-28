package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.DAO.usuario.FavoritosDAO;
import com.example.biblioteca_digital.DAO.usuario.PrestamoDAO;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

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
    @FXML private Button btnVolver; // Añadido para el botón del FXML

    private Libro libroActual;
    private Usuario usuarioActual;
    private final FavoritosDAO favoritosDAO = new FavoritosDAO();

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

    private void actualizarBotonFavorito()
    {
        boolean esFavorito = favoritosDAO.esFavorito(usuarioActual.getId(), libroActual.getId());
        btnAgregarFavorito.setText(esFavorito ? "❤️ En Favoritos" : "♡ Añadir a Favoritos");
    }

    @FXML
    private void handleAlternarFavorito()
    {
        if (libroActual != null && usuarioActual != null)
        {
            favoritosDAO.alternarFavorito(usuarioActual.getId(), libroActual.getId());
            actualizarBotonFavorito();
            mensaje("Estado de favoritos actualizado.");
        }
    }

    @FXML
    private void handlePedirPrestado()
    {
        boolean exito = new PrestamoDAO().crearPrestamo(usuarioActual.getId(), libroActual.getId());

        if (exito) {
            mensaje("¡Préstamo exitoso! Recarga el catálogo para ver el stock actualizado.");
            // Opcional: Cerrar la ventana de detalles después del préstamo
            handleVolver(new ActionEvent(btnPedirPrestado, null));
        } else {
            mensaje("No se pudo crear el préstamo. Verifica la disponibilidad.");
        }
    }

    @FXML
    private void handleVolver(ActionEvent event)
    {
        // Cierra la ventana modal actual
        Stage stage = (Stage) btnVolver.getScene().getWindow();
        stage.close();
    }

    private void mensaje(String texto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(texto);
        alert.showAndWait();
    }
}