package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Prestamo;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.util.List;

/**
 * Controlador encargado de gestionar la ventana de creación y edición
 * de préstamos dentro del panel de administración.
 *
 * <p>
 * Permite seleccionar usuario y libro (solo en creación), modificar
 * la fecha de vencimiento y el estado del préstamo, y devolver el
 * resultado al controlador que abrió la ventana mediante un callback.
 * </p>
 */
public class ControladorEditarPrestamo {

    /** Etiqueta que muestra el título de la ventana (crear o editar). */
    @FXML
    public Label lblTitulo;

    /** Botón principal para guardar los cambios del préstamo. */
    @FXML
    public Button btnGuardar;

    /** Desplegable para seleccionar el usuario del préstamo. */
    @FXML
    public ComboBox<Usuario> comboUsuario;

    /** Desplegable para seleccionar el libro del préstamo. */
    @FXML
    public ComboBox<Libro> comboLibro;

    /** Selector de fecha para la fecha de vencimiento del préstamo. */
    @FXML
    public DatePicker fechaFin;

    /** Desplegable para seleccionar el estado del préstamo. */
    @FXML
    public ComboBox<String> comboEstado;

    /** Préstamo que se está creando o editando. */
    private Prestamo prestamoEditar;

    /** Ventana actual donde se muestra el editor de préstamos. */
    private Stage stage;

    /**
     * Callback que se ejecuta al guardar el préstamo.
     * Permite comunicar los cambios al controlador que abrió esta vista.
     */
    private Runnable onGuardarCallback;

    /**
     * Inicializa la vista configurando los desplegables
     * y los conversores de texto para mostrar solo la
     * información relevante de usuarios y libros.
     */
    @FXML
    public void initialize() {

        // Estados permitidos (se excluye "devuelto")
        comboEstado.getItems().addAll("activo", "bloqueado");

        // Mostrar únicamente el nombre de usuario
        comboUsuario.setConverter(new StringConverter<>() {
            @Override
            public String toString(Usuario u) {
                return u == null ? "" : u.getNombreUsuario();
            }

            @Override
            public Usuario fromString(String s) {
                return null;
            }
        });

        // Mostrar únicamente el título del libro
        comboLibro.setConverter(new StringConverter<>() {
            @Override
            public String toString(Libro l) {
                return l == null ? "" : l.getTitulo();
            }

            @Override
            public Libro fromString(String s) {
                return null;
            }
        });
    }

    /**
     * Carga los listados de usuarios y libros disponibles
     * en los desplegables correspondientes.
     *
     * @param usuarios lista de usuarios del sistema
     * @param libros lista de libros del sistema
     */
    public void cargarDatos(List<Usuario> usuarios, List<Libro> libros) {
        comboUsuario.getItems().setAll(usuarios);
        comboLibro.getItems().setAll(libros);
    }

    /**
     * Prepara la vista para la creación de un nuevo préstamo,
     * inicializando los textos y creando una nueva instancia
     * de {@link Prestamo}.
     */
    public void prepararNuevoPrestamo() {
        lblTitulo.setText("Nuevo Préstamo");
        btnGuardar.setText("Crear Préstamo");
        prestamoEditar = new Prestamo();
    }

    /**
     * Configura la vista en modo edición cargando los datos
     * del préstamo existente y bloqueando la modificación
     * de usuario y libro.
     *
     * @param p préstamo que se va a editar
     */
    public void setPrestamoEditar(Prestamo p) {
        this.prestamoEditar = p;

        lblTitulo.setText("Editar Préstamo");
        btnGuardar.setText("Guardar Cambios");

        comboUsuario.setValue(p.getUsuario());
        comboLibro.setValue(p.getLibro());
        fechaFin.setValue(p.getFecha_fin());

        // Si el préstamo estaba marcado como devuelto,
        // se fuerza a estado activo para permitir edición
        if ("devuelto".equals(p.getEstado())) {
            comboEstado.setValue("activo");
        } else {
            comboEstado.setValue(p.getEstado());
        }

        comboUsuario.setDisable(true);
        comboLibro.setDisable(true);
    }

    /**
     * Guarda los cambios realizados en el préstamo.
     * <p>
     * Valida los campos obligatorios, actualiza el objeto
     * {@link Prestamo} y ejecuta el callback asociado.
     * </p>
     */
    @FXML
    void guardar() {

        if (fechaFin.getValue() == null || comboEstado.getValue() == null) {
            new Alert(Alert.AlertType.ERROR, "Todos los campos son obligatorios").show();
            return;
        }

        prestamoEditar.setFecha_fin(fechaFin.getValue());
        prestamoEditar.setEstado(comboEstado.getValue());

        if (onGuardarCallback != null) {
            onGuardarCallback.run();
        }

        stage.close();
    }

    /**
     * Cancela la operación y cierra la ventana
     * sin guardar cambios.
     */
    @FXML
    private void cancelar() {
        stage.close();
    }

    /**
     * Establece la ventana actual del editor.
     *
     * @param stage ventana donde se muestra esta vista
     */
    public void setStage(Stage stage) {
        this.stage = stage;
    }

    /**
     * Establece la función que se ejecutará al guardar
     * el préstamo.
     *
     * @param cb callback de guardado
     */
    public void setOnGuardarCallback(Runnable cb) {
        this.onGuardarCallback = cb;
    }

    /**
     * Devuelve el préstamo resultante tras la edición
     * o creación.
     *
     * @return préstamo actualizado
     */
    public Prestamo getPrestamoResultado() {
        return prestamoEditar;
    }
}