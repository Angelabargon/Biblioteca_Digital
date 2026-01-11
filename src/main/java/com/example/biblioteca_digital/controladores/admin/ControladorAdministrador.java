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
 */
public class ControladorAdministrador {

    @FXML private AnchorPane panelContenido;

    @FXML private Label lblTotalLibros;
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblPrestamosActivos;
    @FXML private Label lblPrestamosVencidos;
    @FXML private Button btnLibrosAdmin;
    @FXML private Button btnUsuariosAdmin;
    @FXML private Button btnPrestamosAdmin;
    @FXML private Button btnCatalogoAdmin;

    private final LibroAdminDAO libroAdminDAO = new LibroAdminDAO();
    private final UsuarioAdminDAO usuarioAdminDAO = new UsuarioAdminDAO();
    private final PrestamoAdminDAO prestamoAdminDAO = new PrestamoAdminDAO();

    /** Timeline para refresco automático de estadísticas */
    private Timeline refrescoEstadisticas;

    @FXML
    public void initialize() {
        cargarEstadisticas();
        iniciarRefrescoAutomatico();
        mostrarLibros();
    }

    private void cargarEstadisticas() {
        lblTotalLibros.setText(String.valueOf(libroAdminDAO.contarLibros()));
        lblTotalUsuarios.setText(String.valueOf(usuarioAdminDAO.contarUsuarios()));
        lblPrestamosActivos.setText(String.valueOf(prestamoAdminDAO.contarPrestamosActivos()));
        lblPrestamosVencidos.setText(String.valueOf(prestamoAdminDAO.contarPrestamosVencidos()));
    }

    /**
     * 🔁 Refresca estadísticas automáticamente cada segundo
     */
    private void iniciarRefrescoAutomatico() {
        refrescoEstadisticas = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> cargarEstadisticas())
        );
        refrescoEstadisticas.setCycleCount(Timeline.INDEFINITE);
        refrescoEstadisticas.play();
    }

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

    @FXML public void mostrarLibros() {
        cargarPanel("adminLibros.fxml");
        btnLibrosAdmin.setStyle("-fx-background-color: transparent;  -fx-padding:8 18; -fx-text-fill: #8B5E3C; -fx-font-weight: bold;");
        btnUsuariosAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
        btnPrestamosAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
        btnCatalogoAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
    }

    @FXML public void mostrarUsuarios() {
        cargarPanel("adminUsuarios.fxml");
        btnUsuariosAdmin.setStyle("-fx-background-color: transparent;  -fx-padding:8 18; -fx-text-fill: #8B5E3C; -fx-font-weight: bold;");
        btnLibrosAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
        btnPrestamosAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
        btnCatalogoAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
    }

    @FXML public void mostrarPrestamos() {
        cargarPanel("adminPrestamos.fxml");
        btnPrestamosAdmin.setStyle("-fx-background-color: transparent;  -fx-padding:8 18; -fx-text-fill: #8B5E3C; -fx-font-weight: bold;");
        btnUsuariosAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
        btnLibrosAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
        btnCatalogoAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
    }

    @FXML public void mostrarCatalogo() {
        cargarPanel("adminCatalogo.fxml");
        btnCatalogoAdmin.setStyle("-fx-background-color: transparent;  -fx-padding:8 18; -fx-text-fill: #8B5E3C; -fx-font-weight: bold;");
        btnUsuariosAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
        btnLibrosAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
        btnPrestamosAdmin.setStyle("-fx-background-color:transparent; -fx-padding:8 18; -fx-font-weight:600;");
    }

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
