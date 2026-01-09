package com.example.biblioteca_digital.controladores.usuario;

/**
 * Imports necesarios de la clase.
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
import javafx.scene.control.Label;
import javafx.scene.control.TextInputDialog;
import javafx.stage.Stage;
import java.io.IOException;

/**
 * Controlador de la vista de perfil de usuario.
 * Muestra los datos usuario actual y otorga opciones
 * para cambiar la contraseña, cerrar sesión, cerrar la ventana
 * o acceder a la ayuda.
 */
public class ControladorPerfilUsuario
{
    /** Label que muestra el nombre de usuario. */
    @FXML private Label lb_nombreUsuario;
    /** Label que muestra el nombre real del usuario. */
    @FXML private Label lb_nombreReal;
    /** Label que muestra el número de favoritos del usuario. */
    @FXML private Label lb_favoritos;
    /** Label que muestra el número de préstamos del usuario. */
    @FXML private Label lb_prestamos;
    /** Usuario actualmente logueado en la sesión. */
    private Usuario usuarioActual;
    /** DAO para operaciones del perfil del usuario. */
    private final PerfilUsuarioDAO perfilUsuarioDAO = new PerfilUsuarioDAO();

    /**
     * Inicia automáticamente la vista.
     * Obtiene el usuario actual de la sesión y carga sus datos.
     */
    @FXML
    public void initialize()
    {
        usuarioActual = Sesion.getInstancia().getUsuario();
        cargarDatosPerfil();
    }

    /**
     * Carga los datos del perfil del usuario en los labels.
     * Muestra nombre de usuario, nombre real, número de favoritos y número de préstamos.
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
     * Le da la opción al usuario cambiar su contraseña con una ventana emergente.
     * Si la actualización es correcta, se muestra un mensaje de confirmación,
     * en caso contrario, se muestra un mensaje de error.
     * @param event evento de acción generado al pulsar el botón
     */
    @FXML
    private void cambiarContrasena(ActionEvent event)
    {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Cambiar Contraseña");
        dialog.setHeaderText("Introduce tu nueva contraseña:");
        dialog.setContentText("Contraseña:");
        dialog.showAndWait().ifPresent(nuevaPass ->
        {
            boolean ok = perfilUsuarioDAO.actualizarContrasena(usuarioActual.getId(), nuevaPass);
            if (ok)
            {
                usuarioActual.setContrasena(nuevaPass);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, "Contraseña actualizada correctamente.");
                alert.showAndWait();

            }
            else
            {
                Alert alert = new Alert(Alert.AlertType.ERROR, "Error al actualizar la contraseña.");
                alert.showAndWait();
            }
        });
    }

    /**
     * Cierra la sesión actual y envía al usuario a la página de inicio,
     * cerrando tanto la ventana de perfil como la ventana principal asociada.
     * @param event evento de acción generado al pulsar el botón.
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
            Stage perfilStage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            perfilStage.close();
            Stage menuStage = (Stage) perfilStage.getOwner();
            if (menuStage != null)
            {
                menuStage.close();
            }

        }
        catch (IOException e)
        {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "No se pudo cargar la página de inicio.");
            alert.showAndWait();
        }
    }

    /**
     * Cierra solo la ventana de perfil.
     * @param event evento de acción generado al pulsar el botón.
     */
    @FXML
    private void cerrarPerfil(ActionEvent event)
    {
        Navegacion.cerrarVentana(event);
    }

    /**
     * Muestra la ventana de ayuda del perfil de usuario.
     * @param event evento de acción generado al pulsar el botón.
     */
    @FXML
    private void mostrarAyuda(ActionEvent event)
    {
        ControladorAyuda.mostrarAyuda("/com/example/biblioteca_digital/vistas/usuario/Vista-Ayuda-PerfilUsuario.fxml", "Perfil Usuario");
    }
}
