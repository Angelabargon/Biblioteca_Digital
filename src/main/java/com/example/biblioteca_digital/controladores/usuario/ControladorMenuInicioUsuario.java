package com.example.biblioteca_digital.controladores.usuario;

/**
 * Imports necesarios de la clase.
 */
import com.example.biblioteca_digital.DAO.usuario.CatalogoDAO;
import com.example.biblioteca_digital.DAO.usuario.PrestamoDAO;
import com.example.biblioteca_digital.controladores.ControladorAyuda;
import com.example.biblioteca_digital.modelos.Sesion;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.net.URL;

/**
 * Controlador principal del menú de inicio del usuario.
 * También se encarga de cargar dinámicamente las vistas dentro del contenedor central.
 */
public class ControladorMenuInicioUsuario
{
    /** Contenedor donde se cargan dinámicamente las vistas internas. */
    @FXML private AnchorPane contenedor;
    /** Botones de navegación del menú lateral. */
    @FXML private ToggleButton tbt_MENU;
    /** Icono del usuario que abre la vista de perfil. */
    @FXML private ImageView iv_iconoUsuario;

    /**
     * Metodo que inicializa el controlador con el usuario actual.
     */
    @FXML
    public void initialize()
    {
        // Vinculación entre DAOs.
        CatalogoDAO catalogoDAO = new CatalogoDAO();
        PrestamoDAO prestamoDAO = new PrestamoDAO();
        catalogoDAO.setPrestamoDAO(prestamoDAO);
        prestamoDAO.setCatalogoDAO(catalogoDAO);
        // Evento para abrir el perfil al hacer clic en el icono.
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
        // Nos aseguramos de que esté seleccionado por defecto el catálogo.
        tbt_MENU.setSelected(true);
        handleCatalogo();
    }

    /**
     * Metodo que establece el usuario actual y carga sus libros favoritos.
     * @param usuario El objeto Usuario actualmente logueado.
     */
    public void setUsuario(Usuario usuario)
    {
        // Usuario actualmente logueado.
        Sesion.setUsuario(usuario);
        if (tbt_MENU != null)
        {
            tbt_MENU.setSelected(true);
            handleCatalogo();
        }
    }

    /**
     * Metodo genérico para cargar FXML, pasándole el Usuario al controlador.
     * @param fxmlPath Ruta del archivo FXML.
     */
    private void cargarVista(String fxmlPath)
    {
        try
        {
            URL fxmlUrl = getClass().getResource(fxmlPath);
            FXMLLoader loader = new FXMLLoader(fxmlUrl);
            Parent vista = loader.load();
            // Obtener usuario desde la sesión.
            Usuario usuarioSesion = Sesion.getInstancia().getUsuario();
            // Limpiar contenedor y anclar la nueva vista.
            contenedor.getChildren().clear();
            AnchorPane.setLeftAnchor(vista, 0.0);
            AnchorPane.setRightAnchor(vista, 0.0);
            AnchorPane.setTopAnchor(vista, 0.0);
            AnchorPane.setBottomAnchor(vista, 0.0);
            contenedor.getChildren().add(vista);
            // Pasar el usuario al controlador correspondiente.
            Object controller = loader.getController();
            if (controller instanceof ControladorCatalogoUsuario a)
            {
                a.setUsuario(usuarioSesion);
            }
            else if (controller instanceof ControladorPrestamosUsuario b)
            {
                b.setUsuario(usuarioSesion);
            }
            else if (controller instanceof ControladorFavoritosUsuario c)
            {
                c.setUsuario(usuarioSesion);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
            mostrarAlertaError("Error de E/S", "No se pudo cargar la vista: " + fxmlPath);
        }
    }

    /**
     * Metodo que muestra la vista de Ayuda en una ventana modal separada.
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

    /**
     * Metodo que muestra la alerta de error con un título y mensaje especificado.
     * @param titulo
     * @param mensaje
     */
    private void mostrarAlertaError(String titulo, String mensaje)
    {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    /**
     * Metodo para abrir el perfil como ventana modal
     * al clickar la imagen de perfil, para ver los datos.
     */
    @FXML
    private void abrirPerfil(MouseEvent event) throws IOException
    {
        URL fxmlUrl = getClass().getResource("/com/example/biblioteca_digital/vistas/usuario/Vista-Perfil-Usuario.fxml");
        if (fxmlUrl == null)
        {
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

    /**
     * Metodo del togglebutton que muestra el catálogo.
     */
    @FXML
    private void handleCatalogo()
    {
        cargarVista("/com/example/biblioteca_digital/vistas/usuario/Vista-Catalogo-Usuario.fxml");
    }

    /**
     * Metodo del togglebutton que muestra los préstamos.
     */
    @FXML
    private void handlePrestamos()
    {
        cargarVista("/com/example/biblioteca_digital/vistas/usuario/Vista-Prestamos-Usuario.fxml");
    }

    /**
     * Metodo del togglebutton que muestra los favoritos.
     */
    @FXML
    private void handleFavoritos()
    {
        cargarVista("/com/example/biblioteca_digital/vistas/usuario/Vista-Favoritos-Usuario.fxml");
    }
}