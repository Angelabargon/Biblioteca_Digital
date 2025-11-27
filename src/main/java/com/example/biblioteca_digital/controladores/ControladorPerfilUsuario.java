package com.example.biblioteca_digital.controladores;

/*
Hacemos los imports necesarios.
 */
import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Sesion;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;


/*
Creamos la clase de ControladorPerfilUsuario para mostrar una ventana con los datos del usuario
junto con las opciones de cambiar contraseña y cerrar sesión.
 */
public class ControladorPerfilUsuario {

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
    private Button bt_volver;

    /*

     */

    @FXML
    public void initialize() {

        usuarioActual = Sesion.getUsuario();
        cargarDatosPerfil();
    }

    private void cargarDatosPerfil() {
        if (usuarioActual == null) return;

        lb_nombreUsuario.setText("Nombre de Usuario: " + usuarioActual.getNombreUsuario());
        lb_nombreReal.setText("Nombre " + usuarioActual.getNombre());

        int numFavoritos = contarFavoritos(usuarioActual.getId());
        int numPrestamos = contarPrestamos(usuarioActual.getId());

        lb_favoritos.setText("Favoritos " + String.valueOf(numFavoritos));
        lb_prestamos.setText("Préstamos " + String.valueOf(numPrestamos));
    }

    private int contarFavoritos(int idUsuario) {
        String sql = "SELECT COUNT(*) FROM favoritos WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idUsuario);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    private int contarPrestamos(int idUsuario) {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {
            pst.setInt(1, idUsuario);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt(1);
        } catch (Exception e) { e.printStackTrace(); }
        return 0;
    }

    @FXML
    private void cambiarContrasena(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Registro.fxml"));
            Parent root = loader.load();

            ControladorRegistro controlador = loader.getController();

            controlador.precargarDatos(usuarioActual);

            Stage stage = new Stage();
            stage.setTitle("Cambiar Contraseña");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void cerrarSesion(ActionEvent event) {
        Sesion.setUsuario(null);
        Navegacion.cambiarVista(event, "/com/example/biblioteca_digital/vistas/Vista-Pagina-Inicio.fxml", "Página de Inicio");
    }

    @FXML
    private void cerrarPerfil(ActionEvent event) {
        Navegacion.cerrarVentana(event);
    }

    @FXML
    private void volverAtras(ActionEvent event) {
        Navegacion.cambiarVista(event, "/com/example/biblioteca_digital/vistas/usuario/Vista-Menu-Usuario.fxml", "Menu Principal");
    }

    @FXML
    private void mostrarAyuda(ActionEvent event) {
        ControladorAyuda.mostrarAyuda("/com/example/biblioteca_digital/vistas/usuario/Vista-Ayuda-PerfilUsuario.fxml", "Perfil Usuario");
    }
}
