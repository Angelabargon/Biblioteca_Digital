package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.DAO.admin.PrestamoAdminDAO;
import com.example.biblioteca_digital.modelos.Prestamo;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;

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
    }

    // ============================================================
    // CONFIGURACIÓN DE COLUMNAS
    // ============================================================
    private void cargarColumnas() {

        // USUARIO
        colUsuario.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getNombreUsuario()));

        // LIBRO
        colLibro.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getTituloLibro()));

        // FECHA PRESTAMO
        colFechaPrestamo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFechaPrestamo().format(formato)));

        // FECHA VENCIMIENTO
        colFechaVencimiento.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getFechaVencimiento().format(formato)));

        // ESTADO (badge igual estilo que Libros)
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
                boolean vencido = p.getFechaVencimiento().isBefore(LocalDate.now());

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

        // ACCIONES (✎ editar / 🗑 eliminar)
        colAcciones.setCellFactory(col -> new TableCell<>() {

            private final Button btnEditar = new Button("✎");
            private final Button btnEliminar = new Button("🗑");
            private final HBox contenedor = new HBox(8);

            {
                // Estilo EDITAR (como en Libros)
                btnEditar.setStyle(
                        "-fx-background-color:#fff6ee; " +
                                "-fx-border-radius:8; -fx-background-radius:8; -fx-padding:6;"
                );

                btnEditar.setOnAction(e -> {
                    Prestamo p = getTableView().getItems().get(getIndex());
                    editarPrestamo(p);
                });

                // Estilo ELIMINAR (como en Libros)
                btnEliminar.setStyle(
                        "-fx-background-color:#ef4444; -fx-text-fill:white; " +
                                "-fx-border-radius:8; -fx-background-radius:8; -fx-padding:6;"
                );

                btnEliminar.setOnAction(e -> {
                    Prestamo p = getTableView().getItems().get(getIndex());
                    eliminarPrestamo(p);
                });

                contenedor.setPadding(new Insets(4, 0, 4, 0));
                contenedor.getChildren().addAll(btnEditar, btnEliminar);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(contenedor);
                }
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
                p.getNombreUsuario().toLowerCase().contains(texto) ||
                        p.getTituloLibro().toLowerCase().contains(texto)
        );

        tablaPrestamos.setItems(filtrado);
    }

    @FXML
    private void nuevoPrestamo() {
        System.out.println("Nuevo préstamo");
        // abrir modal…
    }

    private void editarPrestamo(Prestamo p) {
        System.out.println("Editar préstamo: " + p.getId());
        // abrir modal…
    }

    private void eliminarPrestamo(Prestamo p) {
        System.out.println("Eliminar préstamo: " + p.getId());
        // confirmar…
    }
}
