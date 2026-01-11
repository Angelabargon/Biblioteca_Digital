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

/**
 * Controlador principal del panel de administración.
 * <p>
 * Gestiona la pantalla principal del administrador, cargando dinámicamente las vistas
 * correspondientes a libros, usuarios, préstamos y catálogo dentro del panel central.
 * También muestra estadísticas básicas y permite cerrar sesión.
 */
public class ControladorAdministrador {

    /** Panel donde se cargarán dinámicamente las vistas del administrador. */
    @FXML private AnchorPane panelContenido;

    /** Etiqueta que muestra el número total de libros registrados. */
    @FXML private Label lblTotalLibros;

    /** Etiqueta que muestra el número total de usuarios registrados. */
    @FXML private Label lblTotalUsuarios;

    /** Etiqueta que muestra el número de préstamos activos. */
    @FXML private Label lblPrestamosActivos;

    /** Etiqueta que muestra el número de préstamos vencidos. */
    @FXML private Label lblPrestamosVencidos;

    /** DAO para operaciones relacionadas con libros. */
    private final LibroAdminDAO libroAdminDAO = new LibroAdminDAO();

    /** DAO para operaciones relacionadas con usuarios. */
    private final UsuarioAdminDAO usuarioAdminDAO = new UsuarioAdminDAO();

    /** DAO para operaciones relacionadas con préstamos. */
    private final PrestamoAdminDAO prestamoAdminDAO = new PrestamoAdminDAO();

    /**
     * Metodo inicializador automático.
     * <p>
     * Carga las estadísticas generales del sistema
     * y por defecto muestra la vista de listado de libros.
     */
    @FXML
    public void initialize() {
        cargarEstadisticas();
        mostrarLibros();
    }

    /**
     * Obtiene las estadísticas actuales de la base de datos y actualiza
     * los labels visibles en la interfaz.
     */
    public void cargarEstadisticas() {
        lblTotalLibros.setText(String.valueOf(libroAdminDAO.contarLibros()));
        lblTotalUsuarios.setText(String.valueOf(usuarioAdminDAO.contarUsuarios()));
        lblPrestamosActivos.setText(String.valueOf(prestamoAdminDAO.contarPrestamosActivos()));
        lblPrestamosVencidos.setText(String.valueOf(prestamoAdminDAO.contarPrestamosVencidos()));
    }

    /**
     * Actualiza las estadísticas y recarga la vista actual del panel central.
     * <p>
     * Este método se puede llamar desde cualquier controlador de las vistas
     * de administración después de añadir/editar/eliminar un libro, usuario o préstamo.
     *
     * @param recurso Nombre del FXML que estaba cargado (ej: "adminLibros.fxml")
     */
    public void actualizarPanel(String recurso) {
        cargarEstadisticas();
        cargarPanel(recurso);
    }

    /**
     * Carga dinámicamente un archivo FXML dentro del panel central.
     *
     * @param recurso Nombre del archivo FXML ubicado en
     *                <code>/com/example/biblioteca_digital/vistas/admin/</code>
     */
    private void cargarPanel(String recurso) {
        try {
            Node n = FXMLLoader.load(getClass().getResource("/com/example/biblioteca_digital/vistas/admin/" + recurso));
            panelContenido.getChildren().setAll(n);

            // Anclar completamente el contenido al panel
            AnchorPane.setTopAnchor(n, 0.0);
            AnchorPane.setBottomAnchor(n, 0.0);
            AnchorPane.setLeftAnchor(n, 0.0);
            AnchorPane.setRightAnchor(n, 0.0);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Muestra la vista de administración de libros.
     */
    @FXML public void mostrarLibros() { cargarPanel("adminLibros.fxml"); }

    /**
     * Muestra la vista de administración de usuarios.
     */
    @FXML public void mostrarUsuarios() { cargarPanel("adminUsuarios.fxml"); }

    /**
     * Muestra la vista de administración de préstamos.
     */
    @FXML public void mostrarPrestamos() { cargarPanel("adminPrestamos.fxml"); }

    /**
     * Muestra la vista del catálogo administrable.
     */
    @FXML public void mostrarCatalogo() { cargarPanel("adminCatalogo.fxml"); }

    /**
     * Cierra la sesión actual y redirige al usuario a la pantalla de login.
     * <p>
     * Si ocurre un error cargando la vista de login, la ventana se cerrará completamente.
     */
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
