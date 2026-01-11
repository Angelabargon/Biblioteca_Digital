package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.DAO.usuario.CatalogoDAO;
import com.example.biblioteca_digital.controladores.admin.ControladorLibroCatalogoAdmin;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class ControladorCatalogoAdmin {

    private static final String FXML_CARD_PATH =
            "/com/example/biblioteca_digital/vistas/admin/VistaTarjetaLibroAdmin.fxml";

    private static final List<String> GENEROS_ESTATICOS = Arrays.asList(
            "Todas", "Ficción", "Clásicos", "Tragedia", "Terror",
            "Romance", "Ciencia Ficción", "Ciencia", "Misterio", "Fantasía"
    );

    private Usuario usuarioActual;

    private final CatalogoDAO catalogoDAO = new CatalogoDAO();

    @FXML private Label labelBienvenida;
    @FXML private FlowPane contenedorLibros;
    @FXML private TextField filtroTitulo;
    @FXML private TextField filtroAutor;
    @FXML private ChoiceBox<String> filtroGenero;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;

        if (labelBienvenida != null && usuario != null) {
            labelBienvenida.setText("Administrador: " + usuario.getNombre());
        }

        cargarFiltroGeneros();
        mostrarLibrosFiltrados();
    }

    @FXML
    public void initialize() {

        cargarFiltroGeneros();      // 🔥 ESTO ES LO QUE FALTABA
        mostrarLibrosFiltrados();   // opcional pero recomendable

        if (filtroTitulo != null)
            filtroTitulo.textProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());

        if (filtroAutor != null)
            filtroAutor.textProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());

        if (filtroGenero != null)
            filtroGenero.getSelectionModel()
                    .selectedItemProperty()
                    .addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());
    }


    private void cargarFiltroGeneros() {
        filtroGenero.setItems(FXCollections.observableArrayList(GENEROS_ESTATICOS));
        filtroGenero.getSelectionModel().selectFirst();
    }

    @FXML
    public void mostrarLibrosFiltrados() {

        contenedorLibros.getChildren().clear();

        List<Libro> libros = catalogoDAO.cargarCatalogo(
                filtroTitulo.getText(),
                filtroAutor.getText(),
                filtroGenero.getValue()
        );

        for (Libro libro : libros) {
            contenedorLibros.getChildren().add(crearVistaLibroItem(libro));
        }
    }

    private Node crearVistaLibroItem(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(FXML_CARD_PATH));
            Parent item = loader.load();

            ControladorLibroCatalogoAdmin controlador = loader.getController();
            controlador.setDatos(libro, this);

            return item;
        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Error cargando libro");
        }
    }

    public void clickVer(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/biblioteca_digital/vistas/admin/VistaLibroIndividualAdmin.fxml")
            );
            Parent root = loader.load();

            ControladorLibroIndividualAdmin controlador = loader.getController();
            controlador.setLibro(libro);

            Stage stage = new Stage();
            stage.setTitle(libro.getTitulo());
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
