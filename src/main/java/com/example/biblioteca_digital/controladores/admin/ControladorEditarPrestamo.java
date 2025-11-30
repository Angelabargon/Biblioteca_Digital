package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.modelos.Estado;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import com.example.biblioteca_digital.modelos.Prestamo;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.util.List;

/**
 * Controlador de la ventana emergente utilizada por el administrador para crear
 * un préstamo dentro del sistema.
 *
 * <p>Permite seleccionar un usuario, un libro y una fecha de vencimiento.
 * También construye el objeto {@link Prestamo} resultante que será procesado
 * por el controlador principal.</p>
 */
public class ControladorEditarPrestamo {

    /** ComboBox que muestra la lista de usuarios disponibles. */
    @FXML private ComboBox<Usuario> comboUsuario;

    /** ComboBox que muestra la lista de libros disponibles. */
    @FXML private ComboBox<Libro> comboLibro;

    /** Selector de fecha para indicar el vencimiento del préstamo. */
    @FXML private DatePicker fechaFin;

    /** Instancia del préstamo que se está creando. */
    private Prestamo prestamo;

    /** Ventana actual donde se encuentra cargado este editor. */
    private Stage stage;

    /**
     * Función callback que se ejecutará cuando el usuario pulse el botón Guardar.
     * Se utiliza para comunicar los datos al controlador principal.
     */
    private Runnable onGuardarCallback;

    /** Lista de usuarios enviada desde el controlador principal. */
    private List<Usuario> usuarios;

    /** Lista de libros enviada desde el controlador principal. */
    private List<Libro> libros;

    /**
     * Inicializa el comportamiento visual de los ComboBox,
     * mostrando únicamente el nombre de usuario y el título del libro.
     * También asigna un valor por defecto para la fecha de finalización.
     */
    @FXML
    private void initialize() {

        // Mostrar solo el nombre del usuario en lista y botón
        comboUsuario.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Usuario u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? null : u.getNombreUsuario());
            }
        });
        comboUsuario.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Usuario u, boolean empty) {
                super.updateItem(u, empty);
                setText(empty || u == null ? null : u.getNombreUsuario());
            }
        });

        // Mostrar solo el título del libro
        comboLibro.setCellFactory(list -> new ListCell<>() {
            @Override
            protected void updateItem(Libro l, boolean empty) {
                super.updateItem(l, empty);
                setText(empty || l == null ? null : l.getTitulo());
            }
        });
        comboLibro.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Libro l, boolean empty) {
                super.updateItem(l, empty);
                setText(empty || l == null ? null : l.getTitulo());
            }
        });

        // Fecha mínima por defecto (2 semanas desde hoy)
        fechaFin.setValue(LocalDate.now().plusWeeks(2));
    }

    /**
     * Carga las listas de usuarios y libros que deben mostrarse en los ComboBox.
     * Este metodo es llamado desde el controlador principal antes de abrir el popup.
     *
     * @param usuarios lista completa de usuarios del sistema.
     * @param libros lista completa de libros disponibles.
     */
    public void cargarDatos(List<Usuario> usuarios, List<Libro> libros) {
        this.usuarios = usuarios;
        this.libros = libros;

        comboUsuario.getItems().setAll(usuarios);
        comboLibro.getItems().setAll(libros);
    }

    /**
     * Establece la ventana actual de este editor.
     *
     * @param s instancia del {@link Stage}.
     */
    public void setStage(Stage s) {
        this.stage = s;
    }

    /**
     * Construye y devuelve un objeto {@link Prestamo} con los valores seleccionados
     * en la interfaz.
     *
     * <p>Si no existía un préstamo previo, se crea uno nuevo. De lo contrario, se
     * actualiza el préstamo existente.</p>
     *
     * @return el préstamo generado o actualizado listo para ser guardado.
     */
    public Prestamo getPrestamoResultado() {
        if (prestamo == null)
            prestamo = new Prestamo();

        Usuario usuarioSeleccionado = comboUsuario.getValue();
        Libro libroSeleccionado = comboLibro.getValue();

        if (usuarioSeleccionado != null)
            prestamo.setUsuario(usuarioSeleccionado);

        if (libroSeleccionado != null)
            prestamo.setLibro(libroSeleccionado);

        prestamo.setFecha_inicio(LocalDate.now());
        prestamo.setFecha_fin(fechaFin.getValue());
        prestamo.setEstado(String.valueOf(Estado.activo));

        return prestamo;
    }

    /**
     * Acción ejecutada al pulsar el botón Guardar.
     * Ejecuta la función callback asociada y cierra la ventana.
     */
    @FXML
    private void guardar() {
        if (onGuardarCallback != null)
            onGuardarCallback.run();

        if (stage != null)
            stage.close();
    }

    /**
     * Cierra la ventana sin realizar ningún cambio.
     */
    @FXML
    private void cancelar() {
        if (stage != null)
            stage.close();
    }

    /**
     * Establece la función callback que será ejecutada cuando el usuario guarde los cambios.
     *
     * @param cb función a ejecutar.
     */
    public void setOnGuardarCallback(Runnable cb) {
        this.onGuardarCallback = cb;
    }
}