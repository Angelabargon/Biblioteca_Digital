package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.DAO.usuario.CatalogoDAO;
import com.example.biblioteca_digital.modelos.Prestamo;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.function.Consumer;

public class ControladorPedirPrestamo
{
    @FXML private Label lblTitulo;
    @FXML private Label lblAutor;
    @FXML private Label lblDiasRestantes;
    @FXML private Button btnLeerLibro;
    @FXML private Button btnQuitarLibro;

    private Prestamo prestamoActual;
    private Consumer<Prestamo> leerLibroHandler;
    private Consumer<Prestamo> quitarLibroHandler;

    private final CatalogoDAO catalogoDAO = new CatalogoDAO();

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

    @FXML
    private void handleBotonQuitar()
    {
        if (quitarLibroHandler != null)
        {
            quitarLibroHandler.accept(prestamoActual);
        }
    }
}
