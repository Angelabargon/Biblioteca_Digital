package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.DAO.admin.UsuarioAdminDAO;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;

import java.io.IOException;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Optional;

/**
 * Controlador actualizado para adminUsuarios.fxml
 * - Cell factories programáticos: badge rol, formateo fecha, acciones (editar/eliminar).
 * - Respeta tus métodos y DAO.
 */
public class ControladorUsuarioAdmin {

    @FXML private TableView<Usuario> tablaUsuarios;
    @FXML private TableColumn<Usuario, Integer> colId; // si lo usas en el futuro
    @FXML private TableColumn<Usuario, String> colUsuario;
    @FXML private TableColumn<Usuario, String> colCorreo;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, Object> colFecha; // object porque puede ser Date/LocalDate/String
    @FXML private TableColumn<Usuario, Void> colAcciones;

    @FXML private TextField txtBuscar;

    private final UsuarioAdminDAO usuarioServicio = new UsuarioAdminDAO();
    private final ObservableList<Usuario> lista = FXCollections.observableArrayList();

    private final DateTimeFormatter fechaFmt = DateTimeFormatter.ofPattern("d/M/yyyy");

    @FXML
    public void initialize() {
        // Value factories
        if (colUsuario != null) colUsuario.setCellValueFactory(new PropertyValueFactory<>("nombreUsuario"));
        if (colCorreo != null) colCorreo.setCellValueFactory(new PropertyValueFactory<>("correo"));
        if (colRol != null) colRol.setCellValueFactory(new PropertyValueFactory<>("rol"));
        if (colFecha != null) colFecha.setCellValueFactory(new PropertyValueFactory<>("fechaRegistro"));

        // Cell factories visuales
        if (colRol != null) configurarRolCellFactory();
        if (colFecha != null) configurarFechaCellFactory();
        if (colAcciones != null) configurarAccionesCellFactory();

        cargarUsuarios();
    }

    private void configurarRolCellFactory() {
        colRol.setCellFactory(col -> new TableCell<Usuario, String>() {
            @Override
            protected void updateItem(String rol, boolean empty) {
                super.updateItem(rol, empty);
                if (empty || rol == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label(rol.equalsIgnoreCase("admin") || rol.equalsIgnoreCase("administrador") ? "Administrador" : "Usuario");
                    // Estilo similar a Figma
                    if (rol.equalsIgnoreCase("admin") || rol.equalsIgnoreCase("administrador") || rol.equalsIgnoreCase("admin_principal")) {
                        badge.setStyle("-fx-background-color:#8b4b2e; -fx-text-fill:white; -fx-padding:6 12; -fx-background-radius:8; -fx-font-weight:bold;");
                    } else {
                        badge.setStyle("-fx-background-color:#d6b48a; -fx-text-fill:#3B3027; -fx-padding:6 12; -fx-background-radius:8; -fx-font-weight:600;");
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });
    }

    private void configurarFechaCellFactory() {
        colFecha.setCellFactory(col -> new TableCell<Usuario, Object>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    String s = formatDateObject(item);
                    setText(s);
                }
            }
        });
    }

    // intenta formatear distintos tipos de fecha
    private String formatDateObject(Object obj) {
        try {
            if (obj instanceof LocalDate) {
                return ((LocalDate) obj).format(fechaFmt);
            } else if (obj instanceof java.sql.Date) {
                LocalDate ld = ((java.sql.Date) obj).toLocalDate();
                return ld.format(fechaFmt);
            } else if (obj instanceof java.util.Date) {
                Instant ins = ((Date) obj).toInstant();
                LocalDate ld = ins.atZone(ZoneId.systemDefault()).toLocalDate();
                return ld.format(fechaFmt);
            } else {
                // si ya es String, o cualquier otro, lo devolvemos tal cual
                return obj.toString();
            }
        } catch (Exception e) {
            return obj.toString();
        }
    }

    private void configurarAccionesCellFactory() {
        colAcciones.setCellFactory(new Callback<TableColumn<Usuario, Void>, TableCell<Usuario, Void>>() {
            @Override
            public TableCell<Usuario, Void> call(final TableColumn<Usuario, Void> param) {
                return new TableCell<Usuario, Void>() {
                    private final Button btnEdit = new Button("✎");
                    private final Button btnDelete = new Button("🗑");
                    private final HBox box = new HBox(8, btnEdit, btnDelete);

                    {
                        btnEdit.setStyle("-fx-background-color:#fff6ee; -fx-text-fill:#3B3027; -fx-background-radius:6; -fx-padding:6 8;");
                        btnDelete.setStyle("-fx-background-color:#e04f44; -fx-text-fill:white; -fx-background-radius:6; -fx-padding:6 8;");

                        btnEdit.setOnAction(e -> {
                            Usuario u = getTableView().getItems().get(getIndex());
                            if (u != null) editarUsuario(u);
                        });

                        btnDelete.setOnAction(e -> {
                            Usuario u = getTableView().getItems().get(getIndex());
                            if (u != null) eliminarUsuario(u);
                        });

                        box.setStyle("-fx-alignment: center;");
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(box);
                        }
                    }
                };
            }
        });
    }

    private void cargarUsuarios() {
        lista.setAll(usuarioServicio.obtenerTodos());
        if (tablaUsuarios != null) tablaUsuarios.setItems(lista);
    }

    @FXML
    public void buscarUsuario() {
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

    // mantiene la firma original (usa la selección)
    @FXML public void editarUsuario() {
        Usuario sel = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (sel==null) { mostrarAlerta("Selecciona un usuario"); return; }
        abrirEditor(sel);
    }

    // sobrecarga: usada por los botones de la celda
    public void editarUsuario(Usuario u) {
        abrirEditor(u);
    }

    @FXML public void eliminarUsuario() {
        Usuario sel = tablaUsuarios.getSelectionModel().getSelectedItem();
        if (sel==null) { mostrarAlerta("Selecciona un usuario"); return; }
        confirmarYEliminar(sel);
    }

    // sobrecarga: usada por botones de la celda
    public void eliminarUsuario(Usuario u) {
        confirmarYEliminar(u);
    }

    private void confirmarYEliminar(Usuario u) {
        Alert a=new Alert(Alert.AlertType.CONFIRMATION,"Eliminar usuario \"" + u.getNombreUsuario() + "\"?", ButtonType.OK,ButtonType.CANCEL);
        a.setHeaderText(null);
        Optional<ButtonType> r=a.showAndWait();
        if (r.isPresent() && r.get()==ButtonType.OK) {
            boolean ok = usuarioServicio.eliminarUsuario(u.getId());
            if (!ok) mostrarAlerta("No se pudo eliminar.");
            cargarUsuarios();
        }
    }

    private void abrirEditor(Usuario u) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/admin/editarUsuario.fxml"));
            Parent root = loader.load();
            com.example.biblioteca_digital.controladores.admin.ControladorEditarUsuario ctrl = loader.getController();
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

