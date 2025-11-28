package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.DAO.usuario.FavoritosDAO; // Importar la nueva clase DAO
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

    // --- Atributos de Lógica ---
    private Usuario usuarioActual;
    private final FavoritosDAO favoritosDAO = new FavoritosDAO(); // Instancia del DAO
    /**
     * Inicializa el controlador con el usuario actual.
     */
    /**
     * Establece el usuario actual y carga sus libros favoritos.
     * Este método es llamado por el controlador principal (padre) al cambiar de vista.
     * @param usuario El objeto Usuario actualmente logueado.
     */
    public void setUsuario(Usuario usuario)
    {
        this.usuarioActual = usuario;
        cargarFavoritos();
        listaFavoritos.setOnMouseClicked(e -> mostrarDetalles(
                listaFavoritos.getSelectionModel().getSelectedItem()
        ));
    }
    /**
     * Carga la lista de favoritos en la ListView usando el DAO.
     */
    private void cargarFavoritos()
    {
        if (usuarioActual == null) return;
        // USANDO DAO
        List<Libro> libros = favoritosDAO.obtenerFavoritos(usuarioActual.getId());
        listaFavoritos.setItems(FXCollections.observableArrayList(libros));
        // Limpiar detalles si la lista se recarga
        mostrarDetalles(null);
    }

    /**
     * Muestra los detalles del libro seleccionado en las etiquetas.
     */
    private void mostrarDetalles(Libro libro)
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
        // Recargar la lista después de la operación
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
