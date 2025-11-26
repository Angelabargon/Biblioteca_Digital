package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.modelos.Prestamo;
import com.example.biblioteca_digital.modelos.Estado;
import com.example.biblioteca_digital.DAO.Admin.PrestamoAdminDAO;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Optional;

public class ControladorPrestamosAdmin {

    @FXML private TableView<Prestamo> tablaPrestamos;
    @FXML private TableColumn<Prestamo, Integer> colId;
    @FXML private TableColumn<Prestamo, Integer> colUsuario;
    @FXML private TableColumn<Prestamo, Integer> colLibro;
    @FXML private TableColumn<Prestamo, LocalDate> colInicio;
    @FXML private TableColumn<Prestamo, LocalDate> colFin;
    @FXML private TableColumn<Prestamo, String> colEstado;
    @FXML private TextField txtBuscar;

    private final PrestamoAdminDAO prestamoServicio = new PrestamoAdminDAO();
    private final ObservableList<Prestamo> lista = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (colId!=null) colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colUsuario!=null) colUsuario.setCellValueFactory(new PropertyValueFactory<>("id_usuario"));
        if (colLibro!=null) colLibro.setCellValueFactory(new PropertyValueFactory<>("id_libro"));
        if (colInicio!=null) colInicio.setCellValueFactory(new PropertyValueFactory<>("fecha_inicio"));
        if (colFin!=null) colFin.setCellValueFactory(new PropertyValueFactory<>("fecha_fin"));
        if (colEstado!=null) colEstado.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(cell.getValue().getEstado()));

        cargarPrestamos();
    }

    private void cargarPrestamos() {
        lista.setAll(prestamoServicio.obtenerTodos());
        if (tablaPrestamos!=null) tablaPrestamos.setItems(lista);
    }

    @FXML public void buscarPrestamo() {
        String q = txtBuscar!=null? txtBuscar.getText().trim().toLowerCase() : "";
        if (q.isEmpty()) { cargarPrestamos(); return; }
        ObservableList<Prestamo> filt = lista.filtered(p ->
                String.valueOf(p.getId_usuario()).contains(q) ||
                        String.valueOf(p.getId_libro()).contains(q)
        );
        tablaPrestamos.setItems(filt);
    }

    @FXML public void abrirAgregarPrestamo() { abrirEditor(null); }
    @FXML public void editarPrestamo() {
        Prestamo sel = tablaPrestamos.getSelectionModel().getSelectedItem();
        if (sel==null) { mostrarAlerta("Selecciona un préstamo"); return; }
        abrirEditor(sel);
    }
    @FXML public void eliminarPrestamo() {
        Prestamo sel = tablaPrestamos.getSelectionModel().getSelectedItem();
        if (sel==null) { mostrarAlerta("Selecciona un préstamo"); return; }
        Alert a=new Alert(Alert.AlertType.CONFIRMATION,"Eliminar préstamo?",ButtonType.OK,ButtonType.CANCEL);
        a.setHeaderText(null);
        Optional<javafx.scene.control.ButtonType> r = a.showAndWait();
        if (r.isPresent() && r.get()==ButtonType.OK) {
            boolean ok = prestamoServicio.eliminarPrestamo(sel.getId());
            if (!ok) mostrarAlerta("No se pudo eliminar.");
            cargarPrestamos();
        }
    }

    private void abrirEditor(Prestamo p) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/editarPrestamo.fxml"));
            Parent root = loader.load();
            ControladorEditarPrestamo ctrl = loader.getController();
            Stage st = new Stage();
            st.initOwner(tablaPrestamos.getScene().getWindow());
            st.initModality(Modality.APPLICATION_MODAL);
            ctrl.setStage(st);
            ctrl.setPrestamo(p);
            ctrl.setOnGuardarCallback(() -> {
                Prestamo res = ctrl.getPrestamoResultado();
                if (p==null) {
                    boolean ok = prestamoServicio.agregarPrestamo(res);
                    if (!ok) mostrarAlerta("No se pudo crear.");
                } else {
                    res.setId(p.getId());
                    boolean ok = prestamoServicio.actualizarPrestamo(res);
                    if (!ok) mostrarAlerta("No se pudo actualizar.");
                }
                cargarPrestamos();
            });
            st.setScene(new javafx.scene.Scene(root));
            st.showAndWait();
        } catch (IOException e) { e.printStackTrace(); mostrarAlerta("Error abrir editor"); }
    }

    private void mostrarAlerta(String t) { Alert a=new Alert(Alert.AlertType.WARNING); a.setHeaderText(null); a.setContentText(t); a.showAndWait(); }
}
