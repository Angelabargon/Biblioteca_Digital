package com.example.biblioteca_digital.controladores.usuario;

/**
 * Imports necesarios de la clase.
 */
import com.example.biblioteca_digital.DAO.usuario.FavoritosDAO; // Importar la nueva clase DAO
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import java.io.IOException;
import java.util.List;

/**
 * Controlador encargado de gestionar la vista de libros favoritos del usuario.
 */
public class ControladorFavoritosUsuario {

    /** Lista de favoritos. */
    @FXML private ListView<Libro> listaFavoritos;

    /** Etiquetas que muestran los detalles del libro seleccionado. */
    @FXML private Label tituloLabel;
    @FXML private Label autorLabel;
    @FXML private Label categoriaLabel;


    /** Botón para eliminar un libro de favoritos. */
    @FXML private Button btnEliminar;

    /** ScrollPane que contiene el FlowPane de tarjetas. */
    @FXML private ScrollPane scrollPaneFavoritos;

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

            // Espacio vertical entre tarjetas.
            contenedorFavoritos.setVgap(15);

            // Espacio horizontal entre tarjetas.
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
     * Metodo que muestra los detalles del libro seleccionado en las etiquetas.
     * (parte del clickver)
     */
    public void mostrarDetalles(Libro libro) {

        if (libro == null) {
            tituloLabel.setText("");
            autorLabel.setText("");
            categoriaLabel.setText("");
            return;
        }

        tituloLabel.setText(libro.getTitulo());
        autorLabel.setText(libro.getAutor());
        categoriaLabel.setText(libro.getGenero());
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
            controlador.setDatos(libro, usuarioActual, null);
            return item;

        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Error al cargar item: " + libro.getTitulo());
        }
    }
}
