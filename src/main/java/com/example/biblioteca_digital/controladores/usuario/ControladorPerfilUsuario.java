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
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

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
    private Button bt_volver;

    /*

     */

    @FXML
    public void initialize() {
        usuarioActual = Sesion.getUsuario();
        cargarDatosPerfil();
    }

    private final PerfilUsuarioDAO perfilUsuarioDAO = new PerfilUsuarioDAO();

    private void cargarDatosPerfil() {
        if (usuarioActual == null) return;

        lb_nombreUsuario.setText("Nombre de Usuario: " + usuarioActual.getNombreUsuario());
        lb_nombreReal.setText("Nombre " + usuarioActual.getNombre());

        int numFavoritos = perfilUsuarioDAO.contarFavoritos(usuarioActual.getId());
        int numPrestamos = perfilUsuarioDAO.contarPrestamos(usuarioActual.getId());

        lb_favoritos.setText("Favoritos " + numFavoritos);
        lb_prestamos.setText("Préstamos " + numPrestamos);

    }

    @FXML
    private void cambiarContrasena(ActionEvent event) {
        Navegacion.cambiarVista(event, "/com/example/biblioteca_digital/vistas/Vista-Registro.fxml", "Cambiar Contraseña");

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
