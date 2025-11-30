package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.DAO.usuario.CatalogoDAO;
import com.example.biblioteca_digital.DAO.usuario.PrestamoDAO;
import com.example.biblioteca_digital.controladores.ControladorAyuda;
import com.example.biblioteca_digital.controladores.Navegacion;
import com.example.biblioteca_digital.modelos.Sesion;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class ControladorMenuInicioUsuario {

    @FXML
    private AnchorPane contenedor;
    @FXML
    private ToggleButton tbt_MENU;
    @FXML
    private ToggleButton tbt_PRESTAMOS;
    @FXML
    private ToggleButton tbt_FAVORITOS;
    @FXML
    private ImageView iv_iconoUsuario;
    @FXML
    private Button bt_ayuda;

    private Usuario usuarioActual;

    @FXML
    public void initialize()
    {
        CatalogoDAO catalogoDAO = new CatalogoDAO();
        PrestamoDAO prestamoDAO = new PrestamoDAO();
        catalogoDAO.setPrestamoDAO(prestamoDAO);
        prestamoDAO.setCatalogoDAO(catalogoDAO);
        iv_iconoUsuario.setOnMouseClicked(event ->
        {
            try
            {
                abrirPerfil(event);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                mostrarAlertaError("Error de Navegación", "No se pudo cargar la vista de perfil: " + e.getMessage());
            }
        });
    }
    public void setUsuario(Usuario usuario)
    {
        this.usuarioActual = usuario;
        Sesion.setUsuario(usuario);
        if (tbt_MENU != null)
        {
            tbt_MENU.setSelected(true);
            handleCatalogo();
        }
    }
    /**
     * Método genérico para cargar FXML, pasándole el Usuario al controlador.
     * @param fxmlPath Ruta del archivo FXML.
     * @param controllerClass Clase del controlador asociado para la inyección.
     */
    private void cargarVista(String fxmlPath, Class<?> controllerClass) {
        try
        {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent vista = loader.load();
            Object controlador = loader.getController();
            Usuario usuarioSesion = Sesion.getUsuario(); //Cogemos el usuario real

            contenedor.getChildren().clear();
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);
            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            contenedor.getChildren().add(vista);
            if (usuarioSesion != null) {
                if (controllerClass == ControladorCatalogoUsuario.class)
                {
                    ControladorCatalogoUsuario controlador1 = loader.getController();
                    controlador1.setUsuario(usuarioSesion);
                }
                else if (controllerClass == ControladorPrestamosUsuario.class)
                {
                    ControladorCatalogoUsuario controlador2 = loader.getController();
                    controlador2.setUsuario(usuarioSesion);
                }
                else if (controllerClass == ControladorFavoritosUsuario.class)
                {
                    ControladorCatalogoUsuario controlador3 = loader.getController();
                    controlador3.setUsuario(usuarioSesion);
                }
            }
            else
            {
                mostrarAlertaError("Error de sesión", "No se pudo cargar la vista: " + fxmlPath);
            }
//                if (controlador instanceof ControladorCatalogoUsuario) {
//                ((ControladorCatalogoUsuario) controlador).setUsuario(usuarioSesion);
//            } else if (controlador instanceof ControladorPrestamosUsuario) {
//                ((ControladorPrestamosUsuario) controlador).setUsuario(usuarioSesion);
//            } else if (controlador instanceof ControladorFavoritosUsuario) {
//                ((ControladorFavoritosUsuario) controlador).setUsuario(usuarioSesion);
//            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
            mostrarAlertaError("Error de E/S", "No se pudo cargar la vista: " + fxmlPath);
        }
    }

    /**
     * Muestra la vista de Ayuda en una ventana modal separada.
     * @param event El evento de acción.
     */
    @FXML
    public void mostrarAyuda(ActionEvent event)
    {
        ControladorAyuda.mostrarAyuda(
                "/com/example/biblioteca_digital/vistas/usuario/Vista-Ayuda-MenuUsuario.fxml",
                "Usuario"
        );
    }

    private void mostrarAlertaError(String titulo, String mensaje)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    @FXML
    private void abrirPerfil(MouseEvent event) throws IOException {
        URL fxmlUrl = getClass().getResource("/com/example/biblioteca_digital/vistas/usuario/Vista-Perfil-Usuario.fxml");
        if (fxmlUrl == null) {
            System.err.println("FXML de perfil no encontrado.");
            mostrarAlertaError("Error de Navegación", "No se pudo encontrar la vista de perfil.");
            return;
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        Stage perfilStage = new Stage();
        perfilStage.initOwner(((Stage) ((Node) event.getSource()).getScene().getWindow()));
        perfilStage.initModality(javafx.stage.Modality.APPLICATION_MODAL);
        perfilStage.setTitle("Perfil de Usuario");
        perfilStage.setScene(new Scene(root));
        perfilStage.showAndWait();
    }

    @FXML
    private void handleCatalogo() {
        cargarVista("/com/example/biblioteca_digital/vistas/usuario/Vista-Catalogo-Usuario.fxml", ControladorCatalogoUsuario.class);
    }

    @FXML
    private void handlePrestamos() {
        cargarVista("/com/example/biblioteca_digital/vistas/usuario/Vista-Prestamos-Usuario.fxml", ControladorPrestamosUsuario.class);
    }

    @FXML
    private void handleFavoritos() {
        cargarVista("/com/example/biblioteca_digital/vistas/usuario/Vista-Favoritos-Usuario.fxml", ControladorFavoritosUsuario.class);
    }
}