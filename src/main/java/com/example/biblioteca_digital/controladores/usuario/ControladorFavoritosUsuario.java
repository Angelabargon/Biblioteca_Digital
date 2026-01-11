package com.example.biblioteca_digital.controladores.usuario;

/**
 * Imports necesarios de la clase.
 */
import com.example.biblioteca_digital.DAO.usuario.FavoritosDAO;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;

/**
 * Controlador encargado de gestionar la vista de los
 * libros favoritos del usuario.
 */
public class ControladorFavoritosUsuario {

    /** Contenedor donde se muestran las tarjetas de libros favoritos. */
    @FXML private FlowPane contenedorFavoritos;
    /** Ruta del FXML que representa cada tarjeta individual de libro. */
    private static final String FXML_CARD_PATH = "/com/example/biblioteca_digital/vistas/usuario/Vista-Tarjeta-Libro.fxml";
    /** Usuario actualmente logueado. */
    private Usuario usuarioActual;
    /** DAO encargado de obtener los libros favoritos del usuario. */
    private final FavoritosDAO favoritosDAO = new FavoritosDAO();

    /**
     * Metodo que inicializa el controlador con el usuario actual.
     */
    public void initialize() {

        if (contenedorFavoritos != null) {
            //Espacio entre las tarjetas
            contenedorFavoritos.setVgap(15);
            contenedorFavoritos.setHgap(15);
        }
    }

    /**
     * Metodo que establece el usuario actual y carga sus libros favoritos.
     *
     * @param usuario El objeto Usuario actualmente logueado.
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;

        if (usuarioActual != null) {
            cargarFavoritos();
        }
    }

    /**
     * Metodo que carga la lista de favoritos en la ListView usando el DAO.
     */
    private void cargarFavoritos() {
        if (usuarioActual == null) return;
        contenedorFavoritos.getChildren().clear();
        List<Libro> librosFavoritos = favoritosDAO.obtenerFavoritos(usuarioActual.getId());

        if (librosFavoritos.isEmpty()) {
            contenedorFavoritos.getChildren().add(new Label("Aún no tienes libros marcados como favoritos."));

        } else {
            for (Libro libro : librosFavoritos) {
                contenedorFavoritos.getChildren().add(crearVistaLibroItem(libro));
            }
        }
    }

    /**
     * Metodo que abre una ventana modal con los detalles individuales del libro.
     *
     * @param libro Libro seleccionado.
     */
    public void clickVer(Libro libro) {

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Libro-Invidual.fxml"));
            Parent root = loader.load();
            ControladorLibrosIndividual controlador = loader.getController();
            controlador.setLibro(libro, usuarioActual);
            Stage stage = new Stage();
            stage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
            stage.setTitle("Detalles de: " + libro.getTitulo());
            stage.setScene(new Scene(root));
            stage.getIcons().add(new Image(getClass().getResourceAsStream("/com/example/biblioteca_digital/imagenes/icono-app-login.png")));
            stage.showAndWait();

        } catch (IOException e) {
            mostrarAlertaError("Error de Vista", "No se pudo cargar la vista individual del libro.");
            e.printStackTrace();
        }
    }

    /**
     * Metodo que maneja las mini tarjetas de libros (son individuales).
     *
     * @param libro Libro seleccionado.
     */
    private Node crearVistaLibroItem(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_CARD_PATH));
            Node item = loader.load();
            ControladorLibroCatalogo controlador = loader.getController();
            controlador.setDatos(libro, usuarioActual, this);
            return item;

        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Error al cargar item: " + libro.getTitulo());
        }
    }

    /**
     * Muestra una alerta de error.
     */
    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
