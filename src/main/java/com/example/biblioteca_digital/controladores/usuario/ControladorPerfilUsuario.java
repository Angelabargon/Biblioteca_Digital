package com.example.biblioteca_digital.controladores.usuario;

/*
Hacemos los imports necesarios.
 */
import com.example.biblioteca_digital.DAO.usuario.PerfilUsuarioDAO;
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
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

import java.io.IOException;

/*
Creamos la clase de ControladorPerfilUsuario para mostrar una ventana con los datos del usuario
junto con las opciones de cambiar contraseña y cerrar sesión.
 */
public class ControladorPerfilUsuario
{
    @FXML
    private ImageView iv_icono;

    @FXML
    private Label lb_nombreUsuario;

    @FXML
    private Label lb_nombreReal;

    @FXML
    private Label lb_favoritos;

    @FXML
    private Label lb_prestamos;

    @FXML
    private Button bt_cambioContrasena;

    @FXML
    private Button bt_cerrarSesion;

    private Usuario usuarioActual;

    @FXML
    private Button bt_cerrar;

    /**
     * Método que inicializa el controlador con el usuario actual.
     */
    @FXML
    public void initialize()
    {
        usuarioActual = Sesion.getInstancia().getUsuario();
        cargarDatosPerfil();
    }

    private final PerfilUsuarioDAO perfilUsuarioDAO = new PerfilUsuarioDAO();

    /**
     * Método que carga los datos del usuario actual
     */
    private void cargarDatosPerfil()
    {
        if (usuarioActual == null) return;

        lb_nombreUsuario.setText("Nombre de Usuario: " + usuarioActual.getNombreUsuario());
        lb_nombreReal.setText("Nombre: " + usuarioActual.getNombre());

        int numFavoritos = perfilUsuarioDAO.contarFavoritos(usuarioActual.getId());
        int numPrestamos = perfilUsuarioDAO.contarPrestamos(usuarioActual.getId());

        lb_favoritos.setText("Favoritos: " + numFavoritos);
        lb_prestamos.setText("Préstamos: " + numPrestamos);

    }

    /**
     * Método que permite al usuario cambiar su contraseña mediante un diálogo emergente.
     */
    @FXML
    private void cambiarContrasena(ActionEvent event) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cambiar Contraseña");
        dialog.setHeaderText("Introduce tu nueva contraseña:");
        dialog.setContentText("Contraseña:");

        dialog.showAndWait().ifPresent(nuevaPass -> {
            boolean ok = PerfilUsuarioDAO.actualizarContrasena(usuarioActual.getId(), nuevaPass);
            if (ok) {
                usuarioActual.setContrasena(nuevaPass);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Contraseña actualizada correctamente.");
                alert.showAndWait();
            } else {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error al actualizar la contraseña.");
                alert.showAndWait();
            }
        });
    }

    /**
     * Método que permite cerrar sesión y lleva a la pantalla de inicio
     * @param event
     */
    @FXML
    private void cerrarSesion(ActionEvent event)
    {
        Sesion.cerrarSesion();
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Pagina-Inicio.fxml"));
            Parent root = loader.load();

            Stage nuevaStage = new Stage();
            nuevaStage.setTitle("Página de Inicio");
            nuevaStage.setScene(new Scene(root));
            nuevaStage.show();

            // Cerrar la ventana del perfil
            Stage perfilStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            perfilStage.close();

            // Cerrar también la ventana principal
            Stage menuStage = (Stage) perfilStage.getOwner();
            if (menuStage != null) {
                menuStage.close();
            }

        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "No se pudo cargar la página de inicio.");
            alert.showAndWait();
        }
    }

    /**
     * Método que cierra la ventana de perfil
     */
    @FXML
    private void cerrarPerfil(ActionEvent event) {
        Navegacion.cerrarVentana(event);
    }

    /**
     * Método que muestra la ventana de ayuda de navegación por el perfil
     * @param event
     */
    @FXML
    private void mostrarAyuda(ActionEvent event) {
        ControladorAyuda.mostrarAyuda("/com/example/biblioteca_digital/vistas/usuario/Vista-Ayuda-PerfilUsuario.fxml", "Perfil Usuario");
    }
}
