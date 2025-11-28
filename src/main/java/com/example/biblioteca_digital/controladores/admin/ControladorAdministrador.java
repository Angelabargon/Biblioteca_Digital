package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.DAO.admin.LibroAdminDAO;
import com.example.biblioteca_digital.DAO.admin.PrestamoAdminDAO;
import com.example.biblioteca_digital.DAO.admin.UsuarioAdminDAO;
import com.example.biblioteca_digital.modelos.Sesion;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;

public class ControladorAdministrador {

    @FXML private AnchorPane panelContenido;
    @FXML private Label lblTotalLibros;
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblPrestamosActivos;
    @FXML private Label lblPrestamosVencidos;

    private final LibroAdminDAO LibroAdminDAO = new LibroAdminDAO();
    private final UsuarioAdminDAO UsuarioAdminDAO = new UsuarioAdminDAO();
    private final PrestamoAdminDAO PrestamoAdminDAO = new PrestamoAdminDAO();

    @FXML
    public void initialize() {
        cargarEstadisticas();
        mostrarLibros();
    }

    private void cargarEstadisticas() {
        lblTotalLibros.setText(String.valueOf(LibroAdminDAO.contarLibros()));
        lblTotalUsuarios.setText(String.valueOf(UsuarioAdminDAO.contarUsuarios()));
        lblPrestamosActivos.setText(String.valueOf(PrestamoAdminDAO.contarPrestamosActivos()));
        lblPrestamosVencidos.setText(String.valueOf(PrestamoAdminDAO.contarPrestamosVencidos()));
    }

    private void cargarPanel(String recurso) {
        try {
            Node n = FXMLLoader.load(getClass().getResource("/com/example/biblioteca_digital/vistas/admin/" + recurso));
            panelContenido.getChildren().setAll(n);
            AnchorPane.setTopAnchor(n, 0.0);
            AnchorPane.setBottomAnchor(n, 0.0);
            AnchorPane.setLeftAnchor(n, 0.0);
            AnchorPane.setRightAnchor(n, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML public void mostrarLibros() { cargarPanel("adminLibros.fxml"); }
    @FXML public void mostrarUsuarios() { cargarPanel("adminUsuarios.fxml"); }
    @FXML public void mostrarPrestamos() { cargarPanel("adminPrestamos.fxml"); }
    @FXML public void mostrarCatalogo() { cargarPanel("adminCatalogo.fxml"); }

    @FXML
    public void cerrarSesion() {
        Sesion.cerrarSesion();
        Stage st = (Stage) panelContenido.getScene().getWindow();
        try {
            Parent login = FXMLLoader.load(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Login.fxml"));
            st.getScene().setRoot(login);
        } catch (IOException e) {
            e.printStackTrace();
            st.close();
        }
    }
}
