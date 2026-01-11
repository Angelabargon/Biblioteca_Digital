package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.DAO.admin.UsuarioAdminDAO;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.beans.property.SimpleStringProperty;
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
 * Controlador encargado de la gestión de usuarios desde el panel
 * de administración.
 *
 * <p>
 * Permite listar, buscar, crear, editar y eliminar usuarios del sistema.
 * Utiliza una tabla con celdas personalizadas para mejorar la
 * visualización de roles, fechas y acciones disponibles.
 * </p>
 *
 * <p>
 * Se apoya en {@link UsuarioAdminDAO} para todas las operaciones
 * de acceso a datos.
 * </p>
 */
public class ControladorUsuarioAdmin {

    /** Tabla principal donde se muestran los usuarios. */
    @FXML private TableView<Usuario> tablaUsuarios;

    /** Columna que muestra el identificador del usuario. */
    @FXML private TableColumn<Usuario, Integer> colId;

    /** Columna que muestra el nombre de usuario. */
    @FXML private TableColumn<Usuario, String> colUsuario;

    /** Columna que muestra el correo electrónico del usuario. */
    @FXML private TableColumn<Usuario, String> colCorreo;

    /** Columna que muestra el rol del usuario. */
    @FXML private TableColumn<Usuario, String> colRol;

    /** Columna que muestra la fecha de registro del usuario. */
    @FXML private TableColumn<Usuario, Object> colFecha;

    /** Columna que contiene los botones de acciones (editar / eliminar). */
    @FXML private TableColumn<Usuario, Void> colAcciones;

    /** Campo de texto para búsqueda en tiempo real. */
    @FXML private TextField txtBuscar;

    /** DAO encargado de la gestión de usuarios. */
    private final UsuarioAdminDAO usuarioServicio = new UsuarioAdminDAO();

    /** Lista observable utilizada para poblar la tabla. */
    private final ObservableList<Usuario> lista =
            FXCollections.observableArrayList();

    /** Formato de fecha utilizado en la tabla. */
    private final DateTimeFormatter fechaFmt =
            DateTimeFormatter.ofPattern("d/M/yyyy");

    /**
     * Inicializa el controlador configurando columnas,
     * celdas personalizadas y carga inicial de datos.
     */
    @FXML
    public void initialize() {

        if (colUsuario != null)
            colUsuario.setCellValueFactory(
                    new PropertyValueFactory<>("nombreUsuario"));

        if (colCorreo != null)
            colCorreo.setCellValueFactory(
                    new PropertyValueFactory<>("correo"));

        if (colRol != null)
            colRol.setCellValueFactory(cell ->
                    new SimpleStringProperty(
                            cell.getValue().getRol().toString()
                    ));

        if (colFecha != null)
            colFecha.setCellValueFactory(
                    new PropertyValueFactory<>("fechaRegistro"));

        if (colRol != null) configurarRolCellFactory();
        if (colFecha != null) configurarFechaCellFactory();
        if (colAcciones != null) configurarAccionesCellFactory();

        cargarUsuarios();

        if (txtBuscar != null) {
            txtBuscar.textProperty()
                    .addListener((obs, oldV, newV) -> buscarUsuario());
        }
    }

    /**
     * Configura la columna de rol mostrando una etiqueta visual
     * distinta para administradores y usuarios normales.
     */
    private void configurarRolCellFactory() {
        colRol.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String rol, boolean empty) {
                super.updateItem(rol, empty);

                if (empty || rol == null) {
                    setGraphic(null);
                    setText(null);
                    return;
                }

                Label badge = new Label(
                        rol.equalsIgnoreCase("admin") ||
                                rol.equalsIgnoreCase("administrador")
                                ? "Administrador"
                                : "Usuario"
                );

                if (rol.equalsIgnoreCase("admin")
                        || rol.equalsIgnoreCase("administrador")
                        || rol.equalsIgnoreCase("admin_principal")) {

                    badge.setStyle(
                            "-fx-background-color:#8b4b2e; " +
                                    "-fx-text-fill:white; " +
                                    "-fx-padding:6 12; " +
                                    "-fx-background-radius:8; " +
                                    "-fx-font-weight:bold;"
                    );
                } else {
                    badge.setStyle(
                            "-fx-background-color:#d6b48a; " +
                                    "-fx-text-fill:#3B3027; " +
                                    "-fx-padding:6 12; " +
                                    "-fx-background-radius:8; " +
                                    "-fx-font-weight:600;"
                    );
                }

                setGraphic(badge);
                setText(null);
            }
        });
    }

    /**
     * Configura la columna de fecha permitiendo mostrar
     * correctamente distintos tipos de fecha.
     */
    private void configurarFechaCellFactory() {
        colFecha.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Object item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                } else {
                    setText(formatDateObject(item));
                }
            }
        });
    }

    /**
     * Convierte distintos tipos de fecha en una representación
     * legible para el usuario.
     *
     * @param obj objeto fecha a formatear
     * @return fecha formateada como texto
     */
    private String formatDateObject(Object obj) {
        try {
            if (obj instanceof LocalDate ld) {
                return ld.format(fechaFmt);
            } else if (obj instanceof java.sql.Date sql) {
                return sql.toLocalDate().format(fechaFmt);
            } else if (obj instanceof Date util) {
                Instant ins = util.toInstant();
                LocalDate ld =
                        ins.atZone(ZoneId.systemDefault()).toLocalDate();
                return ld.format(fechaFmt);
            } else {
                return obj.toString();
            }
        } catch (Exception e) {
            return obj.toString();
        }
    }

    /**
     * Configura la columna de acciones añadiendo botones
     * para editar y eliminar usuarios.
     */
    private void configurarAccionesCellFactory() {
        colAcciones.setCellFactory(
                new Callback<>() {
                    @Override
                    public TableCell<Usuario, Void> call(
                            TableColumn<Usuario, Void> param) {

                        return new TableCell<>() {

                            private final Button btnEdit =
                                    new Button("✎");
                            private final Button btnDelete =
                                    new Button("🗑");

                            private final HBox box =
                                    new HBox(8, btnEdit, btnDelete);

                            {
                                btnEdit.setStyle(
                                        "-fx-background-color:#fff6ee; " +
                                                "-fx-text-fill:#3B3027; " +
                                                "-fx-background-radius:6; " +
                                                "-fx-padding:6 8;"
                                );

                                btnDelete.setStyle(
                                        "-fx-background-color:#e04f44; " +
                                                "-fx-text-fill:white; " +
                                                "-fx-background-radius:6; " +
                                                "-fx-padding:6 8;"
                                );

                                btnEdit.setOnAction(e -> {
                                    Usuario u =
                                            getTableView().getItems()
                                                    .get(getIndex());
                                    if (u != null) editarUsuario(u);
                                });

                                btnDelete.setOnAction(e -> {
                                    Usuario u =
                                            getTableView().getItems()
                                                    .get(getIndex());
                                    if (u != null) eliminarUsuario(u);
                                });

                                box.setStyle("-fx-alignment: center;");
                            }

                            @Override
                            protected void updateItem(
                                    Void item, boolean empty) {

                                super.updateItem(item, empty);
                                setGraphic(empty ? null : box);
                            }
                        };
                    }
                });
    }

    /**
     * Carga todos los usuarios desde la base de datos
     * y los muestra en la tabla.
     */
    private void cargarUsuarios() {
        lista.setAll(usuarioServicio.obtenerTodos());
        tablaUsuarios.setItems(lista);
    }

    /**
     * Filtra usuarios por nombre de usuario, nombre real
     * o correo electrónico.
     */
    @FXML
    public void buscarUsuario() {
        String q = txtBuscar != null
                ? txtBuscar.getText().trim().toLowerCase()
                : "";

        if (q.isEmpty()) {
            cargarUsuarios();
            return;
        }

        ObservableList<Usuario> filtrado =
                lista.filtered(u ->
                        (u.getNombreUsuario() != null &&
                                u.getNombreUsuario()
                                        .toLowerCase().contains(q)) ||
                                (u.getNombre() != null &&
                                        u.getNombre()
                                                .toLowerCase().contains(q)) ||
                                (u.getCorreo() != null &&
                                        u.getCorreo()
                                                .toLowerCase().contains(q))
                );

        tablaUsuarios.setItems(filtrado);
    }

    /** Abre el editor en modo creación de usuario. */
    @FXML
    public void abrirAgregarUsuario() {
        abrirEditor(null);
    }

    /**
     * Abre el editor con el usuario seleccionado en la tabla.
     */
    @FXML
    public void editarUsuario() {
        Usuario sel =
                tablaUsuarios.getSelectionModel().getSelectedItem();

        if (sel == null) {
            mostrarAlerta("Selecciona un usuario");
            return;
        }

        abrirEditor(sel);
    }

    /**
     * Abre el editor para el usuario indicado.
     *
     * @param u usuario a editar
     */
    public void editarUsuario(Usuario u) {
        abrirEditor(u);
    }

    /**
     * Elimina el usuario seleccionado en la tabla.
     */
    @FXML
    public void eliminarUsuario() {
        Usuario sel =
                tablaUsuarios.getSelectionModel().getSelectedItem();

        if (sel == null) {
            mostrarAlerta("Selecciona un usuario");
            return;
        }

        confirmarYEliminar(sel);
    }

    /**
     * Elimina directamente el usuario indicado.
     *
     * @param u usuario a eliminar
     */
    public void eliminarUsuario(Usuario u) {
        confirmarYEliminar(u);
    }

    /**
     * Solicita confirmación antes de eliminar un usuario.
     *
     * @param u usuario a eliminar
     */
    private void confirmarYEliminar(Usuario u) {
        Alert a = new Alert(
                Alert.AlertType.CONFIRMATION,
                "Eliminar usuario \"" +
                        u.getNombreUsuario() + "\"?",
                ButtonType.OK,
                ButtonType.CANCEL
        );

        a.setHeaderText(null);

        Optional<ButtonType> r = a.showAndWait();

        if (r.isPresent() && r.get() == ButtonType.OK) {
            boolean ok =
                    usuarioServicio.eliminarUsuario(u.getId());

            if (!ok) mostrarAlerta("No se pudo eliminar.");

            cargarUsuarios();
        }
    }

    /**
     * Abre la ventana de creación o edición de usuarios
     * y gestiona el guardado correspondiente.
     *
     * @param u usuario a editar o null para crear uno nuevo
     */
    private void abrirEditor(Usuario u) {
        try {
            FXMLLoader loader =
                    new FXMLLoader(getClass().getResource(
                            "/com/example/biblioteca_digital/vistas/admin/editarUsuario.fxml"));

            Parent root = loader.load();

            ControladorEditarUsuario ctrl =
                    loader.getController();

            Stage st = new Stage();
            st.initOwner(tablaUsuarios.getScene().getWindow());
            st.initModality(Modality.APPLICATION_MODAL);

            ctrl.setStage(st);

            if (u != null) ctrl.setUsuario(u);

            ctrl.setOnGuardarCallback(() -> {
                Usuario res = ctrl.getUsuarioResultado();

                if (u == null) {
                    boolean ok =
                            usuarioServicio.agregarUsuario(res);
                    if (!ok) mostrarAlerta("No se pudo crear.");
                } else {
                    res.setId(u.getId());
                    boolean ok =
                            usuarioServicio.actualizarUsuario(res);
                    if (!ok)
                        mostrarAlerta("No se pudo actualizar.");
                }

                cargarUsuarios();
            });

            st.setScene(new javafx.scene.Scene(root));
            st.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta("Error al abrir el editor");
        }
    }

    /**
     * Muestra una alerta informativa simple.
     *
     * @param texto mensaje a mostrar
     */
    private void mostrarAlerta(String texto) {
        Alert a = new Alert(Alert.AlertType.WARNING);
        a.setHeaderText(null);
        a.setContentText(texto);
        a.showAndWait();
    }
}