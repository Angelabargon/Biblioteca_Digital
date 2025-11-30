
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

        lblTitulo.setText(libro.getTitulo());
        lblAutor.setText(libro.getAutor());
        lblGenero.setText(libro.getGenero());

        String nombreArchivo = libro.getFoto();

        if (nombreArchivo != null && !nombreArchivo.isEmpty())
        {
            String rutaBase = "/com/example/biblioteca_digital/imagenes/libros/";
            String rutaCompleta = rutaBase + nombreArchivo;
            boolean imagenCargada = false;

            // El nombre del libro es clave para el mensaje de error.
            String tituloLibro = (libro != null && libro.getTitulo() != null) ? libro.getTitulo() : "Libro Desconocido";


            // --- INTENTO 1: Carga con el nombre de archivo exacto (.jpg o lo que esté en la BD) ---
            try
            {
                Image portada = new Image(getClass().getResourceAsStream(rutaCompleta));

                if (!portada.isError()) {
                    imgPortada.setImage(portada);
                    imagenCargada = true;
                } else {
                    // Si la carga falla, lanzamos una excepción para ir al bloque catch (FALLBACK)
                    throw new Exception("Carga inicial fallida, intentando fallback.");
                }
            }
            catch (Exception e)
            {
                // --- FALLBACK: Intenta buscar con la extensión alternativa (.webp en este caso) ---
                if (!imagenCargada) {
                    String rutaFallback = null;
                    try {
                        // Asume que el nombre sin extensión es la parte hasta el último punto
                        int lastDot = nombreArchivo.lastIndexOf('.');
                        String nombreSinExt = (lastDot > 0) ? nombreArchivo.substring(0, lastDot) : nombreArchivo;
                        rutaFallback = rutaBase + nombreSinExt + ".webp";

                        Image portadaFallback = new Image(getClass().getResourceAsStream(rutaFallback));

                        if (!portadaFallback.isError()) {
                            // Si el fallback funciona, lo establecemos
                            imgPortada.setImage(portadaFallback);
                            imagenCargada = true;
                        }
                    } catch (Exception ex) {
                        // Se ignora el error del fallback, se manejará con el placeholder
                    }

                    // Reportamos el fallo de ambos intentos
                    if (!imagenCargada) {
                        System.err.println("Advertencia: No se pudo cargar la imagen para el libro " + tituloLibro + ". Ruta esperada: " + rutaCompleta + ". Fallback probado: " + rutaFallback);
                    }
                }

                // portada genérica si todo falla ---
                if (!imagenCargada) {
                    String generica = "/com/example/biblioteca_digital/imagenes/libros/generica.jpg";
                    try
                    {
                        Image generica1 = new Image(getClass().getResourceAsStream(generica));
                        if (!generica1.isError()) {
                            imgPortada.setImage(generica1);
                            System.err.println("Advertencia: No se pudo cargar la imagen para el libro " + tituloLibro + ". Se usó una imagen genérica.");
                        } else {
                            System.err.println("Error FATAL: La imagen genérica existe pero no se pudo cargar. (" + generica + ").");
                        }
                    } catch (Exception placeholderEx) {
                        System.err.println("Error FATAL: No se pudo encontrar la imagen genérica en la ruta (" + generica + ").");
                    }
                }
            }
        }
        int disponibles = libro.getCantidadDisponible();
        int stockTotal = libro.getCantidad();
        boolean yaEstaPrestado = prestamoDAO.esLibroPrestadoPorUsuario(usuarioActual.getId(), libro.getId());
        String disponiblesText = String.format("Disponibles: %d/%d", disponibles, stockTotal);
        lblDisponibles.setText(disponiblesText);

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
            btnPedirPrestado.getStyleClass().add("btn-prestar"); // Clase activa
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