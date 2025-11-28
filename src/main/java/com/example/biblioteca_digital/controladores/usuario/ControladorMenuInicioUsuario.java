package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.controladores.ControladorAyuda;
import com.example.biblioteca_digital.controladores.Navegacion;
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

    public void setUsuario(Usuario usuario)
    {
        this.usuarioActual = usuario;
        if (contenedor == null) {
            System.out.println("ERROR: El AnchorPane 'contenedor' es NULL en setUsuario().");
            return;
        }
        System.out.println("OK: El AnchorPane 'contenedor' ha sido inyectado correctamente.");
        cargarVista("/com/example/biblioteca_digital/vistas/usuario/Vista-Catalogo-Usuario.fxml", ControladorCatalogoUsuario.class);
    }

    @FXML
    public void initialize()
    {
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
    /**
     * Método genérico para cargar FXML, pasándole el Usuario al controlador.
     * @param fxmlPath Ruta del archivo FXML.
     * @param controllerClass Clase del controlador asociado para la inyección.
     */
    private void cargarVista(String fxmlPath, Class<?> controllerClass)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent vista = loader.load();
            Object controlador = loader.getController();
            contenedor.getChildren().clear();
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);
            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            contenedor.getChildren().add(vista);
            if (controlador instanceof ControladorCatalogoUsuario)
            {
                ((ControladorCatalogoUsuario) controlador).setUsuario(usuarioActual);
            }
            else if (controlador instanceof ControladorPrestamosUsuario)
            {
                ((ControladorPrestamosUsuario) controlador).setUsuario(usuarioActual);
            }
            else if (controlador instanceof ControladorFavoritosUsuario)
            {
                ((ControladorFavoritosUsuario) controlador).setUsuario(usuarioActual);
            }

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
        // Asegúrate de que el FXML esté creado en la ruta correcta
        cargarVista("/com/example/biblioteca_digital/vistas/usuario/Vista-Favoritos-Usuario.fxml", ControladorFavoritosUsuario.class);
    }
}