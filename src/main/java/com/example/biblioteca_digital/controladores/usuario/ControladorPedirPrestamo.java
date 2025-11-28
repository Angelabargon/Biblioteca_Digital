package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.DAO.usuario.CatalogoDAO; // Necesitas el DAO de Catálogo
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
    private final CatalogoDAO catalogoDAO = new CatalogoDAO(); // Para obtener el autor

    /**
     * Establece los datos y el manejador de eventos para esta tarjeta de préstamo.
     */
    public void setPrestamo(Prestamo prestamo, String tiempoRestante, Consumer<Prestamo> handler)
    {
        this.prestamoActual = prestamo;
        this.leerLibroHandler = handler;

        lblTitulo.setText(prestamo.getLibro().getTitulo());

        // ✅ SOLUCIÓN: Usar el DAO para obtener el autor con el ID del libro
        String autor = catalogoDAO.obtenerAutorPorIdLibro(prestamo.getId_libro());
        lblAutor.setText(autor != null ? autor : "Autor Desconocido");

        lblDiasRestantes.setText(tiempoRestante);

        // Deshabilitar si está vencido
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