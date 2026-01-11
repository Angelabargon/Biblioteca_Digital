package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.DAO.usuario.ReseñasDAO;
import com.example.biblioteca_digital.modelos.Reseña;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Controlador encargado de mostrar las reseñas asociadas a un libro
 * dentro del panel de administración.
 *
 * <p>
 * Este controlador funciona en modo solo lectura, permitiendo al
 * administrador visualizar las opiniones de los usuarios sin
 * posibilidad de crear, editar o eliminar reseñas.
 * </p>
 */
public class ControladorReseñasAdmin {

    /** ScrollPane que contiene visualmente el listado de reseñas. */
    @FXML private ScrollPane sp_resenas;

    /** Contenedor vertical donde se insertan dinámicamente las reseñas. */
    @FXML private VBox ap_resenasScroll;

    /** DAO encargado de recuperar las reseñas desde la base de datos. */
    private final ReseñasDAO reseñasDAO = new ReseñasDAO();

    /** Identificador del libro cuyas reseñas se están mostrando. */
    private int idLibro;

    /** Usuario actual (en admin siempre será null). */
    private Usuario usuarioActual;

    /**
     * Inicializa el controlador.
     * <p>
     * No se requiere ninguna configuración adicional al cargar el FXML.
     * </p>
     */
    @FXML
    public void initialize() {
        // Sin inicialización específica
    }

    /**
     * Establece el contexto del controlador indicando el libro
     * del que se mostrarán las reseñas.
     *
     * @param idLibro ID del libro
     * @param usuario usuario actual (null en modo administrador)
     */
    public void setContexto(int idLibro, Usuario usuario) {
        this.idLibro = idLibro;
        this.usuarioActual = usuario;
        cargarReseñas();
    }

    /**
     * Carga las reseñas del libro desde la base de datos
     * y las representa visualmente en forma de tarjetas.
     */
    private void cargarReseñas() {

        ap_resenasScroll.getChildren().clear();

        List<Reseña> reseñas =
                reseñasDAO.obtenerReseñasPorLibro(idLibro);

        for (Reseña r : reseñas) {

            // Conversión de la calificación a estrellas visuales
            String estrellas = "⭐".repeat(r.getCalificacion());

            Label nombre = new Label(
                    r.getNombreUsuario() + " / " + estrellas
            );
            Label contenido = new Label(r.getContenido());
            Label fecha = new Label(String.valueOf(r.getFecha()));

            // Bloque visual de la reseña (tipo tarjeta)
            VBox bloque = new VBox(nombre, contenido, fecha);
            bloque.setSpacing(5);

            bloque.getStyleClass().add("resena-card");
            nombre.getStyleClass().add("resena-nombre");
            contenido.getStyleClass().add("resena-contenido");
            fecha.getStyleClass().add("resena-fecha");

            // Ajuste de anchura para ocupar todo el contenedor
            bloque.setMaxWidth(Double.MAX_VALUE);
            nombre.setMaxWidth(Double.MAX_VALUE);
            contenido.setMaxWidth(Double.MAX_VALUE);
            fecha.setMaxWidth(Double.MAX_VALUE);

            ap_resenasScroll.getChildren().add(bloque);
        }
    }
}