package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.modelos.Prestamo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import java.util.function.Consumer;

public class ControladorPedirPrestamo
{
    // Componentes FXML de la tarjeta individual
    @FXML private Label lblTitulo;
    @FXML private Label lblAutor;
    @FXML private Label lblDiasRestantes;
    @FXML private Button btnLeerLibro;

    // Atributos internos
    private Prestamo prestamoActual;
    private Consumer<Prestamo> leerLibroHandler;

    /**
     * Establece los datos y el manejador de eventos para esta tarjeta de préstamo.
     */
    public void setPrestamo(Prestamo prestamo, String tiempoRestante, Consumer<Prestamo> handler)
    {
        this.prestamoActual = prestamo;
        this.leerLibroHandler = handler;

        // **NOTA:** Asumiendo que Prestamo.getLibro() devuelve el objeto Libro
        lblTitulo.setText(prestamo.getLibro().getTitulo());
        lblAutor.setText(prestamo.getLibro().getAutor());
        lblDiasRestantes.setText(tiempoRestante);

        // Deshabilitar si está vencido, similar al diseño de tu imagen
        if (tiempoRestante.startsWith("Vencido") || tiempoRestante.startsWith("Vence Hoy"))
        {
            btnLeerLibro.setDisable(true);
        }
    }

    @FXML
    private void handleBotonLeer()
    {
        if (leerLibroHandler != null)
        {
            leerLibroHandler.accept(prestamoActual);
        }
    }
}