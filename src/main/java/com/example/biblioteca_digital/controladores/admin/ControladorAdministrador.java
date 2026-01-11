package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.DAO.admin.LibroAdminDAO;
import com.example.biblioteca_digital.DAO.admin.PrestamoAdminDAO;
import com.example.biblioteca_digital.DAO.admin.UsuarioAdminDAO;
import com.example.biblioteca_digital.modelos.Sesion;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

/**
 * Controlador principal del panel de administración.
 *
 * Se encarga de gestionar la navegación entre las distintas
 * secciones del administrador (libros, usuarios, préstamos y catálogo),
 * así como de mostrar estadísticas generales del sistema.
 */
public class ControladorAdministrador {

    /** Panel central donde se cargan dinámicamente las vistas del administrador. */
    @FXML private AnchorPane panelContenido;

    /** Etiqueta que muestra el número total de libros. */
    @FXML private Label lblTotalLibros;
    /** Etiqueta que muestra el número total de usuarios. */
    @FXML private Label lblTotalUsuarios;
    /** Etiqueta que muestra el número de préstamos activos. */
    @FXML private Label lblPrestamosActivos;
    /** Etiqueta que muestra el número de préstamos vencidos. */
    @FXML private Label lblPrestamosVencidos;

    /** Botón de acceso a la gestión de libros. */
    @FXML private Button btnLibrosAdmin;
    /** Botón de acceso a la gestión de usuarios. */
    @FXML private Button btnUsuariosAdmin;
    /** Botón de acceso a la gestión de préstamos. */
    @FXML private Button btnPrestamosAdmin;
    /** Botón de acceso al catálogo. */
    @FXML private Button btnCatalogoAdmin;

    /** DAO encargado de gestionar los libros desde administración. */
    private final LibroAdminDAO libroAdminDAO = new LibroAdminDAO();
    /** DAO encargado de gestionar los usuarios desde administración. */
    private final UsuarioAdminDAO usuarioAdminDAO = new UsuarioAdminDAO();
    /** DAO encargado de gestionar los préstamos desde administración. */
    private final PrestamoAdminDAO prestamoAdminDAO = new PrestamoAdminDAO();

    /** Timeline utilizado para el refresco automático de estadísticas. */
    private Timeline refrescoEstadisticas;

    /**
     * Método de inicialización del controlador.
     * Carga las estadísticas iniciales, inicia el refresco automático
     * y muestra por defecto el panel de libros.
     */
    @FXML
    public void initialize() {
        cargarEstadisticas();
        iniciarRefrescoAutomatico();
        mostrarLibros();
    }

    /**
     * Carga las estadísticas generales del sistema
     * (libros, usuarios y préstamos) desde los DAOs.
     */
    private void cargarEstadisticas() {
        lblTotalLibros.setText(String.valueOf(libroAdminDAO.contarLibros()));
        lblTotalUsuarios.setText(String.valueOf(usuarioAdminDAO.contarUsuarios()));
        lblPrestamosActivos.setText(String.valueOf(prestamoAdminDAO.contarPrestamosActivos()));
        lblPrestamosVencidos.setText(String.valueOf(prestamoAdminDAO.contarPrestamosVencidos()));
    }

    /**
     * Inicia un refresco automático de las estadísticas
     * cada segundo para mantener los datos actualizados.
     */
    private void iniciarRefrescoAutomatico() {
        refrescoEstadisticas = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> cargarEstadisticas())
        );
        refrescoEstadisticas.setCycleCount(Timeline.INDEFINITE);
        refrescoEstadisticas.play();
    }

    /**
     * Carga una vista FXML dentro del panel central del administrador.
     *
     * @param recurso Nombre del archivo FXML a cargar.
     */
    private void cargarPanel(String recurso) {
        try {
            Node n = FXMLLoader.load(
                    getClass().getResource("/com/example/biblioteca_digital/vistas/admin/" + recurso)
            );

            panelContenido.getChildren().setAll(n);

            AnchorPane.setTopAnchor(n, 0.0);
            AnchorPane.setBottomAnchor(n, 0.0);
            AnchorPane.setLeftAnchor(n, 0.0);
            AnchorPane.setRightAnchor(n, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Muestra el panel de gestión de libros y actualiza
     * los estilos de los botones de navegación.
     */
    @FXML
    public void mostrarLibros() {
        cargarPanel("adminLibros.fxml");
        btnLibrosAdmin.setStyle("-fx-background-color: transparent; -fx-padding:8 18; -fx-text-fill: #8B5E3C; -fx-font-weight: bold;");
        btnUsuariosAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
        btnPrestamosAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
        btnCatalogoAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
    }

    /**
     * Muestra el panel de gestión de usuarios y actualiza
     * los estilos de los botones de navegación.
     */
    @FXML
    public void mostrarUsuarios() {
        cargarPanel("adminUsuarios.fxml");
        btnUsuariosAdmin.setStyle("-fx-background-color: transparent; -fx-padding:8 18; -fx-text-fill: #8B5E3C; -fx-font-weight: bold;");
        btnLibrosAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
        btnPrestamosAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
        btnCatalogoAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
    }

    /**
     * Muestra el panel de gestión de préstamos y actualiza
     * los estilos de los botones de navegación.
     */
    @FXML
    public void mostrarPrestamos() {
        cargarPanel("adminPrestamos.fxml");
        btnPrestamosAdmin.setStyle("-fx-background-color: transparent; -fx-padding:8 18; -fx-text-fill: #8B5E3C; -fx-font-weight: bold;");
        btnUsuariosAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
        btnLibrosAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
        btnCatalogoAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
    }

    /**
     * Muestra el catálogo de libros y actualiza
     * los estilos de los botones de navegación.
     */
    @FXML
    public void mostrarCatalogo() {
        cargarPanel("adminCatalogo.fxml");
        btnCatalogoAdmin.setStyle("-fx-background-color: transparent; -fx-padding:8 18; -fx-text-fill: #8B5E3C; -fx-font-weight: bold;");
        btnUsuariosAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
        btnLibrosAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
        btnPrestamosAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
    }

    /**
     * Cierra la sesión del administrador y devuelve
     * a la pantalla de login.
     */
    @FXML
    public void cerrarSesion() {
        Sesion.cerrarSesion();

        if (refrescoEstadisticas != null) {
            refrescoEstadisticas.stop();
        }

        Stage st = (Stage) panelContenido.getScene().getWindow();
        try {
            Parent login = FXMLLoader.load(
                    getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Login.fxml")
            );
            st.getScene().setRoot(login);
        } catch (IOException e) {
            e.printStackTrace();
            st.close();
        }
    }
}
