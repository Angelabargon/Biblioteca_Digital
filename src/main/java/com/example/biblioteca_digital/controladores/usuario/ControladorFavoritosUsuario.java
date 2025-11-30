package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.DAO.usuario.FavoritosDAO; // Importar la nueva clase DAO
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.util.List;

public class ControladorFavoritosUsuario {

    // --- Atributos FXML ---
    @FXML
    private ListView<Libro> listaFavoritos;
    @FXML
    private Label tituloLabel;
    @FXML
    private Label autorLabel;
    @FXML
    private Label categoriaLabel;
    @FXML
    private Button btnEliminar;
    @FXML
    private ScrollPane scrollPaneFavoritos;
    @FXML
    private FlowPane contenedorFavoritos;

    //Ruta fxml a los libros
    private static final String FXML_CARD_PATH = "/com/example/biblioteca_digital/vistas/usuario/Vista-Tarjeta-Libro.fxml";

    //Atributos de Lógica
    private Usuario usuarioActual;
    private final FavoritosDAO favoritosDAO = new FavoritosDAO(); // Instancia del DAO
    /**
     * Inicializa el controlador con el usuario actual.
     */
    public void initialize()
    {
        if (contenedorFavoritos != null)
        {
            contenedorFavoritos.setVgap(15);
            contenedorFavoritos.setHgap(15);
        }
    }
    /**
     * Establece el usuario actual y carga sus libros favoritos.
     * Este método es llamado por el controlador principal (padre) al cambiar de vista.
     * @param usuario El objeto Usuario actualmente logueado.
     */
    public void setUsuario(Usuario usuario)
    {
        this.usuarioActual = usuario;
        if (usuarioActual != null)
        {
            cargarFavoritos();
        }
    }
    /**
     * Carga la lista de favoritos en la ListView usando el DAO.
     */
    private void cargarFavoritos()
    {
        if (usuarioActual == null) return;
        contenedorFavoritos.getChildren().clear();
        List<Libro> librosFavoritos = favoritosDAO.obtenerFavoritos(usuarioActual.getId());
        if (librosFavoritos.isEmpty())
        {
            contenedorFavoritos.getChildren().add(new Label("Aún no tienes libros marcados como favoritos."));
        }
        else
        {
            for (Libro libro : librosFavoritos)
            {
                contenedorFavoritos.getChildren().add(crearVistaLibroItem(libro));
            }
        }
    }

    /**
     * Muestra los detalles del libro seleccionado en las etiquetas.
     * Este método solo funcionará si los detalles se pasan desde la tarjeta del libro.
     */
    public void mostrarDetalles(Libro libro) // Hecho público para ser llamado desde la tarjeta
    {
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

    private Node crearVistaLibroItem(Libro libro)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_CARD_PATH));
            Node item = loader.load();
            ControladorLibroCatalogo controlador = loader.getController();
            controlador.setDatos(libro, usuarioActual, null);
            return item;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return new Label("Error al cargar item: " + libro.getTitulo());
        }
    }
    /**
     * Manejador de evento para eliminar el favorito seleccionado.
     */
    @FXML
    private void eliminarFavorito()
    {
        Libro libro = listaFavoritos.getSelectionModel().getSelectedItem();
        if (libro == null)
        {
            mensaje("Seleccione un libro para eliminar.");
            return;
        }
        // USANDO DAO
        if (favoritosDAO.borrarFavorito(usuarioActual.getId(), libro.getId()))
        {
            mensaje("Favorito eliminado correctamente: " + libro.getTitulo());
        }
        else
        {
            mensaje("Error al eliminar el favorito. Intente de nuevo.");
        }
        cargarFavoritos();
    }
    /**
     * Muestra una alerta de información.
     */
    private void mensaje(String t)
    {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(t);
        a.show();
    }
}
