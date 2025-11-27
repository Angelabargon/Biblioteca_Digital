package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.controladores.ControladorAyuda;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
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
    private ImageView iv_iconoUsuario;
    @FXML
    private Button bt_ayuda;

    private Usuario usuarioActual;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        // Cargar la vista inicial (Catálogo)
        cargarVista("/com/example/biblioteca_digital/vistas/usuario/Vista-Catalogo-Usuario.fxml", true);
    }

    @FXML
    public void initialize() {
        iv_iconoUsuario.setOnMouseClicked(event -> {
            try {
                mostrarVistaPerfil();
            } catch (IOException e) {
                e.printStackTrace();
                mostrarAlertaError("Error de Navegación", "No se pudo cargar la vista de perfil: " + e.getMessage());
            }
        });
    }

    @FXML
    public void cambiarVista(ActionEvent event) {
        ToggleButton botonSeleccionado = (ToggleButton) event.getSource();
        String vistaFxml = null;
        boolean esCatalogo = false;

        if (botonSeleccionado == tbt_MENU) {
            vistaFxml = "/com/example/biblioteca_digital/vistas/usuario/Vista-Catalogo-Usuario.fxml";
            esCatalogo = true;
        } else if (botonSeleccionado == tbt_PRESTAMOS) {
            vistaFxml = "/com/example/biblioteca_digital/vistas/Vista-MisPrestamos-Usuario.fxml";
            esCatalogo = false;
        }

        if (vistaFxml != null) {
            cargarVista(vistaFxml, esCatalogo);
        }
    }

    private void cargarVista(String fxmlPath, boolean esCatalogo) {
        try {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                System.err.println("Recurso FXML no encontrado: " + fxmlPath);
                mostrarAlertaError("Error de Carga", "El archivo de vista no se encontró.");
                return;
            }

            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent vista = loader.load();

            // Pasar el usuario al controlador de la sub-vista
            if (esCatalogo) {
                ControladorCatalogoUsuario controlador = loader.getController();
                controlador.setUsuario(usuarioActual);
            }
            // else if (vista Prestamos) {
            //     ControladorPrestamosUsuario controlador = loader.getController();
            //     controlador.setUsuario(usuarioActual);
            // }

            contenedor.getChildren().clear();
            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);
            contenedor.getChildren().add(vista);

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlertaError("Error de E/S", "No se pudo cargar la vista: " + fxmlPath);
        }
    }

    /**
     * Muestra la vista de Ayuda en una ventana modal separada.
     * @param event El evento de acción.
     */
    @FXML
    public void mostrarAyuda(ActionEvent event) {
        ControladorAyuda.mostrarAyuda(
                "/com/example/biblioteca_digital/vistas/Vista-Ayuda-Usuario.fxml",
                "Usuario"
        );
    }

    private void mostrarVistaPerfil() throws IOException {
        Stage stage = (Stage) iv_iconoUsuario.getScene().getWindow();
        URL fxmlUrl = getClass().getResource("/com/example/biblioteca_digital/vistas/vista_perfil_usuario.fxml");
        if (fxmlUrl == null) {
            System.err.println("Recurso FXML de perfil no encontrado.");
            mostrarAlertaError("Error de Navegación", "No se pudo encontrar la vista de perfil.");
            return;
        }
        Parent root = FXMLLoader.load(fxmlUrl);
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("Perfil de Usuario");
        stage.show();
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}