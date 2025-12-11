package com.example.biblioteca_digital.controladores;

import com.example.biblioteca_digital.DAO.usuario.ReseñasDAO;
import com.example.biblioteca_digital.modelos.Reseña;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class ControladorReseñas {

    @FXML private ScrollPane sp_resenas;
    @FXML private VBox ap_resenasScroll;
    @FXML private TextArea ta_textoresena;
    @FXML private ComboBox<Integer> cb_puntuacion;
    @FXML private Button bt_publicarResena;

    private final ReseñasDAO reseñasDAO = new ReseñasDAO();
    private int idLibro;
    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        cb_puntuacion.getItems().addAll(1, 2, 3, 4, 5);
    }

    public void setContexto(int idLibro, Usuario usuario) {
        this.idLibro = idLibro;
        this.usuarioActual = usuario;
        cargarReseñas();
    }

    private void cargarReseñas() {
        ap_resenasScroll.getChildren().clear();
        List<Reseña> reseñas = reseñasDAO.obtenerReseñasPorLibro(idLibro);

        for (Reseña r : reseñas) {

            String estrellas = "⭐".repeat(r.getCalificacion());

            Label nombre = new Label(r.getNombreUsuario() + " / " + estrellas);
            Label contenido = new Label(r.getContenido());
            Label fecha = new Label(String.valueOf(r.getFecha()));

            VBox bloque = new VBox(nombre, contenido, fecha);
            bloque.setSpacing(5);
            bloque.setStyle("-fx-padding: 10; -fx-background-color: #f4f4f4; -fx-border-color: #ccc;");

            bloque.setMaxWidth(Double.MAX_VALUE);

            nombre.setMaxWidth(Double.MAX_VALUE);
            contenido.setMaxWidth(Double.MAX_VALUE);
            fecha.setMaxWidth(Double.MAX_VALUE);

            ap_resenasScroll.getChildren().add(bloque);
        }
    }

    @FXML
    private void publicarResena() {
        String texto = ta_textoresena.getText();
        Integer puntuacion = cb_puntuacion.getValue();

        if (texto == null || texto.isBlank() || puntuacion == null) {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Debes escribir una reseña y elegir puntuación.");
            alert.show();
            return;
        }

        Reseña nueva = new Reseña();
        nueva.setId_libro(idLibro);
        nueva.setId_usuario(usuarioActual.getId());
        nueva.setContenido(texto);
        nueva.setCalificacion(puntuacion);

        if (reseñasDAO.guardarReseña(nueva)) {
            ta_textoresena.clear();
            cb_puntuacion.setValue(null);
            cargarReseñas();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR, "Error al guardar la reseña.");
            alert.show();
        }
    }
}
