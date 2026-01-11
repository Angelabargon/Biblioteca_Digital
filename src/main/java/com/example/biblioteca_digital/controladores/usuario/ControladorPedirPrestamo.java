package com.example.biblioteca_digital.controladores.usuario;

/**
 * Imports necesarios de la clase.
 */
import com.example.biblioteca_digital.DAO.usuario.CatalogoDAO;
import com.example.biblioteca_digital.modelos.Prestamo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.util.function.Consumer;

/**
 * Controlador de la opción de pedir prestado un libro en la tarjeta individual y la detallada.
 */
public class ControladorPedirPrestamo {

    /** Etiqueta que muestra el título del libro. */
    @FXML private Label lblTitulo;
    /** Etiqueta que muestra el autor del libro. */
    @FXML private Label lblAutor;
    /** Etiqueta que muestra los días restantes del préstamo. */
    @FXML private Label lblDiasRestantes;
    /** Botón que muestra el contenido del libro. */
    @FXML private Button btnLeerLibro;
    /** Variable privada para utilizar los métodos privados de la parte de favoritos */
    private Prestamo prestamoActual;
    /** Variable privada para utilizar los métodos privados de la parte de favoritos */
    private Consumer<Prestamo> leerLibroHandler;
    /** Variable privada para utilizar los métodos privados de la parte de favoritos */
    private Consumer<Prestamo> quitarLibroHandler;
    /** Variable privada para utilizar los métodos privados de la parte de favoritos */
    private final CatalogoDAO catalogoDAO = new CatalogoDAO();

    /**
     * Método que permite pedir prestado un libro haciendo set para rellenar los datoss del préstamo
     * @param prestamo
     * @param tiempoRestante
     * @param leer
     * @param quitar
     */
    public void setPrestamo(
            Prestamo prestamo,
            String tiempoRestante,
            Consumer<Prestamo> leer,
            Consumer<Prestamo> quitar)
    {
        this.prestamoActual = prestamo;
        this.leerLibroHandler = leer;
        this.quitarLibroHandler = quitar;
        lblTitulo.setText(prestamo.getLibro().getTitulo());
        String autor = catalogoDAO.obtenerAutorPorIdLibro(prestamo.getLibro().getId());
        lblAutor.setText(autor != null ? autor : "Autor Desconocido");
        lblDiasRestantes.setText(tiempoRestante);

        if (tiempoRestante.startsWith("Vencido") || tiempoRestante.startsWith("Vence Hoy")) {
            lblDiasRestantes.getStyleClass().removeAll("badge-aviso");
            lblDiasRestantes.getStyleClass().add("badge-vencido");
            btnLeerLibro.setDisable(true);
        } else {
            lblDiasRestantes.getStyleClass().removeAll("badge-vencido");
            lblDiasRestantes.getStyleClass().add("badge-aviso");
        }
    }

    /**
     * Método utilizado para el botón de leer
     */
    @FXML
    private void handleBotonLeer() {
        if (leerLibroHandler != null) {
            leerLibroHandler.accept(prestamoActual);
        }
    }

    /**
     * Método utilizado para el botón de quitar libro de préstamos
     */
    @FXML
    private void handleBotonQuitar() {
        if (quitarLibroHandler != null) {
            quitarLibroHandler.accept(prestamoActual);
        }
    }
}
