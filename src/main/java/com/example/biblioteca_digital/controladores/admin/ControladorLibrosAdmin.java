package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.DAO.Admin.LibroAdminDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Optional;

public class ControladorLibrosAdmin {

    @FXML private TableView<Libro> tablaLibros;
    @FXML private TableColumn<Libro, Integer> colId;
    @FXML private TableColumn<Libro, String> colTitulo;
    @FXML private TableColumn<Libro, String> colAutor;
    @FXML private TableColumn<Libro, String> colGenero;
    @FXML private TableColumn<Libro, Integer> colCantidad;
    @FXML private TextField txtBuscar;

    private final LibroAdminDAO libroServicio = new LibroAdminDAO();
    private final ObservableList<Libro> lista = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (colId != null) colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colTitulo != null) colTitulo.setCellValueFactory(new PropertyValueFactory<>("titulo"));
        if (colAutor != null) colAutor.setCellValueFactory(new PropertyValueFactory<>("autor"));
        if (colGenero != null) colGenero.setCellValueFactory(new PropertyValueFactory<>("genero"));
        if (colCantidad != null) colCantidad.setCellValueFactory(new PropertyValueFactory<>("cantidad"));

        cargarLibros();
    }

    private void cargarLibros() {
        lista.setAll(libroServicio.obtenerTodos());
        if (tablaLibros != null) tablaLibros.setItems(lista);
    }

    @FXML public void buscarLibro() {
        String q = txtBuscar != null ? txtBuscar.getText().trim().toLowerCase() : "";
        if (q.isEmpty()) { cargarLibros(); return; }
        ObservableList<Libro> filt = lista.filtered(l ->
                (l.getTitulo()!=null && l.getTitulo().toLowerCase().contains(q)) ||
                        (l.getAutor()!=null && l.getAutor().toLowerCase().contains(q)) ||
                        (l.getGenero()!=null && l.getGenero().toLowerCase().contains(q))
        );
        tablaLibros.setItems(filt);
    }

    @FXML public void abrirAgregarLibro() { abrirEditor(null); }
    @FXML public void editarLibro() {
        Libro sel = tablaLibros.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Selecciona un libro"); return; }
        abrirEditor(sel);
    }
    @FXML public void eliminarLibro() {
        Libro sel = tablaLibros.getSelectionModel().getSelectedItem();
        if (sel == null) { mostrarAlerta("Selecciona un libro"); return; }
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, "Eliminar libro?", ButtonType.OK, ButtonType.CANCEL);
        a.setHeaderText(null);
        Optional<ButtonType> r = a.showAndWait();
        if (r.isPresent() && r.get()==ButtonType.OK) {
            boolean ok = libroServicio.eliminarLibro(sel.getId());
            if (!ok) mostrarAlerta("No se pudo eliminar (referencias).");
            cargarLibros();
        }
    }

    private void abrirEditor(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/editarLibro.fxml"));
            Parent root = loader.load();
            ControladorEditarLibros ctrl = loader.getController();
            Stage st = new Stage();
            st.initOwner(tablaLibros.getScene().getWindow());
            st.initModality(Modality.APPLICATION_MODAL);
            ctrl.setStage(st);
            if (libro != null) ctrl.setLibro(libro);
            ctrl.setOnGuardarCallback(() -> {
                Libro res = ctrl.getLibroResultado();
                if (libro==null) libroServicio.agregarLibro(res);
                else { res.setId(libro.getId()); libroServicio.actualizarLibro(res); }
                cargarLibros();
            });
            st.setScene(new javafx.scene.Scene(root));
            st.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al abrir editor");
        }
    }

    private void mostrarAlerta(String t) { Alert a=new Alert(Alert.AlertType.WARNING); a.setHeaderText(null); a.setContentText(t); a.showAndWait(); }
}


