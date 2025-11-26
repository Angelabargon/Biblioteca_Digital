package com.example.biblioteca_digital.controladores.admin;

import com.example.biblioteca_digital.DAO.Admin.LibroAdminDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.control.Label;

import java.io.IOException;

import servicios.LibroServicio;
import servicios.UsuarioServicio;
import servicios.PrestamoServicio;

public class ControladorAdministrador {

    @FXML
    private AnchorPane panelContenido;

    @FXML
    private Label lblTotalLibros;
    @FXML
    private Label lblTotalUsuarios;
    @FXML
    private Label lblPrestamosActivos;
    @FXML
    private Label lblPrestamosVencidos;


    // ───────────────────────────────
    //      INICIALIZACIÓN GENERAL
    // ───────────────────────────────
    @FXML
    public void initialize() {
        cargarEstadisticas();
        cargarPanel("admin/LibrosAdmin.fxml"); // carga por defecto
    }


    // ───────────────────────────────
    //       CARGA DE SUBPANELES
    // ───────────────────────────────
    private void cargarPanel(String ruta) {
        try {
            Node node = FXMLLoader.load(getClass().getResource("/vistas/" + ruta));
            panelContenido.getChildren().setAll(node);

            AnchorPane.setTopAnchor(node, 0.0);
            AnchorPane.setBottomAnchor(node, 0.0);
            AnchorPane.setLeftAnchor(node, 0.0);
            AnchorPane.setRightAnchor(node, 0.0);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    // ───────────────────────────────
    //  EVENTOS DE LOS BOTONES (tabs)
    // ───────────────────────────────
    @FXML
    private void mostrarLibros() {
        cargarPanel("admin/LibrosAdmin.fxml");
    }

    @FXML
    private void mostrarUsuarios() {
        cargarPanel("admin/UsuariosAdmin.fxml");
    }

    @FXML
    private void mostrarPrestamos() {
        cargarPanel("admin/PrestamosAdmin.fxml");
    }

    @FXML
    private void mostrarCatalogo() {
        cargarPanel("admin/CatalogoAdmin.fxml");
    }


    // ───────────────────────────────
    //      ESTADÍSTICAS SUPERIORES
    // ───────────────────────────────
    private void cargarEstadisticas() {
        LibroAdminDAO libroServ = new LibroAdminDAO();
        UsuarioServicio usuarioServ = new UsuarioServicio();
        PrestamoServicio prestamoServ = new PrestamoServicio();

        lblTotalLibros.setText(String.valueOf(libroServ.contarLibros()));
        lblTotalUsuarios.setText(String.valueOf(usuarioServ.contarUsuarios()));
        lblPrestamosActivos.setText(String.valueOf(prestamoServ.contarPrestamosActivos()));
        lblPrestamosVencidos.setText(String.valueOf(prestamoServ.contarPrestamosVencidos()));
    }
}

