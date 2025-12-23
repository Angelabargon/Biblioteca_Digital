package com.example.biblioteca_digital.controladores;

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
public class ControladorReseñas {

    /** ScrollPane que contiene el listado de reseñas. */
    @FXML private ScrollPane sp_resenas;

    /** Contenedor donde se añaden dinámicamente los bloques de reseñas. */
    @FXML private VBox ap_resenasScroll;

    /** Área de texto donde el usuario escribe su reseña. */
    @FXML private TextArea ta_textoresena;

    /** ComboBox para seleccionar la puntuación (1 a 5 estrellas). */
    @FXML private ComboBox<Integer> cb_puntuacion;

    /** Botón para publicar la reseña. */
    @FXML private Button bt_publicarResena;

    /** DAO encargado de gestionar las reseñas en la base de datos. */
    private final ReseñasDAO reseñasDAO = new ReseñasDAO();

    /** ID del libro al que pertenecen las reseñas. */
    private int idLibro;

    /** Usuario actualmente logueado. */
    private Usuario usuarioActual;

    /**
     * Inicializa el controlador cargando las opciones de puntuación.
     * Se ejecuta automáticamente al cargar el FXML.
     */
    @FXML
    public void initialize() {
        cb_puntuacion.getItems().addAll(1, 2, 3, 4, 5);
    }

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

    /**
     * Publica una nueva reseña escrita por el usuario.
     * Valida los campos, guarda la reseña y recarga la lista.
     */
    @FXML
    private void publicarResena() {

        String texto = ta_textoresena.getText();
        Integer puntuacion = cb_puntuacion.getValue();

        // Validaciones.
        if (texto == null || texto.isBlank() || puntuacion == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Debes escribir una reseña y elegir puntuación.");
            alert.show();
            return;
        }

        // Construimos el objeto reseña.
        Reseña nueva = new Reseña();
        nueva.setId_libro(idLibro);
        nueva.setId_usuario(usuarioActual.getId());
        nueva.setContenido(texto);
        nueva.setCalificacion(puntuacion);

        // Se guardan en la base de datos.
        if (reseñasDAO.guardarReseña(nueva)) {

            // Se limpian los campos.
            ta_textoresena.clear();
            cb_puntuacion.setValue(null);

            // Se recargan las reseñas.
            cargarReseñas();

        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al guardar la reseña.");
            alert.show();
        }
    }
}
