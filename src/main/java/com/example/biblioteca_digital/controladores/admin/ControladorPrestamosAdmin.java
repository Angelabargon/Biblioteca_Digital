package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.DAO.admin.PrestamoAdminDAO;
import com.example.biblioteca_digital.modelos.Prestamo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Controlador encargado de gestionar los préstamos dentro del panel de administración.
 * Permite listar, filtrar, crear y eliminar préstamos asociados a usuarios y libros.
 *
 * Se apoya en {@link PrestamoAdminDAO} para el acceso a datos y utiliza una tabla con
 * celdas personalizadas para mejorar la visualización del estado del préstamo.
 */
public class ControladorPrestamosAdmin {

    /** Tabla principal que muestra los préstamos registrados. */
    @FXML private TableView<Prestamo> tablaPrestamos;

    /** Columna que muestra el nombre del usuario asociado al préstamo. */
    @FXML private TableColumn<Prestamo, String> colUsuario;

    /** Columna que muestra el título del libro prestado. */
    @FXML private TableColumn<Prestamo, String> colLibro;

    /** Columna que muestra la fecha de inicio del préstamo. */
    @FXML private TableColumn<Prestamo, String> colFechaPrestamo;

    /** Columna que muestra la fecha de vencimiento del préstamo. */
    @FXML private TableColumn<Prestamo, String> colFechaVencimiento;

    /** Columna que muestra el estado del préstamo. */
    @FXML private TableColumn<Prestamo, String> colEstado;

    /** Columna que contiene acciones como eliminar el préstamo. */
    @FXML private TableColumn<Prestamo, Void> colAcciones;

    /** Campo de búsqueda para filtrar préstamos por usuario o libro. */
    @FXML private TextField txtBuscar;

    /** Objeto DAO que gestiona el acceso a datos de préstamos. */
    private final PrestamoAdminDAO prestamoAdminDAO = new PrestamoAdminDAO();

    /** Lista observable utilizada para poblar la tabla. */
    private final ObservableList<Prestamo> listaPrestamos = FXCollections.observableArrayList();

    /** Formato de fecha utilizado en la visualización. */
    private final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Inicializa el controlador configurando las columnas, cargando los datos
     * y activando la búsqueda en tiempo real.
     */
    @FXML
    public void initialize() {
        cargarColumnas();
        refrescarTabla();
        // buscar en tiempo real al escribir
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> buscarPrestamo());
        }
    }

    /**
     * Configura las columnas de la tabla, incluyendo celdas personalizadas
     * para estado y acciones (eliminar).
     */
    private void cargarColumnas() {

        // Usuario
        colUsuario.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUsuario().getNombreUsuario()));

        // Libro
        colLibro.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getLibro().getTitulo()));

        // Fecha Prestamo
        colFechaPrestamo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFecha_inicio().format(formato)));

        // Fecha Vencimiento
        colFechaVencimiento.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFecha_fin().format(formato)));

        // Estado
        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEstado()));

        colEstado.setCellFactory(col -> new TableCell<>() {

            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);

                if (empty || getTableRow() == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Prestamo p = getTableView().getItems().get(getIndex());
                boolean vencido = p.getFecha_fin().isBefore(LocalDate.now());

                Label badge = new Label(vencido ? "Vencido" : "Vigente");
                badge.setStyle(
                        vencido
                                ? "-fx-background-color:#ef4444; -fx-text-fill:white; -fx-padding:4 10; -fx-background-radius:8;"
                                : "-fx-background-color:#10B981; -fx-text-fill:white; -fx-padding:4 10; -fx-background-radius:8;"
                );

                setGraphic(badge);
                setText(null);
            }
        });

        // Celda personalizada para botones de acciones
        colAcciones.setCellFactory(col -> new TableCell<>() {

            private final Button btnEliminar = new Button("🗑");
            private final HBox contenedor = new HBox(8);

            {
                btnEliminar.setStyle(
                        "-fx-background-color:#ef4444; -fx-text-fill:white; " +
                                "-fx-border-radius:8; -fx-background-radius:8; -fx-padding:6;"
                );

                btnEliminar.setOnAction(e -> {
                    Prestamo p = getTableView().getItems().get(getIndex());
                    eliminarPrestamo(p);
                });

                contenedor.setPadding(new Insets(4, 0, 4, 0));
                contenedor.getChildren().add(btnEliminar);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : contenedor);
            }
        });
    }

    /**
     * Recarga la tabla con la información más reciente desde la base de datos.
     */
    public void refrescarTabla() {
        listaPrestamos.setAll(prestamoAdminDAO.obtenerTodos());
        tablaPrestamos.setItems(listaPrestamos);
    }

    /**
     * Filtra los préstamos según el texto ingresado en el buscador.
     * Se puede buscar por nombre de usuario o nombre de libro.
     */
    @FXML
    private void buscarPrestamo() {
        String texto = txtBuscar.getText().toLowerCase().trim();

        if (texto.isEmpty()) {
            tablaPrestamos.setItems(listaPrestamos);
            return;
        }

        ObservableList<Prestamo> filtrado = listaPrestamos.filtered(p ->
                p.getUsuario().getNombreUsuario().toLowerCase().contains(texto) ||
                        p.getLibro().getTitulo().toLowerCase().contains(texto)
        );

        tablaPrestamos.setItems(filtrado);
    }

    /**
     * Abre la ventana para crear un nuevo préstamo y guarda el resultado si es válido.
     */
    @FXML
    private void nuevoPrestamo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/admin/editarPrestamo.fxml"));
            Parent root = loader.load();
            com.example.biblioteca_digital.controladores.admin.ControladorEditarPrestamo ctrl = loader.getController();

            // cargar usuarios y libros existentes
            ctrl.cargarDatos(new com.example.biblioteca_digital.DAO.admin.UsuarioAdminDAO().obtenerTodos(),
                    new com.example.biblioteca_digital.DAO.admin.LibroAdminDAO().obtenerTodos());

            Stage st = new Stage();
            st.initOwner(tablaPrestamos.getScene().getWindow());
            st.initModality(Modality.APPLICATION_MODAL);
            ctrl.setStage(st);
            ctrl.setOnGuardarCallback(() -> {
                com.example.biblioteca_digital.modelos.Prestamo p = ctrl.getPrestamoResultado();
                boolean ok = prestamoAdminDAO.crearPrestamo(p);
                if (!ok) {
                    Alert a = new Alert(Alert.AlertType.ERROR, "No se pudo crear el préstamo");
                    a.showAndWait();
                }
                refrescarTabla();
            });

            st.setScene(new javafx.scene.Scene(root));
            st.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Metodo reservado para una futura implementación de edición de préstamos.
     *
     * @param p préstamo a editar
     */
    private void editarPrestamo(Prestamo p) {
        System.out.println("Editar préstamo: " + p.getId());
    }

    /**
     * Elimina el préstamo indicado tras una confirmación,
     * devolviendo automáticamente el libro asociado.
     *
     * @param p préstamo a eliminar
     */
    private void eliminarPrestamo(Prestamo p) {
        if (p == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setHeaderText("¿Eliminar préstamo?");
        alert.setContentText("Esto devolverá el libro automáticamente.");
        alert.showAndWait().ifPresent(btn -> {
            if (btn == ButtonType.OK) {
                boolean ok = prestamoAdminDAO.eliminarPrestamo(p.getId());

                if (!ok) {
                    new Alert(Alert.AlertType.ERROR,
                            "No se pudo eliminar el préstamo").showAndWait();
                }

                refrescarTabla();
            }
        });
    }
}
