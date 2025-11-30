package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.DAO.admin.LibroAdminDAO;
import com.example.biblioteca_digital.modelos.Libro;
import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

/**
 * Controlador encargado de gestionar la ventana de edición y creación de libros
 * dentro del panel de administración.
 * <p>
 * Permite cargar los datos de un libro, modificarlos y devolver el resultado
 * al controlador principal mediante un callback.
 */
public class ControladorEditarLibros {

    /** Campo de texto para el título del libro. */
    @FXML private TextField txtTitulo;

    /** Campo de texto para el autor del libro. */
    @FXML private TextField txtAutor;

    /** Campo de texto para la categoría o género del libro. */
    @FXML private TextField txtCategoria;

    /** Campo de texto para el ISBN del libro. */
    @FXML private TextField txtIsbn;

    /** Campo de texto para la ruta de la imagen del libro. */
    @FXML private TextField txtFoto;

    /** Campo de texto para la cantidad total de ejemplares. */
    @FXML private TextField txtCantidad;

    /** Área de texto destinada a la descripción del libro. */
    @FXML private TextArea txtDescripcion;

    /** Área de texto destinada al contenido del libro. */
    @FXML private TextArea txtContenido;

    /** Instancia del libro que se está editando. */
    private Libro libro;

    /** Ventana actual donde se está mostrando el editor. */
    private Stage stage;

    /**
     * Función callback que se ejecutará cuando el usuario haga clic en Guardar.
     * Permite comunicar el resultado al controlador que abrió este editor.
     */
    private Runnable onGuardarCallback;

    /** DAO para gestión de libros (aunque aquí no se usa directamente). */
    private final LibroAdminDAO dao = new LibroAdminDAO();

    /**
     * Inicializa la ventana del editor.
     * En este caso no se requiere configuración adicional al cargar la vista.
     */
    @FXML
    public void initialize() {
        // No se necesita inicialización especial.
    }

    /**
     * Establece la ventana actual del editor.
     *
     * @param s instancia de {@link Stage} donde está cargada la vista.
     */
    public void setStage(Stage s) {
        this.stage = s;
    }

    /**
     * Carga los datos de un libro existente en los campos del editor.
     *
     * @param l libro cuyos datos serán mostrados. Si es null, el editor queda vacío.
     */
    public void setLibro(Libro l) {
        this.libro = l;

        if (l != null) {
            txtTitulo.setText(l.getTitulo());
            txtAutor.setText(l.getAutor());
            txtCategoria.setText(l.getGenero());
            txtIsbn.setText(l.getIsbn());
            txtFoto.setText(l.getFoto());
            txtCantidad.setText(String.valueOf(l.getCantidad()));
            txtDescripcion.setText(l.getDescripcion());
            txtContenido.setText(l.getContenido());
        }
    }

    /**
     * Construye o actualiza un objeto {@link Libro} con los valores ingresados
     * en los campos del formulario.
     *
     * @return una instancia de {@link Libro} con los datos actualizados.
     */
    public Libro getLibroResultado() {
        if (libro == null) {
            libro = new Libro();
        }

        libro.setTitulo(txtTitulo.getText());
        libro.setAutor(txtAutor.getText());
        libro.setGenero(txtCategoria.getText());
        libro.setIsbn(txtIsbn.getText());
        libro.setFoto(txtFoto.getText());
        libro.setDescripcion(txtDescripcion.getText());
        libro.setContenido(txtContenido.getText());

        try {
            libro.setCantidad(Integer.parseInt(txtCantidad.getText()));
        } catch (Exception e) {
            libro.setCantidad(1);
        }

        libro.setCantidadDisponible(libro.getCantidad());
        libro.setDisponible(libro.getCantidad() > 0);
        return libro;
    }

    /**
     * Acción ejecutada al pulsar el botón Guardar.
     * <p>
     * Ejecuta el callback asociado y cierra la ventana.
     */
    @FXML
    private void guardar() {
        if (onGuardarCallback != null) {
            onGuardarCallback.run();
        }
        if (stage != null) stage.close();
    }

    /**
     * Cierra la ventana sin realizar cambios.
     */
    @FXML
    private void cancelar() {
        if (stage != null) stage.close();
    }

    /**
     * Acción asociada al botón de cerrar la ventana.
     * Equivale a cancelar.
     */
    @FXML
    private void cerrarVentana() {
        if (stage != null) stage.close();
    }

    /**
     * Establece la función que se ejecutará al hacer clic en Guardar.
     *
     * @param cb función a ejecutar.
     */
    public void setOnGuardarCallback(Runnable cb) {
        this.onGuardarCallback = cb;
    }
}
