package com.example.biblioteca_digital.controladores;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControladorLibros {

    //VISTA

    //Parte superior
    @FXML private Label lblTitulo;

    //Zona izquierda
    @FXML private ImageView imgLibro;
    @FXML private Label lblAutor;
    @FXML private Label lblCategoria;
    @FXML private Label lblISBN;
    @FXML private Label lblDisponibles;
    @FXML private Label lblCalificacionPromedio;

    //Zona derecha
    @FXML private TextArea txtDescripcion;
    @FXML private ListView<String> listaResenas;

    //Zona de enviar reseña
    @FXML private Slider sliderEstrellas;
    @FXML private TextArea txtNuevaResena;
    @FXML private Button btnPublicarResena;

    //Variables
    private Libro libro;
    private Usuario usuarioActual;

    //Metodos de inicializacion
    @FXML
    public void initialize() {
        // Evitar que el usuario escriba enter sin querer
        txtNuevaResena.setWrapText(true);

        sliderEstrellas.setMin(0);
        sliderEstrellas.setMax(5);
        sliderEstrellas.setMajorTickUnit(1);
        sliderEstrellas.setShowTickLabels(true);
        sliderEstrellas.setShowTickMarks(true);

        btnPublicarResena.setOnAction(e -> publicarResena());
    }

    public void setDatos(Libro libro, Usuario usuario) {
        this.libro = libro;
        this.usuarioActual = usuario;

        cargarDatosLibro();
        cargarResenas();
        cargarCalificacionPromedio();
    }

    //Cargar datos del libro
    private void cargarDatosLibro() {

        lblTitulo.setText(libro.getTitulo());

        try {
            imgLibro.setImage(new Image(libro.getFoto()));
        } catch (Exception ignored) {}

        lblAutor.setText(libro.getAutor());
        lblCategoria.setText(libro.getGenero());
        lblISBN.setText(libro.getIsbn());
        lblDisponibles.setText(String.valueOf(libro.getCantidad()));

        txtDescripcion.setText(libro.getDescripcion());
    }

    private void cargarResenas() {
        listaResenas.getItems().clear();

        try (Connection conn = ConexionBD.getConexion()) {

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT contenido, calificacion, fecha FROM reseñas WHERE id_libro = ? ORDER BY fecha DESC"
            );

            ps.setInt(1, libro.getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                String texto =
                        "⭐ " + rs.getInt("calificacion") +
                                " - " + rs.getString("contenido") +
                                " (" + rs.getDate("fecha") + ")";

                listaResenas.getItems().add(texto);
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void cargarCalificacionPromedio() {
        try (Connection conn = ConexionBD.getConexion()) {

            PreparedStatement ps = conn.prepareStatement(
                    "SELECT AVG(calificacion) AS promedio FROM reseñas WHERE id_libro = ?"
            );

            ps.setInt(1, libro.getId());
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                double promedio = rs.getDouble("promedio");
                lblCalificacionPromedio.setText(String.format("⭐ %.1f / 5", promedio));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Publicar reseña
    private void publicarResena() {

        int estrellas = (int) sliderEstrellas.getValue();
        String contenido = txtNuevaResena.getText().trim();

        if (contenido.isEmpty()) {
            mostrarAlerta("La reseña no puede estar vacía.");
            return;
        }

        try (Connection conn = ConexionBD.getConexion()) {

            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO reseñas (id_usuario, id_libro, fecha, contenido, calificacion) VALUES (?, ?, NOW(), ?, ?)"
            );

            ps.setInt(1, usuarioActual.getId());
            ps.setInt(2, libro.getId());
            ps.setString(3, contenido);
            ps.setInt(4, estrellas);

            ps.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }

        // Actualizar interfaz
        txtNuevaResena.clear();
        sliderEstrellas.setValue(0);

        cargarResenas();
        cargarCalificacionPromedio();

        mostrarAlerta("¡Reseña publicada!");
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
