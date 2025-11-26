package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.DAO.Admin.UsuarioAdminDAO;
import com.example.biblioteca_digital.modelos.Usuario;
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
import java.util.Optional;

public class ControladorUsuarioAdmin {

    @FXML
    private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId;
    @FXML private TableColumn<Usuario, String> colUsuario;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TextField txtBuscar;

    private final UsuarioAdminDAO usuarioServicio = new UsuarioAdminDAO();
    private final ObservableList<Usuario> lista = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        if (colId!=null) colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        if (colUsuario!=null) colUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        if (colCorreo!=null) colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        if (colRol!=null) colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));

        cargarUsuarios();
    }

    private void cargarUsuarios() {
        lista.setAll(usuarioServicio.obtenerTodos());
        if (tablaUsuarios!=null) tablaUsuarios.setItems(lista);
    }

    @FXML public void buscarUsuario() {
        String q = txtBuscar!=null? txtBuscar.getText().trim().toLowerCase() : "";
        if (q.isEmpty()) { cargarUsuarios(); return; }
        ObservableList<Usuario> filt = lista.filtered(u ->
                (u.getNombreUsuario()!=null && u.getNombreUsuario().toLowerCase().contains(q)) ||
                        (u.getNombre()!=null && u.getNombre().toLowerCase().contains(q)) ||
                        (u.getCorreo()!=null && u.getCorreo().toLowerCase().contains(q))
        );
        tablaUsuarios.setItems(filt);
    }

    @FXML public void abrirAgregarUsuario() { abrirEditor(null); }
    @FXML public void editarUsuario() {
        Usuario sel = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (sel==null) { mostrarAlerta("Selecciona un usuario"); return; }
        abrirEditor(sel);
    }
    @FXML public void eliminarUsuario() {
        Usuario sel = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (sel==null) { mostrarAlerta("Selecciona un usuario"); return; }
        Alert a=new Alert(Alert.AlertType.CONFIRMATION,"Eliminar usuario?", ButtonType.OK,ButtonType.CANCEL);
        a.setHeaderText(null);
        Optional<ButtonType> r=a.showAndWait();
        if (r.isPresent() && r.get()==ButtonType.OK) {
            boolean ok = usuarioServicio.eliminarUsuario(sel.getId());
            if (!ok) mostrarAlerta("No se pudo eliminar.");
            cargarUsuarios();
        }
    }

    private void abrirEditor(Usuario u) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/editarUsuario.fxml"));
            Parent root = loader.load();
            ControladorEditarUsuario ctrl = loader.getController();
            Stage st = new Stage();
            st.initOwner(tablaUsuarios.getScene().getWindow());
            st.initModality(Modality.APPLICATION_MODAL);
            ctrl.setStage(st);
            if (u != null) ctrl.setUsuario(u);
            ctrl.setOnGuardarCallback(() -> {
                Usuario res = ctrl.getUsuarioResultado();
                if (u == null) {
                    boolean ok = usuarioServicio.agregarUsuario(res);
                    if (!ok) mostrarAlerta("No se pudo crear.");
                } else {
                    res.setId(u.getId());
                    boolean ok = usuarioServicio.actualizarUsuario(res);
                    if (!ok) mostrarAlerta("No se pudo actualizar.");
                }
                cargarUsuarios();
            });
            st.setScene(new javafx.scene.Scene(root));
            st.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error abrir editor usuario");
        }
    }

    private void mostrarAlerta(String t) { Alert a=new Alert(Alert.AlertType.WARNING); a.setHeaderText(null); a.setContentText(t); a.showAndWait(); }

}
