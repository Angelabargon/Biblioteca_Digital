package com.example.biblioteca_digital.controladores.usuario;

/**
 * Imports necesarios.
 */
import com.example.biblioteca_digital.DAO.usuario.FavoritosDAO; // Necesitas un DAO de Favoritos
import com.example.biblioteca_digital.DAO.usuario.PrestamoDAO;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controlador encargado de gestionar la tarjeta visual de un libro dentro del catálogo.
 */
public class ControladorLibroCatalogo {

    @FXML private Label lblTitulo;
    @FXML private Label lblAutor;
    @FXML private Label lblGenero;
    @FXML private ImageView imgPortada;
    @FXML private Label lblDisponibles;
    @FXML private Button btnPedirPrestado;
    @FXML private Label lblNoDisponibleTag;
    @FXML private Button btnVer;
    @FXML private Button btnFavorito;

    /** Libro representado por esta tarjeta. */
    private Libro libroActual;

    /** Usuario actualmente logueado. */
    private Usuario usuarioActual;

    /** Controlador padre que maneja el catálogo completo. */
    private ControladorCatalogoUsuario controladorPadre;

    /** DAO para gestionar favoritos. */
    private final FavoritosDAO favoritosDAO = new FavoritosDAO();

    /** DAO para gestionar préstamos. */
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    /**
     * Configura la tarjeta con los datos del libro y del usuario.
     *
     * @param libro  Libro que se mostrará en la tarjeta.
     * @param usuario Usuario actual.
     * @param padre Controlador padre para delegar acciones.
     */
    public void setDatos(Libro libro, Usuario usuario, ControladorCatalogoUsuario padre) {
        this.libroActual = libro;
        this.usuarioActual = usuario;
        this.controladorPadre = padre;

        // De no haber usuario, se deshabilitan las acciones.
        if (usuarioActual == null) {
            System.err.println("Advertencia: Usuario no definido.");
            btnPedirPrestado.setDisable(true);
            btnFavorito.setDisable(true);
            return;
        }

        // Se muestran datos básicos del libro.
        lblTitulo.setText(libro.getTitulo());
        lblAutor.setText(libro.getAutor());
        lblGenero.setText(libro.getGenero());

        String nombreArchivo = libro.getFoto();

        if (nombreArchivo != null && !nombreArchivo.isEmpty()) {
            String rutaBase = "/com/example/biblioteca_digital/imagenes/libros/";
            String rutaCompleta = rutaBase + nombreArchivo;

            // El nombre del libro es clave para el mensaje de error.
            String tituloLibro = (libro != null && libro.getTitulo() != null) ? libro.getTitulo() : "Libro Desconocido";

            // Carga con el nombre de archivo exacto que está en la BD.
            try {
                Image portada = new Image(getClass().getResourceAsStream(rutaCompleta));
                if (!portada.isError()) {
                    imgPortada.setImage(portada);
                }
            } catch (Exception e) {
                System.err.println("Advertencia: No se pudo cargar la imagen para el libro " + tituloLibro + ". Ruta esperada: " + rutaCompleta + "." + e);
            }
        }

        int disponibles = libro.getCantidadDisponible();
        int stockTotal = libro.getCantidad();
        boolean yaEstaPrestado = prestamoDAO.esLibroPrestadoPorUsuario(usuarioActual.getId(), libro.getId());

        String disponiblesText = String.format("Disponibles: %d/%d", disponibles, stockTotal);
        lblDisponibles.setText(disponiblesText);

        // Mira si está disponible o no el libro y si el usuario lo tiene prestado ya o no.
        if (disponibles <= 0 || yaEstaPrestado) {
            lblDisponibles.setText("No disponible");

            if (lblNoDisponibleTag != null) {
                lblNoDisponibleTag.setText(yaEstaPrestado ? "Ya Prestado" : "Agotado");
                lblNoDisponibleTag.setVisible(true);
                lblNoDisponibleTag.setManaged(true);
            }
            btnPedirPrestado.setDisable(true);
            btnPedirPrestado.getStyleClass().add("btn-prestar-disabled");

        } else {
            lblDisponibles.setVisible(true);
            lblDisponibles.setManaged(true);

            if (lblNoDisponibleTag != null) {
                lblNoDisponibleTag.setVisible(false);
                lblNoDisponibleTag.setManaged(false);
            }
            btnPedirPrestado.setDisable(false);
            btnPedirPrestado.getStyleClass().remove("btn-prestar-disabled");
            btnPedirPrestado.getStyleClass().add("btn-prestar");
        }
        // Actualiza el icono de favorito.
        actualizarBotonFavorito();
    }

    /**
     * Metodo de botón pedir prestado.
     */
    @FXML
    private void handlePedirPrestado() {

        if (libroActual != null && controladorPadre != null) {
            controladorPadre.clickPedirPrestamo(libroActual);
        }
    }

    /**
     * Metodo de botón ver detalles.
     */
    @FXML
    private void handleVerDetalles() {

        if (libroActual != null && controladorPadre != null) {
            controladorPadre.clickVer(libroActual);
        }
    }

    /**
     * Metodo de botón añadir o eliminar de favoritos.
     */
    @FXML
    private void handleAlternarFavorito() {

        if (libroActual != null && usuarioActual != null) {
            favoritosDAO.alternarFavorito(usuarioActual.getId(), libroActual.getId());
            actualizarBotonFavorito();
        }
    }

    /**
     * Metodo de actualización de favorito.
     */
    private void actualizarBotonFavorito() {

        if (usuarioActual == null) {
            System.err.println("Advertencia: Usuario no definido.");
            btnPedirPrestado.setDisable(true);
            btnFavorito.setDisable(true);
            return;
        }

        boolean esFavorito = favoritosDAO.esFavorito(usuarioActual.getId(), libroActual.getId());

        if (esFavorito) {
            btnFavorito.setText("❤");
            btnFavorito.getStyleClass().add("favorito-activo");

        } else {
            btnFavorito.setText("♡");
            btnFavorito.getStyleClass().remove("favorito-activo");
        }
    }
}