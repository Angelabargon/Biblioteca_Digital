package com.example.biblioteca_digital.controladores.usuario;

/**
 * Imports necesarios de la clase.
 */
import com.example.biblioteca_digital.DAO.usuario.FavoritosDAO;
import com.example.biblioteca_digital.DAO.usuario.PrestamoDAO;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

/**
 * Controlador de los libros tanto en el catálogo
 * como en los favoritos, permite ver, pedir prestado
 * y ver algunos detalles de los libros
 */
public class ControladorLibroCatalogo {

    /** Etiqueta que muestra el título del libro. */
    @FXML private Label lblTitulo;
    /** Etiqueta que muestra el autor del libro. */
    @FXML private Label lblAutor;
    /** Etiqueta que muestra el género del libro. */
    @FXML private Label lblGenero;
    /** Etiqueta que muestra la portada del libro. */
    @FXML private ImageView imgPortada;
    /** Etiqueta que muestra la disponibilidad del libro. */
    @FXML private Label lblDisponibles;
    /** Botón que permite pedir prestado el libro. */
    @FXML private Button btnPedirPrestado;
    /** Etiqueta que muestra que el libro no está disponible. */
    @FXML private Label lblNoDisponibleTag;
    /** Botón que permite agregar a favoritos el libro. */
    @FXML private Button btnFavorito;
    /** Variable privada para acceder al libro */
    private Libro libroActual;
    /** Variable privada del usuario */
    private Usuario usuarioActual;
    /** Variable privada para utilizar el botón de ver en el catálogo de usuario */
    private ControladorCatalogoUsuario controladorPadre1;
    /** Variable privada para utilizar el botón de ver en el apartado de favoritos de usuario */
    private ControladorFavoritosUsuario controladorPadre2;
    /** Variable privada para utilizar los métodos privados de la parte de favoritos */
    private final FavoritosDAO favoritosDAO = new FavoritosDAO();
    /** Variable privada para utilizar los métodos privados de la parte de prestamos */
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    /**
     * Configuración para el CATÁLOGO
     */
    public void setDatos(Libro libro, Usuario usuario, ControladorCatalogoUsuario padre) {
        this.controladorPadre1 = padre;
        this.controladorPadre2 = null; // Aseguramos que el otro sea null
        inicializarComun(libro, usuario);
    }

    /**
     * Configuración para los FAVORITOS
     */
    public void setDatos(Libro libro, Usuario usuario, ControladorFavoritosUsuario padre) {
        this.controladorPadre2 = padre;
        this.controladorPadre1 = null;
        inicializarComun(libro, usuario);
    }

    /**
     * Método para rellenar la tarjeta
     * @param libro
     * @param usuario
     */
    private void inicializarComun(Libro libro, Usuario usuario) {
        this.libroActual = libro;
        this.usuarioActual = usuario;

        // si el objeto libro es nulo, no se puede hacer nada
        if (libro == null) return;

        // Manejo de título
        if (libro.getTitulo() != null && !libro.getTitulo().trim().isEmpty()) {
            lblTitulo.setText(libro.getTitulo());

        } else {
            lblTitulo.setText("Sin Nombre");
        }

        // Manejo del autor
        if (libro.getAutor() != null && !libro.getAutor().trim().isEmpty()) {
            lblAutor.setText(libro.getAutor());

        } else {
            lblAutor.setText("Anónimo");
        }

        // Manejo del género
        if (libro.getGenero() != null && !libro.getGenero().trim().isEmpty()) {
            lblGenero.setText(libro.getGenero());

        } else {
            lblGenero.setText("General");
        }

        // Si no hay usuario, desactiva botones y paramos aquí
        if (usuarioActual == null) {
            btnPedirPrestado.setDisable(true);
            btnFavorito.setDisable(true);
            cargarImagen(libro);
            return;
        }

        // Carga el resto de componentes
        cargarImagen(libro);
        actualizarEstadoDisponibilidad();
        actualizarBotonFavorito();
    }

    /**
     * Método que carga la imagen de libro si la hay, si no hay imágen de libro se utiliza una imagen genérica
     * @param libro
     */
    private void cargarImagen(Libro libro) {
        String ruta;

        //Si no existe la imagen
        if (libro.getFoto() != null && !libro.getFoto().trim().isEmpty()) {
            ruta = "/com/example/biblioteca_digital/imagenes/libros/" + libro.getFoto();

        } else {
            ruta = "/com/example/biblioteca_digital/imagenes/libros/generica.jpg";
        }

        try {
            Image portada = new Image(getClass().getResourceAsStream(ruta));

            if (portada.isError()) {
                System.err.println("No se encontró el archivo: " + ruta);
                ruta = "/com/example/biblioteca_digital/imagenes/libros/generica.jpg";
                portada = new Image(getClass().getResourceAsStream(ruta));
            }
            imgPortada.setImage(portada);

        } catch (Exception e) {
            System.err.println("Error crítico cargando imagen: " + e.getMessage());
        }
    }

    /**
     * Método que actualiza la disponibilidad de un libro cuando lo pides prestado
     */
    private void actualizarEstadoDisponibilidad() {

        int disponibles = libroActual.getCantidadDisponible();
        int cantidad = libroActual.getCantidad();
        boolean yaEstaPrestado = prestamoDAO.esLibroPrestadoPorUsuario(usuarioActual.getId(), libroActual.getId());

        if (cantidad <= 0 || yaEstaPrestado) {
            lblDisponibles.setText("No disponible");

            if (lblNoDisponibleTag != null) {
                lblNoDisponibleTag.setText(yaEstaPrestado ? "Ya Prestado" : "Agotado");
                lblNoDisponibleTag.setVisible(true);
                lblNoDisponibleTag.setManaged(true);
            }

            btnPedirPrestado.setDisable(true);

        } else {

            lblDisponibles.setText(String.format("Disponibles: %d/%d", cantidad, disponibles));

            if (lblNoDisponibleTag != null) {
                lblNoDisponibleTag.setVisible(false);
                lblNoDisponibleTag.setManaged(false);
            }

            btnPedirPrestado.setDisable(false);
        }
    }


    /**
     *Método para ver los detalles de un libro ya sea en catálogo o en favoritos (Donde esté el usuario)
     */
    @FXML
    private void handleVerDetalles() {

        if (controladorPadre1 != null) {
            controladorPadre1.clickVer(libroActual);

        } else if (controladorPadre2 != null) {
            controladorPadre2.clickVer(libroActual);
        }
    }

    /**
     * Método para el botón de pedir prestado
     */
    @FXML
    private void handlePedirPrestado() {

        if (controladorPadre1 != null) {
            controladorPadre1.clickPedirPrestamo(libroActual);
        }
    }

    /**
     * Metodo del botón de favoritos
     */
    @FXML
    private void handleAlternarFavorito() {
        if (libroActual != null && usuarioActual != null) {
            favoritosDAO.alternarFavorito(usuarioActual.getId(), libroActual.getId());
            actualizarBotonFavorito();
        }
    }

    /**
     * Método de la lógica actualización del botón de favoritos
     */
    private void actualizarBotonFavorito() {
        boolean esFavorito = favoritosDAO.esFavorito(usuarioActual.getId(), libroActual.getId());
        btnFavorito.setText("❤");
        if (esFavorito) {
            if (!btnFavorito.getStyleClass().contains("favorito-activo")) {
                btnFavorito.getStyleClass().add("favorito-activo");
            }

        } else {
            btnFavorito.getStyleClass().remove("favorito-activo");
        }
    }
}