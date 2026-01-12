package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.DAO.usuario.ReseñasDAO;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Controlador encargado de mostrar la vista individual
 * de un libro dentro del panel de administración.
 *
 * <p>
 * Presenta toda la información detallada del libro
 * (datos generales, disponibilidad, descripción,
 * calificación media y reseñas).
 * </p>
 *
 * <p>
 * En el modo administrador, las reseñas se muestran
 * únicamente en modo lectura.
 * </p>
 */
public class ControladorLibroIndividualAdmin {

    /** Etiqueta que muestra el título del libro. */
    @FXML private Label tituloLabel;

    /** Etiqueta que muestra el autor del libro. */
    @FXML private Label autorLabel;

    /** Etiqueta que muestra la categoría o género del libro. */
    @FXML private Label categoriaLabel;

    /** Etiqueta que muestra el ISBN del libro. */
    @FXML private Label isbnLabel;

    /** Etiqueta que muestra la cantidad de libros disponibles. */
    @FXML private Label disponiblesLabel;

    /** Imagen de la portada del libro. */
    @FXML private ImageView imagenLibro;

    /** Etiqueta que muestra la descripción del libro. */
    @FXML private Label descripcionArea;

    /** Etiqueta que muestra la calificación media del libro. */
    @FXML private Label calificacionMedia;

    /** Contenedor visual de las reseñas del libro. */
    @FXML private VBox vb_contenedorResenas;

    /**
     * Controlador asociado al contenedor de reseñas.
     * Se utiliza para cargar las reseñas en modo solo lectura.
     */
    @FXML private ControladorReseñasAdmin vb_contenedorResenasController;

    /** Libro actualmente mostrado en la vista. */
    private Libro libroActual;

    /**
     * Usuario actual del sistema.
     * <p>
     * En el contexto de administración es siempre null,
     * ya que el administrador no puede crear reseñas.
     * </p>
     */
    private Usuario usuarioActual = null;

    /**
     * Asigna el libro a mostrar y carga toda su información
     * en la vista individual.
     *
     * @param libro libro cuyos datos serán mostrados
     */
    public void setLibro(Libro libro) {
        this.libroActual = libro;

        String ruta = "/com/example/biblioteca_digital/imagenes/libros/" +
                (libro.getFoto() != null && !libro.getFoto().isEmpty()
                        ? libro.getFoto()
                        : "generica.jpg");

        try {
            imagenLibro.setImage(new Image(getClass().getResourceAsStream(ruta)));
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + ruta);
        }

        tituloLabel.setText(libro.getTitulo());
        autorLabel.setText(libro.getAutor());
        categoriaLabel.setText(libro.getGenero());
        isbnLabel.setText("ISBN: " + libro.getIsbn());

        String disponiblesText = String.format(
                "Disponibles: %d/%d",
                libro.getCantidadDisponible(),
                libro.getCantidad()
        );
        disponiblesLabel.setText(disponiblesText);

        descripcionArea.setText(libro.getDescripcion());

        ReseñasDAO dao = new ReseñasDAO();
        double media = dao.obtenerPuntuacionMedia(libro.getId());

        if (media > 0) {
            calificacionMedia.setText(
                    String.format("Puntuación media: %.1f / 5", media)
            );
        } else {
            calificacionMedia.setText("Puntuación media: Sin reseñas");
        }

        vb_contenedorResenasController.setContexto(libro.getId(), null);
    }
}
