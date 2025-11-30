package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.DAO.admin.LibroAdminDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

/**
 * Controlador del catálogo visible por el administrador.
 * <p>
 * Permite visualizar todos los libros registrados, consultar su descripción
 * y realizar búsquedas por título, autor o género.
 */
public class ControladorCatalogoAdmin {

    /** Lista donde se muestran los libros cargados desde la base de datos. */
    @FXML private ListView<Libro> listLibros;

    /** Área donde se muestra la descripción del libro seleccionado. */
    @FXML private TextArea areaDescripcion;

    /** Campo de texto utilizado para realizar la búsqueda en el catálogo. */
    @FXML private TextField txtBuscar;

    /** Servicio que gestiona consultas relacionadas con libros. */
    private final LibroAdminDAO libroServicio = new LibroAdminDAO();

    /** Lista observable que contiene todos los libros cargados. */
    private final ObservableList<Libro> lista = FXCollections.observableArrayList();

    /**
     * Método inicializador de JavaFX.
     * <p>
     * Carga todos los libros, los asigna a la lista y añade un listener
     * para mostrar la descripción del libro seleccionado.
     */
    @FXML public void initialize() {
        lista.setAll(libroServicio.obtenerTodos());
        if (listLibros!=null) listLibros.setItems(lista);
        if (listLibros!=null) listLibros.getSelectionModel().selectedItemProperty().addListener((obs,oldV,newV)->{
            if (newV!=null && areaDescripcion!=null) areaDescripcion.setText(newV.getDescripcion());
        });
    }

    /**
     * Realiza una búsqueda en el catálogo de libros.
     * <p>
     * Filtra los resultados según si el texto ingresado coincide parcial o totalmente
     * con el título, autor o género de un libro.
     * <p>
     * Si el campo de búsqueda está vacío, se restablece la lista completa.
     */
    @FXML public void buscarCatalogo() {
        String q = txtBuscar!=null? txtBuscar.getText().trim().toLowerCase() : "";

        // Si está vacío, restauramos la lista original
        if (q.isEmpty()) { listLibros.setItems(lista); return; }

        // Aplicar filtro
        ObservableList<Libro> filt = lista.filtered(l ->
                (l.getTitulo()!=null && l.getTitulo().toLowerCase().contains(q)) ||
                        (l.getAutor()!=null && l.getAutor().toLowerCase().contains(q)) ||
                        (l.getGenero()!=null && l.getGenero().toLowerCase().contains(q))
        );
        listLibros.setItems(filt);
    }
}
