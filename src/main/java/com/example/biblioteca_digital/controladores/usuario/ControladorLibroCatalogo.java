
package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.DAO.usuario.FavoritosDAO; // Necesitas un DAO de Favoritos
import com.example.biblioteca_digital.DAO.usuario.PrestamoDAO;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.util.List;
import java.util.Objects;

public class ControladorLibroCatalogo
{

    @FXML private Label lblTitulo;
    @FXML private Label lblAutor;
    @FXML private Label lblGenero;
    @FXML private ImageView imgPortada;
    @FXML private Label lblDisponibles;
    @FXML private Button btnPedirPrestado;
    @FXML private Label lblNoDisponibleTag;
    @FXML private Button btnVer;
    @FXML private Button btnFavorito;

    private Libro libroActual;
    private Usuario usuarioActual;
    private ControladorCatalogoUsuario controladorPadre;
    private final FavoritosDAO favoritosDAO = new FavoritosDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    public void setDatos(Libro libro, Usuario usuario, ControladorCatalogoUsuario padre)
    {
        this.libroActual = libro;
        this.usuarioActual = usuario;
        this.controladorPadre = padre;

        if (usuarioActual == null) {
            System.err.println("Advertencia: Usuario no definido.");
            btnPedirPrestado.setDisable(true);
            btnFavorito.setDisable(true);
            return;
        }

        lblTitulo.setText(libro.getTitulo());
        lblAutor.setText(libro.getAutor());
        lblGenero.setText(libro.getGenero());

        String nombreArchivo = libro.getFoto();

        if (nombreArchivo != null && !nombreArchivo.isEmpty())
        {
            String rutaBase = "/com/example/biblioteca_digital/imagenes/libros/";
            String rutaCompleta = rutaBase + nombreArchivo;
            // El nombre del libro es clave para el mensaje de error
            String tituloLibro = (libro != null && libro.getTitulo() != null) ? libro.getTitulo() : "Libro Desconocido";
            // Carga con el nombre de archivo exacto que está en la BD
            try
            {
                Image portada = new Image(getClass().getResourceAsStream(rutaCompleta));
                if (!portada.isError())
                {
                    imgPortada.setImage(portada);
                }
            }
            catch (Exception e)
            {
                System.err.println("Advertencia: No se pudo cargar la imagen para el libro " + tituloLibro + ". Ruta esperada: " + rutaCompleta + "." + e);
            }
        }
        int disponibles = libro.getCantidadDisponible();
        int stockTotal = libro.getCantidad();
        boolean yaEstaPrestado = prestamoDAO.esLibroPrestadoPorUsuario(usuarioActual.getId(), libro.getId());
        String disponiblesText = String.format("Disponibles: %d/%d", disponibles, stockTotal);
        lblDisponibles.setText(disponiblesText);
        // Mira si está disponible o no el libro y si el usuario lo tiene prestado ya o no
        if (disponibles <= 0 || yaEstaPrestado)
        {
            lblDisponibles.setText("No disponible");

            if (lblNoDisponibleTag != null) {
                lblNoDisponibleTag.setText(yaEstaPrestado ? "Ya Prestado" : "Agotado");
                lblNoDisponibleTag.setVisible(true);
                lblNoDisponibleTag.setManaged(true);
            }

            btnPedirPrestado.setDisable(true);
            btnPedirPrestado.getStyleClass().add("btn-prestar-disabled");
        }
        else
        {
            lblDisponibles.setVisible(true);
            lblDisponibles.setManaged(true);

            if (lblNoDisponibleTag != null) {
                lblNoDisponibleTag.setVisible(false);
                lblNoDisponibleTag.setManaged(false);
            }

            btnPedirPrestado.setDisable(false);
            btnPedirPrestado.getStyleClass().remove("btn-prestar-disabled");
            btnPedirPrestado.getStyleClass().add("btn-prestar");
        }
        actualizarBotonFavorito();
    }

    /**
     * Método de botón pedir prestado
     */
    @FXML
    private void handlePedirPrestado()
    {
        if (libroActual != null && controladorPadre != null)
        {
            controladorPadre.clickPedirPrestamo(libroActual);
        }
    }

    /**
     * Método de botón ver detalles
     */
    @FXML
    private void handleVerDetalles()
    {
        if (libroActual != null && controladorPadre != null)
        {
            controladorPadre.clickVer(libroActual);
        }
    }

    /**
     * Método de botón añadir o eliminar de favoritos
     */
    @FXML
    private void handleAlternarFavorito()
    {
        if (libroActual != null && usuarioActual != null)
        {
            favoritosDAO.alternarFavorito(usuarioActual.getId(), libroActual.getId());
            actualizarBotonFavorito();
        }
    }

    /**
     * Método de actualización de favorito
     */
    private void actualizarBotonFavorito()
    {
        if (usuarioActual == null) {
            System.err.println("Advertencia: Usuario no definido.");
            btnPedirPrestado.setDisable(true);
            btnFavorito.setDisable(true);
            return;
        }
        boolean esFavorito = favoritosDAO.esFavorito(usuarioActual.getId(), libroActual.getId());
        if (esFavorito)
        {
            btnFavorito.setText("❤");
            btnFavorito.getStyleClass().add("favorito-activo");
        }
        else
        {
            btnFavorito.setText("♡");
            btnFavorito.getStyleClass().remove("favorito-activo");
        }
    }
}