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

public class ControladorPrestamosAdmin {

    @FXML private TableView<Prestamo> tablaPrestamos;

    @FXML private TableColumn<Prestamo, String> colUsuario;
    @FXML private TableColumn<Prestamo, String> colLibro;
    @FXML private TableColumn<Prestamo, String> colFechaPrestamo;
    @FXML private TableColumn<Prestamo, String> colFechaVencimiento;
    @FXML private TableColumn<Prestamo, String> colEstado;
    @FXML private TableColumn<Prestamo, Void> colAcciones;

    @FXML private TextField txtBuscar;

    private final PrestamoAdminDAO prestamoAdminDAO = new PrestamoAdminDAO();
    private final ObservableList<Prestamo> listaPrestamos = FXCollections.observableArrayList();

    private final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        cargarColumnas();
        refrescarTabla();
        // buscar en tiempo real al escribir
        if (txtBuscar != null) {
            txtBuscar.textProperty().addListener((obs, oldVal, newVal) -> buscarPrestamo());
        }
    }

    // ============================================================
    // CONFIGURACIÓN DE COLUMNAS
    // ============================================================
    private void cargarColumnas() {

        // USUARIO
        colUsuario.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getUsuario().getNombreUsuario()));

        // LIBRO
        colLibro.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getLibro().getTitulo()));

        // FECHA PRESTAMO
        colFechaPrestamo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFecha_inicio().format(formato)));

        // FECHA VENCIMIENTO
        colFechaVencimiento.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFecha_fin().format(formato)));

        // ESTADO (badge igual estilo que Libros)

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

        // ACCIONES (solo eliminar)
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

    // ============================================================
    // ACCIONES
    // ============================================================
    public void refrescarTabla() {
        listaPrestamos.setAll(prestamoAdminDAO.obtenerTodos());
        tablaPrestamos.setItems(listaPrestamos);
    }

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

    @FXML
    private void nuevoPrestamo() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/admin/editarPrestamo.fxml"));
            Parent root = loader.load();
            com.example.biblioteca_digital.controladores.admin.ControladorEditarPrestamo ctrl = loader.getController();

            // cargar usuarios y libros disponibles
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

    private void editarPrestamo(Prestamo p) {
        System.out.println("Editar préstamo: " + p.getId());
        // abrir modal…
    }

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
