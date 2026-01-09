package com.example.biblioteca_digital.controladores.usuario;

/**
 * Imports necesarios de la clase.
 */
import com.example.biblioteca_digital.DAO.usuario.ReseñasDAO;
import com.example.biblioteca_digital.controladores.ControladorReseñas;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controlador encargado de gestionar la vista individual de un libro.
 * Muestra la información del libro seleccionado y carga sus reseñas.
 */
public class ControladorLibrosIndividual
{
    /** Título del libro. */
    @FXML private Label tituloLabel;
    /** Autor del libro. */
    @FXML private Label autorLabel;
    /** Género o categoría del libro. */
    @FXML private Label categoriaLabel;
    /** Código ISBN del libro. */
    @FXML private Label isbnLabel;
    /** Etiqueta que muestra la disponibilidad actual del libro. */
    @FXML private Label disponiblesLabel;
    /** Imagen de portada del libro. */
    @FXML private ImageView imagenLibro;
    /** Descripción del libro. */
    @FXML private Label descripcionArea;
    /** Calificación media del libro. */
    @FXML private Label calificacionMedia;
    /** Controlador encargado de gestionar las reseñas dentro de esta vista. */
    @FXML private ControladorReseñas vb_contenedorResenasController;
    /** Libro actualmente mostrado en la vista. */
    private Libro libroActual;
    /** Usuario que está visualizando el libro. */
    private Usuario usuarioActual;

    /**
     * Carga los datos del libro seleccionado en la vista.
     * @param libro   Libro cuyos detalles se van a mostrar.
     * @param usuario Usuario actualmente logueado.
     */
    public void setLibro(Libro libro, Usuario usuario)
    {
        this.libroActual = libro;
        this.usuarioActual = usuario;
        // Portada de libro.
        String nombreArchivo = libro.getFoto();
        if (nombreArchivo != null && !nombreArchivo.isEmpty())
        {
            String rutaBase = "/com/example/biblioteca_digital/imagenes/libros/";
            String rutaCompleta = rutaBase + nombreArchivo;
            // El mensaje de error.
            String tituloLibro = (libro != null && libro.getTitulo() != null) ? libro.getTitulo() : "Libro Desconocido";
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
                e.printStackTrace();
            }
        }
        // Mostramos los datos principales del libro.
        tituloLabel.setText(libro.getTitulo());
        autorLabel.setText(libro.getAutor());
        categoriaLabel.setText(libro.getGenero());
        isbnLabel.setText(libro.getIsbn());
        // Disponibilidad actual.
        String disponiblesText = String.format("Disponibles: %d/%d", libro.getCantidad(), libro.getCantidadDisponible());
        disponiblesLabel.setText(disponiblesText);
        // Descripción del libro.
        String isbnText = String.format("ISBN: %s", libro.getDescripcion());
        descripcionArea.setText(isbnText);
        // Calificación media de reseñas.
        ReseñasDAO reseñasDAO = new ReseñasDAO();
        double media = reseñasDAO.obtenerPuntuacionMedia(libro.getId()); if (media > 0)
        {
            calificacionMedia.setText(String.format("Puntuación media: %.1f / 5", media));
        }
        else
        {
            calificacionMedia.setText("Puntuación media: Sin reseñas");
        }
        // Se pasa el ID del libro y el usuario actual al controlador de reseñas.
        vb_contenedorResenasController.setContexto(libro.getId(), usuarioActual);
    }
}