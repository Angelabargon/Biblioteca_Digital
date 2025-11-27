package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.DAO.admin.PrestamoAdminDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.example.biblioteca_digital.modelos.Prestamo;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ControladorPrestamosAdmin {

    @FXML
    private TableView<Prestamo> tablaPrestamos;

    @FXML
    private TableColumn<Prestamo, String> colUsuario;

    @FXML
    private TableColumn<Prestamo, String> colLibro;

    @FXML
    private TableColumn<Prestamo, String> colFechaPrestamo;

    @FXML
    private TableColumn<Prestamo, String> colFechaVencimiento;

    @FXML
    private TableColumn<Prestamo, String> colEstado;

    @FXML
    private TableColumn<Prestamo, Void> colAcciones;

    @FXML
    private TextField txtBuscar;

    @FXML
    private Button btnAgregarPrestamo;

    @FXML
    private ImageView iconAdd;

    private final PrestamoAdminDAO prestamoAdminDAO = new PrestamoAdminDAO();
    private ObservableList<Prestamo> listaPrestamos = FXCollections.observableArrayList();

    private final DateTimeFormatter formato = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    @FXML
    public void initialize() {
        // Ícono del botón agregar
        iconAdd.setImage(new Image(getClass().getResourceAsStream("/icons/add.png")));

        cargarColumnas();
        refrescarTabla();
    }

    private void cargarColumnas() {

        colUsuario.setCellValueFactory(data -> data.getValue().nombreUsuarioProperty());
        colLibro.setCellValueFactory(data -> data.getValue().tituloLibroProperty());

        colFechaPrestamo.setCellValueFactory(data ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        data.getValue().getFechaPrestamo().format(formato)));

        colFechaVencimiento.setCellValueFactory(data ->
                javafx.beans.binding.Bindings.createStringBinding(() ->
                        data.getValue().getFechaVencimiento().format(formato)));

        // Estado con badge visual
        colEstado.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String estado, boolean empty) {
                super.updateItem(estado, empty);

                if (empty) {
                    setText(null);
                    setStyle("");
                    return;
                }

                Prestamo p = getTableView().getItems().get(getIndex());

                boolean vencido = p.getFechaVencimiento().isBefore(LocalDate.now());
                setText(vencido ? "Vencido" : "Vigente");

                setStyle("-fx-font-weight: bold; -fx-text-fill: white; -fx-alignment: center;"
                        + (vencido
                        ? "-fx-background-color: #E74C3C; -fx-background-radius: 12;"
                        : "-fx-background-color: #2ECC71; -fx-background-radius: 12;"));
            }
        });

        // Acciones (editar / eliminar)
        colAcciones.setCellFactory(col -> new TableCell<>() {

            private final ImageView iconEdit = new ImageView(new Image(getClass().getResourceAsStream("/icons/edit.png")));
            private final ImageView iconDelete = new ImageView(new Image(getClass().getResourceAsStream("/icons/delete.png")));

            private final Button btnEdit = new Button("", iconEdit);
            private final Button btnDelete = new Button("", iconDelete);

            {
                iconEdit.setFitWidth(18);
                iconEdit.setFitHeight(18);
                iconDelete.setFitWidth(18);
                iconDelete.setFitHeight(18);

                btnEdit.setStyle("-fx-background-color: transparent;");
                btnDelete.setStyle("-fx-background-color: transparent;");

                btnEdit.setOnAction(e -> editarPrestamo(getTableView().getItems().get(getIndex())));
                btnDelete.setOnAction(e -> eliminarPrestamo(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                    return;
                }

                HBox box = new HBox(10, btnEdit, btnDelete);
                setGraphic(box);
            }
        });
    }

    public void refrescarTabla() {
        listaPrestamos.setAll(prestamoAdminDAO.obtenerTodos());
        tablaPrestamos.setItems(listaPrestamos);
    }

    @FXML
    private void buscarPrestamo() {
        String texto = txtBuscar.getText().toLowerCase();

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
        System.out.println("Editar préstamo ID: " + p.getId());
        // abrir modal…
    }

    private void eliminarPrestamo(Prestamo p) {
        System.out.println("Eliminar préstamo ID: " + p.getId());
        // confirmar…
    }
}
