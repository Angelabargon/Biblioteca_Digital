package com.example.biblioteca_digital.controladores.admin;

/**
 * Imports necesarios para la clase.
 */

import com.example.biblioteca_digital.DAO.usuario.ReseñasDAO;
import com.example.biblioteca_digital.modelos.Reseña;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Controlador encargado de gestionar las reseñas de un libro.
 */
public class ControladorReseñasAdmin {

    /** ScrollPane que contiene el listado de reseñas. */
    @FXML private ScrollPane sp_resenas;

    /** Contenedor donde se añaden dinámicamente los bloques de reseñas. */
    @FXML private VBox ap_resenasScroll;

    /** DAO encargado de gestionar las reseñas en la base de datos. */
    private final ReseñasDAO reseñasDAO = new ReseñasDAO();

    /** ID del libro al que pertenecen las reseñas. */
    private int idLibro;

    /** Usuario actualmente logueado. */
    private Usuario usuarioActual;

    private Runnable actualizarMediaCallback;

    /**
     * Inicializa el controlador cargando las opciones de puntuación.
     * Se ejecuta automáticamente al cargar el FXML.
     */
    @FXML
    public void initialize() {}

    /**
     * Establece el libro y el usuario actual, y carga sus reseñas.
     *
     * @param idLibro ID del libro.
     * @param usuario Usuario logueado.
     */
    public void setContexto(int idLibro, Usuario usuario) {
        this.idLibro = idLibro;
        this.usuarioActual = usuario;
        cargarReseñas();
    }

    /**
     * Obtiene las reseñas del libro desde la base de datos
     * y las muestra en el contenedor visual.
     */
    private void cargarReseñas() {
        ap_resenasScroll.getChildren().clear();
        List<Reseña> reseñas = reseñasDAO.obtenerReseñasPorLibro(idLibro);

        for (Reseña r : reseñas) {

            // De esta forma convertimos la calificación en estrellas.
            String estrellas = "⭐".repeat(r.getCalificacion());

            Label nombre = new Label(r.getNombreUsuario() + " / " + estrellas);
            Label contenido = new Label(r.getContenido());
            Label fecha = new Label(String.valueOf(r.getFecha()));

            // Creamos un bloque visal parecido a una tarjeta, para cada reseña.
            VBox bloque = new VBox(nombre, contenido, fecha);
            bloque.setSpacing(5);
            bloque.getStyleClass().add("resena-card");
            nombre.getStyleClass().add("resena-nombre");
            contenido.getStyleClass().add("resena-contenido");
            fecha.getStyleClass().add("resena-fecha");

            // Se ajusta el ancho para que ocupe completamente el contenedor.
            bloque.setMaxWidth(Double.MAX_VALUE);
            nombre.setMaxWidth(Double.MAX_VALUE);
            contenido.setMaxWidth(Double.MAX_VALUE);
            fecha.setMaxWidth(Double.MAX_VALUE);

            ap_resenasScroll.getChildren().add(bloque);
        }
    }
}
