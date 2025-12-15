package com.example.biblioteca_digital.controladores.usuario;

/**
 * Imports necesarios.
 */
import com.example.biblioteca_digital.DAO.usuario.CatalogoDAO; // Necesitas el DAO de Catálogo
import com.example.biblioteca_digital.modelos.Prestamo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.util.function.Consumer;

public class ControladorPedirPrestamo {

    // Componentes FXML de la tarjeta individual
    @FXML private Label lblTitulo;
    @FXML private Label lblAutor;
    @FXML private Label lblDiasRestantes;
    @FXML private Button btnLeerLibro;

    // Atributos internos
    private Prestamo prestamoActual;
    private Consumer<Prestamo> leerLibroHandler;
    private final CatalogoDAO catalogoDAO = new CatalogoDAO();

    /**
     * Metodo que establece los datos y el manejador de eventos para esta tarjeta de préstamo.
     */
    public void setPrestamo(Prestamo prestamo, String tiempoRestante, Consumer<Prestamo> handler) {
        this.prestamoActual = prestamo;
        this.leerLibroHandler = handler;

        lblTitulo.setText(prestamo.getLibro().getTitulo());

        String autor = catalogoDAO.obtenerAutorPorIdLibro(prestamo.getLibro().getId());
        lblAutor.setText(autor != null ? autor : "Autor Desconocido");

        lblDiasRestantes.setText(tiempoRestante);

        if (tiempoRestante.startsWith("Vencido") || tiempoRestante.startsWith("Vence Hoy")) {
            btnLeerLibro.setDisable(true);
        }
    }

    /**
     * Metodo que lleva a la vista de contenido del libro para leer
     */
    @FXML
    private void handleBotonLeer() {

        if (leerLibroHandler != null) {
            leerLibroHandler.accept(prestamoActual);
        }
    }
}