package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.DAO.usuario.FavoritosDAO;
import com.example.biblioteca_digital.DAO.usuario.PrestamoDAO;
import com.example.biblioteca_digital.controladores.ControladorReseñas;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.TextFlow;

public class ControladorLibrosIndividual
{

    @FXML private Label tituloLabel;
    @FXML private Label autorLabel;
    @FXML private Label categoriaLabel;
    @FXML private Label isbnLabel;
    @FXML private Label disponiblesLabel;
    @FXML private ImageView imagenLibro;
    @FXML private Label descripcionArea;
    @FXML private Label calificacionMedia;

    @FXML private VBox vb_contenedorResenas;
    @FXML private ControladorReseñas vb_contenedorResenasController;

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
                    imagenLibro.setImage(portada);
                }
            }
            catch (Exception e)
            {
                System.err.println("Advertencia: No se pudo cargar la imagen para el libro " + tituloLibro + ". Ruta esperada: " + rutaCompleta + "." + e);
            }
        }
        tituloLabel.setText(libro.getTitulo());
        autorLabel.setText(libro.getAutor());
        categoriaLabel.setText(libro.getGenero());
        isbnLabel.setText(libro.getIsbn());
        String disponiblesText = String.format("Disponibles: %d/%d", libro.getCantidadDisponible(), libro.getCantidad());
        disponiblesLabel.setText(disponiblesText);
        String isbnText = String.format("ISBN: %s", libro.getDescripcion());
        descripcionArea.setText(isbnText);
        vb_contenedorResenasController.setContexto(libro.getId(), usuarioActual);
    }
}