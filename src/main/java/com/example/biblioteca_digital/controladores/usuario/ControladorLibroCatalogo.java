package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.DAO.usuario.FavoritosDAO;
import com.example.biblioteca_digital.DAO.usuario.PrestamoDAO;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

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

    private Libro libroActual;
    private Usuario usuarioActual;

    // Referencias a los posibles padres
    private ControladorCatalogoUsuario controladorPadre1;
    private ControladorFavoritosUsuario controladorPadre2;

    private final FavoritosDAO favoritosDAO = new FavoritosDAO();
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    /**
     * Configuración para el CATÁLOGO GENERAL
     */
    public void setDatos(Libro libro, Usuario usuario, ControladorCatalogoUsuario padre) {
        this.controladorPadre1 = padre;
        this.controladorPadre2 = null; // Aseguramos que el otro sea null
        inicializarComun(libro, usuario);
    }

    /**
     * Configuración para la vista de FAVORITOS
     */
    public void setDatos(Libro libro, Usuario usuario, ControladorFavoritosUsuario padre) {
        this.controladorPadre2 = padre;
        this.controladorPadre1 = null; // Aseguramos que el otro sea null
        inicializarComun(libro, usuario);
    }

    /**
     * Lógica compartida para rellenar la tarjeta (evita duplicar código)
     */
    private void inicializarComun(Libro libro, Usuario usuario) {
        this.libroActual = libro;
        this.usuarioActual = usuario;

        if (usuarioActual == null) {
            btnPedirPrestado.setDisable(true);
            btnFavorito.setDisable(true);
            return;
        }

        // Rellenar textos
        lblTitulo.setText(libro.getTitulo());
        lblAutor.setText(libro.getAutor());
        lblGenero.setText(libro.getGenero());

        // Cargar Imagen
        cargarImagen(libro);

        // Lógica de disponibilidad y préstamos
        actualizarEstadoDisponibilidad();

        // Actualizar icono corazón
        actualizarBotonFavorito();
    }

    private void cargarImagen(Libro libro) {
        if (libro.getFoto() != null && !libro.getFoto().isEmpty()) {
            String ruta = "/com/example/biblioteca_digital/imagenes/libros/" + libro.getFoto();
            try {
                Image portada = new Image(getClass().getResourceAsStream(ruta));
                if (!portada.isError()) imgPortada.setImage(portada);
            } catch (Exception e) {
                System.err.println("Error cargando imagen: " + ruta);
            }
        }
    }

    private void actualizarEstadoDisponibilidad() {
        int disponibles = libroActual.getCantidadDisponible();
        boolean yaEstaPrestado = prestamoDAO.esLibroPrestadoPorUsuario(usuarioActual.getId(), libroActual.getId());

        if (disponibles <= 0 || yaEstaPrestado) {
            lblDisponibles.setText("No disponible");
            if (lblNoDisponibleTag != null) {
                lblNoDisponibleTag.setText(yaEstaPrestado ? "Ya Prestado" : "Agotado");
                lblNoDisponibleTag.setVisible(true);
                lblNoDisponibleTag.setManaged(true);
            }
            btnPedirPrestado.setDisable(true);
        } else {
            lblDisponibles.setText(String.format("Disponibles: %d/%d", disponibles, libroActual.getCantidad()));
            if (lblNoDisponibleTag != null) {
                lblNoDisponibleTag.setVisible(false);
                lblNoDisponibleTag.setManaged(false);
            }
            btnPedirPrestado.setDisable(false);
        }
    }

    @FXML
    private void handleVerDetalles() {
        // Ejecuta el método clickVer del padre que esté activo
        if (controladorPadre1 != null) {
            controladorPadre1.clickVer(libroActual);
        } else if (controladorPadre2 != null) {
            controladorPadre2.clickVer(libroActual);
        }
    }

    @FXML
    private void handlePedirPrestado() {
        if (controladorPadre1 != null) {
            controladorPadre1.clickPedirPrestamo(libroActual);
        }
        // Nota: Si quieres que se pueda pedir desde favoritos,
        // deberías implementar clickPedirPrestamo en ControladorFavoritosUsuario también.
    }

    @FXML
    private void handleAlternarFavorito() {
        if (libroActual != null && usuarioActual != null) {
            favoritosDAO.alternarFavorito(usuarioActual.getId(), libroActual.getId());
            actualizarBotonFavorito();

            // Opcional: Si estamos en la vista de favoritos, refrescar al quitar
            if (controladorPadre2 != null) {
                // Podrías llamar a un método del padre para que refresque la lista
                // controladorPadre2.cargarFavoritos();
            }
        }
    }

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