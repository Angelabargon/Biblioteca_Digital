package com.example.biblioteca_digital.controladores.usuario;

/**
 * Imports necesarios de la clase.
 */
import com.example.biblioteca_digital.DAO.usuario.FavoritosDAO;
import com.example.biblioteca_digital.controladores.ControladorReseñas;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Controlador encargado de gestionar la vista individual de un libro.
 */
public class ControladorLibrosIndividual {

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

    /** Contenedor donde se cargan las reseñas del libro. */
    @FXML private VBox vb_contenedorResenas;

    /** Controlador encargado de gestionar las reseñas dentro de esta vista. */
    @FXML private ControladorReseñas vb_contenedorResenasController;

    /** Libro actualmente mostrado en la vista. */
    private Libro libroActual;

    /** Usuario que está visualizando el libro. */
    private Usuario usuarioActual;

    /** DAO para gestionar favoritos (reservado para futuras funciones). */
    private final FavoritosDAO favoritosDAO = new FavoritosDAO();

    /**
     * Metodo de elección de libros.
     *
     * @param libro   Libro cuyos detalles se van a mostrar.
     * @param usuario Usuario actualmente logueado.
     */
    public void setLibro(Libro libro, Usuario usuario) {

        this.libroActual = libro;
        this.usuarioActual = usuario;

        String nombreArchivo = libro.getFoto();

        if (nombreArchivo != null && !nombreArchivo.isEmpty()) {
            String rutaBase = "/com/example/biblioteca_digital/imagenes/libros/";
            String rutaCompleta = rutaBase + nombreArchivo;

            // El nombre del libro es clave para el mensaje de error.
            String tituloLibro = (libro != null && libro.getTitulo() != null) ? libro.getTitulo() : "Libro Desconocido";

            // Carga con el nombre de archivo exacto que está en la BD.
            try {
                Image portada = new Image(getClass().getResourceAsStream(rutaCompleta));
                if (!portada.isError()) {
                    imagenLibro.setImage(portada);
                }

            } catch (Exception e) {
                System.err.println("Advertencia: No se pudo cargar la imagen para el libro " + tituloLibro + ". Ruta esperada: " + rutaCompleta + "." + e);
            }
        }

        tituloLabel.setText(libro.getTitulo());
        autorLabel.setText(libro.getAutor());
        categoriaLabel.setText(libro.getGenero());
        isbnLabel.setText(libro.getIsbn());

        // Disponibilidad actual.
        String disponiblesText = String.format("Disponibles: %d/%d", libro.getCantidadDisponible(), libro.getCantidad());
        disponiblesLabel.setText(disponiblesText);

        // Descripción del libro.
        String isbnText = String.format("ISBN: %s", libro.getDescripcion());
        descripcionArea.setText(isbnText);

        // Se pasa el ID del libro y el usuario actual al controlador de reseñas.
        vb_contenedorResenasController.setContexto(libro.getId(), usuarioActual);
    }
}