
package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.DAO.usuario.FavoritosDAO; // Necesitas un DAO de Favoritos
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.List;

public class ControladorLibroCatalogo
{

    @FXML private Label lblTitulo;
    @FXML private Label lblAutor;
    @FXML private Label lblGenero;
    @FXML private Label lblDisponibles;
    @FXML private Button btnPedirPrestado;
    @FXML private Button btnVer;
    @FXML private Button btnFavorito;

    private Libro libroActual;
    private Usuario usuarioActual;
    private ControladorCatalogoUsuario controladorPadre;
    private final FavoritosDAO favoritosDAO = new FavoritosDAO();

    public void setDatos(Libro libro, Usuario usuario, ControladorCatalogoUsuario padre)
    {
        this.libroActual = libro;
        this.usuarioActual = usuario;
        this.controladorPadre = padre;

        lblTitulo.setText(libro.getTitulo());
        lblAutor.setText(libro.getAutor());
        lblGenero.setText(libro.getGenero());

        int idLibro = libro.getId();
        int disponibles = libro.getCantidadDisponible();
        int stockTotal = libro.getCantidad();

        String disponiblesText = String.format("Disponibles: %d/%d", disponibles, stockTotal);
        lblDisponibles.setText(disponiblesText);

        if (disponibles <= 0)
        {
            btnPedirPrestado.setDisable(true);
            btnPedirPrestado.setText("No disponible");
            btnPedirPrestado.getStyleClass().remove("book-button-prestado-enabled");
            btnPedirPrestado.getStyleClass().add("book-button-disabled");
        }
        else
        {
            btnPedirPrestado.setDisable(false);
            btnPedirPrestado.setText("Pedir Prestado");
            btnPedirPrestado.getStyleClass().remove("book-button-prestado");
            btnPedirPrestado.getStyleClass().remove("book-button-disabled");
            btnPedirPrestado.getStyleClass().add("book-button-prestado-enabled");
        }
        actualizarBotonFavorito();
    }

    @FXML
    private void handlePedirPrestado()
    {
        if (libroActual != null && controladorPadre != null)
        {
            controladorPadre.clickPedirPrestamo(libroActual);
        }
    }

    @FXML
    private void handleVerDetalles()
    {
        if (libroActual != null && controladorPadre != null)
        {
            controladorPadre.clickVer(libroActual);
        }
    }

    @FXML
    private void handleAlternarFavorito()
    {
        if (libroActual != null && usuarioActual != null)
        {
            favoritosDAO.alternarFavorito(usuarioActual.getId(), libroActual.getId());
            actualizarBotonFavorito();
        }
    }

    private void actualizarBotonFavorito()
    {
        boolean esFavorito = favoritosDAO.esFavorito(usuarioActual.getId(), libroActual.getId());
        if (esFavorito)
        {
            btnFavorito.setText("❤\uFE0F");
        }
        else
        {
            btnFavorito.setText("\uD83E\uDD0D ");
        }
    }
}