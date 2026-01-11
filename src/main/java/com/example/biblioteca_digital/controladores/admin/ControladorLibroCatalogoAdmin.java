package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.modelos.Libro;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controlador encargado de gestionar la tarjeta individual
 * de un libro dentro del catálogo del administrador.
 *
 * <p>
 * Se utiliza para mostrar información básica del libro
 * (título, autor, género, disponibilidad y portada)
 * y permitir el acceso a la vista detallada del mismo.
 * </p>
 */
public class ControladorLibroCatalogoAdmin {

    /** Etiqueta que muestra el título del libro. */
    @FXML private Label lblTitulo;

    /** Etiqueta que muestra el autor del libro. */
    @FXML private Label lblAutor;

    /** Etiqueta que muestra el género del libro. */
    @FXML private Label lblGenero;

    /** Imagen de la portada del libro. */
    @FXML private ImageView imgPortada;

    /** Etiqueta que muestra la cantidad de libros disponibles. */
    @FXML private Label lblDisponibles;

    /** Botón para acceder a la vista detallada del libro. */
    @FXML private Button btnVer;

    /** Libro asociado a esta tarjeta del catálogo. */
    private Libro libroActual;

    /** Controlador padre del catálogo, utilizado para navegación. */
    private ControladorCatalogoAdmin controladorPadre;

    /**
     * Asigna los datos del libro a la tarjeta y
     * establece la referencia al controlador padre.
     *
     * @param libro libro cuyos datos se mostrarán
     * @param padre controlador del catálogo que gestiona la vista principal
     */
    public void setDatos(Libro libro, ControladorCatalogoAdmin padre) {
        this.libroActual = libro;
        this.controladorPadre = padre;

        lblTitulo.setText(libro.getTitulo());
        lblAutor.setText(libro.getAutor());
        lblGenero.setText(libro.getGenero());

        lblDisponibles.setText(
                "Disponibles: " + libro.getCantidad() + "/" + libro.getCantidadDisponible()
        );

        String ruta;

        // Si el libro tiene imagen asignada, se usa; si no, se carga una genérica
        if (libro.getFoto() != null && !libro.getFoto().trim().isEmpty()) {
            ruta = "/com/example/biblioteca_digital/imagenes/libros/" + libro.getFoto();
        } else {
            ruta = "/com/example/biblioteca_digital/imagenes/libros/generica.jpg";
        }

        try {
            Image portada = new Image(getClass().getResourceAsStream(ruta));

            // Si la imagen indicada no existe, se carga la imagen genérica
            if (portada.isError()) {
                System.err.println("No se encontró el archivo: " + ruta);
                ruta = "/com/example/biblioteca_digital/imagenes/libros/generica.jpg";
                portada = new Image(getClass().getResourceAsStream(ruta));
            }

            imgPortada.setImage(portada);

        } catch (Exception e) {
            System.err.println("Error crítico cargando imagen: " + e.getMessage());
        }
    }

    /**
     * Maneja la acción del botón "Ver detalles".
     * <p>
     * Solicita al controlador padre que abra la vista
     * detallada del libro seleccionado.
     * </p>
     */
    @FXML
    private void handleVerDetalles() {
        if (libroActual != null) {
            controladorPadre.clickVer(libroActual);
        }
    }
}