package com.example.biblioteca_digital.controladores;

import com.example.biblioteca_digital.modelos.Prestamo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.function.Consumer;

public class ControladorPrestamoIndividual
{
    // Componentes FXML de la tarjeta individual
    @FXML private Label lblTitulo;
    @FXML private Label lblAutor;
    @FXML private Label lblDiasRestantes;
    @FXML private Button btnLeerLibro;
    // Atributos internos
    private Prestamo prestamoActual;
    private Consumer<Prestamo> leerLibroHandler; // Almacena la referencia a handleLeerLibro
    /**
     * Establece los datos y el manejador de eventos para esta tarjeta de préstamo.
     * @param prestamo El objeto Prestamo a mostrar.
     * @param tiempoRestante La cadena de texto (ej: "10 días restantes").
     * @param handler La función del controlador principal para manejar el clic en "Leer".
     */
    public void setPrestamo(Prestamo prestamo, String tiempoRestante, Consumer<Prestamo> handler)
    {
        this.prestamoActual = prestamo;
        this.leerLibroHandler = handler;
        lblTitulo.setText(prestamo.getLibro().getTitulo());
        lblAutor.setText(prestamo.getLibro().getAutor());
        lblDiasRestantes.setText(tiempoRestante);
        if (tiempoRestante.startsWith("Vencido"))
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
