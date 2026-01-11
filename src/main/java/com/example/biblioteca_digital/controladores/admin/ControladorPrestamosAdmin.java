package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.DAO.admin.LibroAdminDAO;
import com.example.biblioteca_digital.DAO.admin.PrestamoAdminDAO;
import com.example.biblioteca_digital.DAO.admin.UsuarioAdminDAO;
import com.example.biblioteca_digital.modelos.Prestamo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Controlador encargado de gestionar los préstamos dentro del panel
 * de administración de la biblioteca.
 *
 * <p>
 * Permite listar, buscar, crear, editar y eliminar préstamos,
 * mostrando la información en una tabla con estados visuales
 * personalizados.
 * </p>
 *
 * <p>
 * Utiliza {@link PrestamoAdminDAO} para el acceso a datos
 * y controla la apertura de ventanas modales para la creación
 * y edición de préstamos.
 * </p>
 */
public class ControladorPrestamosAdmin {

    /** Tabla principal que contiene los préstamos. */
    @FXML private TableView<Prestamo> tablaPrestamos;

    /** Columna con el nombre del usuario del préstamo. */
    @FXML private TableColumn<Prestamo, String> colUsuario;

    /** Columna con el título del libro prestado. */
    @FXML private TableColumn<Prestamo, String> colLibro;

    /** Columna con la fecha de inicio del préstamo. */
    @FXML private TableColumn<Prestamo, String> colFechaPrestamo;

    /** Columna con la fecha de vencimiento del préstamo. */
    @FXML private TableColumn<Prestamo, String> colFechaVencimiento;

    /** Columna que representa el estado del préstamo. */
    @FXML private TableColumn<Prestamo, String> colEstado;

    /** Columna que contiene los botones de acciones. */
    @FXML private TableColumn<Prestamo, Void> colAcciones;

    /** Campo de texto para buscar préstamos por usuario o libro. */
    @FXML private TextField txtBuscar;

    /** DAO para la gestión de préstamos. */
    private final PrestamoAdminDAO prestamoAdminDAO = new PrestamoAdminDAO();

    /** Lista observable utilizada como fuente de datos de la tabla. */
    private final ObservableList<Prestamo> listaPrestamos =
            FXCollections.observableArrayList();

    /** Formato de fecha utilizado en la tabla. */
    private final DateTimeFormatter formato =
            DateTimeFormatter.ofPattern("dd/MM/yyyy");

    /**
     * Inicializa el controlador.
     * <p>
     * Configura las columnas de la tabla, carga los préstamos
     * desde la base de datos y activa la búsqueda en tiempo real.
     * </p>
     */
    @FXML
    public void initialize() {
        cargarColumnas();
        refrescarTabla();

        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener(
                    (obs, oldVal, newVal) -> buscarPrestamo()
            );
        }
    }

    /**
     * Configura todas las columnas de la tabla,
     * incluyendo celdas personalizadas para estados
     * y botones de acción.
     */
    private void cargarColumnas() {

        // Usuario
        colUsuario.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getUsuario().getNombreUsuario()
                )
        );

        // Libro
        colLibro.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getLibro().getTitulo()
                )
        );

        // Fecha inicio
        colFechaPrestamo.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getFecha_inicio().format(formato)
                )
        );

        // Fecha fin
        colFechaVencimiento.setCellValueFactory(data ->
                new SimpleStringProperty(
                        data.getValue().getFecha_fin().format(formato)
                )
        );

        // Estado
        colEstado.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getEstado())
        );

        colEstado.setCellFactory(col -> new TableCell<>() {

            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);

                if (empty || getIndex() < 0) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Prestamo p = getTableView().getItems().get(getIndex());
                Label badge = new Label();

                switch (p.getEstado()) {
                    case "bloqueado" -> {
                        badge.setText("Bloqueado");
                        badge.setStyle(
                                "-fx-background-color:#7c2d12;" +
                                        "-fx-text-fill:white;" +
                                        "-fx-padding:4 10;" +
                                        "-fx-background-radius:8;"
                        );
                    }
                    case "devuelto" -> {
                        badge.setText("Devuelto");
                        badge.setStyle(
                                "-fx-background-color:#6b7280;" +
                                        "-fx-text-fill:white;" +
                                        "-fx-padding:4 10;" +
                                        "-fx-background-radius:8;"
                        );
                    }
                    default -> {
                        boolean vencido =
                                p.getFecha_fin().isBefore(LocalDate.now());

                        badge.setText(vencido ? "Vencido" : "Vigente");
                        badge.setStyle(vencido
                                ? "-fx-background-color:#ef4444;"
                                + "-fx-text-fill:white;"
                                + "-fx-padding:4 10;"
                                + "-fx-background-radius:8;"
                                : "-fx-background-color:#10B981;"
                                + "-fx-text-fill:white;"
                                + "-fx-padding:4 10;"
                                + "-fx-background-radius:8;"
                        );
                    }
                }

                setGraphic(badge);
                setText(null);
            }
        });

        // Acciones
        colAcciones.setCellFactory(col -> new TableCell<>() {

            private final Button btnEditar = new Button("✎");
            private final Button btnEliminar = new Button("🗑");
            private final HBox contenedor =
                    new HBox(8, btnEditar, btnEliminar);

            {
                btnEditar.setStyle(
                        "-fx-background-color:#fff6ee;" +
                                "-fx-text-fill:#3B3027;" +
                                "-fx-background-radius:6;" +
                                "-fx-padding:6 8;"
                );

                btnEliminar.setStyle(
                        "-fx-background-color:#ef4444;" +
                                "-fx-text-fill:white;" +
                                "-fx-background-radius:6;" +
                                "-fx-padding:6 8;"
                );

                btnEditar.setOnAction(e ->
                        editarPrestamo(
                                getTableView().getItems().get(getIndex())
                        )
                );

                btnEliminar.setOnAction(e ->
                        eliminarPrestamo(
                                getTableView().getItems().get(getIndex())
                        )
                );

                contenedor.setStyle("-fx-alignment:center;");
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : contenedor);
            }
        });
    }

    /**
     * Recarga los préstamos desde la base de datos
     * y actualiza la tabla.
     */
    public void refrescarTabla() {
        listaPrestamos.setAll(prestamoAdminDAO.obtenerTodos());
        tablaPrestamos.setItems(listaPrestamos);
    }

    /**
     * Filtra los préstamos según el texto introducido,
     * buscando por nombre de usuario o título del libro.
     */
    @FXML
    private void buscarPrestamo() {
        String texto = txtBuscar.getText().toLowerCase().trim();

        if (texto.isEmpty()) {
            tablaPrestamos.setItems(listaPrestamos);
            return;
        }

        ObservableList<Prestamo> filtrado =
                listaPrestamos.filtered(p ->
                        p.getUsuario().getNombreUsuario()
                                .toLowerCase().contains(texto)
                                || p.getLibro().getTitulo()
                                .toLowerCase().contains(texto)
                );

        tablaPrestamos.setItems(filtrado);
    }

    /**
     * Abre la ventana para crear un nuevo préstamo.
     */
    @FXML
    private void nuevoPrestamo() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/example/biblioteca_digital/vistas/admin/editarPrestamo.fxml"
                    )
            );
            Parent root = loader.load();
            ControladorEditarPrestamo ctrl = loader.getController();

            ctrl.cargarDatos(
                    new UsuarioAdminDAO().obtenerTodos(),
                    new LibroAdminDAO().obtenerTodos()
            );

            Stage st = new Stage();
            st.initOwner(tablaPrestamos.getScene().getWindow());
            st.initModality(Modality.APPLICATION_MODAL);

            ctrl.setStage(st);
            ctrl.setOnGuardarCallback(() -> {
                prestamoAdminDAO.crearPrestamo(
                        ctrl.getPrestamoResultado()
                );
                refrescarTabla();
            });

            st.setScene(new Scene(root));
            st.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Abre la ventana de edición para un préstamo existente.
     *
     * @param p préstamo a editar
     */
    private void editarPrestamo(Prestamo p) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource(
                            "/com/example/biblioteca_digital/vistas/admin/editarPrestamo.fxml"
                    )
            );
            Parent root = loader.load();
            ControladorEditarPrestamo ctrl = loader.getController();

            ctrl.cargarDatos(
                    new UsuarioAdminDAO().obtenerTodos(),
                    new LibroAdminDAO().obtenerTodos()
            );
            ctrl.setPrestamoEditar(p);

            Stage st = new Stage();
            st.initOwner(tablaPrestamos.getScene().getWindow());
            st.initModality(Modality.APPLICATION_MODAL);

            ctrl.setStage(st);
            ctrl.setOnGuardarCallback(() -> {
                prestamoAdminDAO.actualizarPrestamo(
                        ctrl.getPrestamoResultado()
                );
                refrescarTabla();
            });

            st.setScene(new Scene(root));
            st.showAndWait();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Elimina un préstamo tras confirmación,
     * devolviendo automáticamente el libro.
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
                prestamoAdminDAO.eliminarPrestamo(p.getId());
                refrescarTabla();
            }
        });
    }
}