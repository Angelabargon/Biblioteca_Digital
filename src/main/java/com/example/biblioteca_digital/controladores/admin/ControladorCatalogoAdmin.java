package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.DAO.admin.LibroAdminDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ControladorCatalogoAdmin {

    @FXML private ListView<Libro> listLibros;
    @FXML private TextArea areaDescripcion;
    @FXML private TextField txtBuscar;

    private final LibroAdminDAO libroServicio = new LibroAdminDAO();
    private final ObservableList<Libro> lista = FXCollections.observableArrayList();

    @FXML public void initialize() {
        lista.setAll(libroServicio.obtenerTodos());
        if (listLibros!=null) listLibros.setItems(lista);
        if (listLibros!=null) listLibros.getSelectionModel().selectedItemProperty().addListener((obs,oldV,newV)->{
            if (newV!=null && areaDescripcion!=null) areaDescripcion.setText(newV.getDescripcion());
        });
    }

    @FXML public void buscarCatalogo() {
        String q = txtBuscar!=null? txtBuscar.getText().trim().toLowerCase() : "";
        if (q.isEmpty()) { listLibros.setItems(lista); return; }
        ObservableList<Libro> filt = lista.filtered(l ->
                (l.getTitulo()!=null && l.getTitulo().toLowerCase().contains(q)) ||
                        (l.getAutor()!=null && l.getAutor().toLowerCase().contains(q)) ||
                        (l.getGenero()!=null && l.getGenero().toLowerCase().contains(q))
        );
        listLibros.setItems(filt);
    }
}
