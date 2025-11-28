package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.DAO.usuario.FavoritosDAO; // Necesitas un DAO de Favoritos
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class ControladorLibroCatalogo { // Asumimos este nombre para la tarjeta

    @FXML private Label lblTitulo;
    @FXML private Label lblAutor;
    @FXML private Label lblGenero;
    @FXML private Label lblDisponibles;
    @FXML private Button btnPedirPrestado;
    @FXML private Button btnVer;
    @FXML private Button btnFavorito; // Asume que es un botón o ToggleButton para el corazón

    private Libro libroActual;
    private Usuario usuarioActual;
    private ControladorCatalogoUsuario controladorPadre;
    private final FavoritosDAO favoritosDAO = new FavoritosDAO();

    public void setDatos(Libro libro, Usuario usuario, ControladorCatalogoUsuario padre)
    {
        this.libroActual = libro;
        this.usuarioActual = usuario;
        this.controladorPadre = padre;

        int disponibles = libro.getCantidadDisponible();
        int stockTotal = libro.getCantidad();

        String disponiblesText = String.format("Disponibles: %d/%d", disponibles, stockTotal);
        lblDisponibles.setText(disponiblesText);

        if (disponibles <= 0)
        {
            btnPedirPrestado.setDisable(true);
            btnPedirPrestado.setText("No disponible");
        }
        else
        {
            btnPedirPrestado.setDisable(false);
            btnPedirPrestado.setText("Pedir Prestado");
        }
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
