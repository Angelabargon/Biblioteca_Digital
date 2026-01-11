package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.DAO.usuario.ReseñasDAO;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class ControladorLibroIndividualAdmin {

    @FXML private Label tituloLabel;
    @FXML private Label autorLabel;
    @FXML private Label categoriaLabel;
    @FXML private Label isbnLabel;
    @FXML private Label disponiblesLabel;
    @FXML private ImageView imagenLibro;
    @FXML private Label descripcionArea;
    @FXML private Label calificacionMedia;

    @FXML private VBox vb_contenedorResenas;
    @FXML private ControladorReseñasAdmin vb_contenedorResenasController;

    private Libro libroActual;
    private Usuario usuarioActual = null; // Admin no reseña

    public void setLibro(Libro libro) {
        this.libroActual = libro;

        // Imagen
        String ruta = "/com/example/biblioteca_digital/imagenes/libros/" +
                (libro.getFoto() != null && !libro.getFoto().isEmpty()
                        ? libro.getFoto()
                        : "generica.jpg");

        try {
            imagenLibro.setImage(new Image(getClass().getResourceAsStream(ruta)));
        } catch (Exception e) {
            System.err.println("Error cargando imagen: " + ruta);
        }

        // Datos
        tituloLabel.setText(libro.getTitulo());
        autorLabel.setText(libro.getAutor());
        categoriaLabel.setText(libro.getGenero());
        isbnLabel.setText("ISBN: " + libro.getIsbn());

        disponiblesLabel.setText(
                "Disponibles: " + libro.getCantidadDisponible() + "/" + libro.getCantidad()
        );

        descripcionArea.setText(libro.getDescripcion());

        // Calificación
        ReseñasDAO dao = new ReseñasDAO();
        double media = dao.obtenerPuntuacionMedia(libro.getId());

        if (media > 0) {
            calificacionMedia.setText(String.format("Puntuación media: %.1f / 5", media));
        } else {
            calificacionMedia.setText("Puntuación media: Sin reseñas");
        }

        // Reseñas (solo lectura)
        vb_contenedorResenasController.setContexto(libro.getId(), null);
    }
}