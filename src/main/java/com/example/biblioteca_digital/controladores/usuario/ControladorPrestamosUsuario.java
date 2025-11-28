package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.DAO.usuario.PrestamoDAO;
import com.example.biblioteca_digital.modelos.Prestamo;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ControladorPrestamosUsuario
{

    @FXML private VBox contenedorPrestamos;
    @FXML private Label labelTituloSeccion;

    private Usuario usuarioActual;
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    @FXML
    public void initialize()
    {
        if (contenedorPrestamos != null)
        {
            contenedorPrestamos.setSpacing(15);
            contenedorPrestamos.setPadding(new Insets(10));
        }
    }

    public void setUsuario(Usuario usuario)
    {
        this.usuarioActual = usuario;
        if (labelTituloSeccion != null)
        {
            labelTituloSeccion.setText("Mis Préstamos Activos");
        }
        cargarPrestamosUsuario();
    }

    // CARGA DE PRÉSTAMOS

    private void cargarPrestamosUsuario()
    {
        contenedorPrestamos.getChildren().clear();
        List<Prestamo> lista = prestamoDAO.obtenerPrestamosDeUsuario(usuarioActual.getId());
        if (lista.isEmpty())
        {
            Label noPrestamos = new Label("No tienes libros actualmente en préstamo.");
            noPrestamos.setStyle("-fx-font-size: 16px; -fx-text-fill: #777;");
            contenedorPrestamos.getChildren().add(noPrestamos);
            return;
        }
        for (Prestamo p : lista)
        {
            contenedorPrestamos.getChildren().add(crearPrestamoItem(p));
        }
    }

    // CREAR TARJETA FXML

    private Parent crearPrestamoItem(Prestamo prestamo)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/biblioteca_digital/vistas/Vista-Prestamo-Item.fxml"
            ));
            Parent item = loader.load();
            ControladorPedirPrestamo controlador = loader.getController();
            String tiempo = calcularTiempoRestante(prestamo.getFecha_fin());
            controlador.setPrestamo(prestamo, tiempo, this::handleLeerLibro);
            return item;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return new Label("Error al cargar préstamo");
        }
    }

    // TIEMPO RESTANTE

    private String calcularTiempoRestante(LocalDate fechaFin)
    {
        long dias = ChronoUnit.DAYS.between(LocalDate.now(), fechaFin);
        if (dias > 0)
        {
            return "Vence: " + fechaFin.toString();
        } else if (dias == 0)
        {
            return "Vence Hoy";
        }
        else
        {
            return "Vencido hace " + Math.abs(dias) + " días";
        }
    }

    // MANEJADOR DEL BOTÓN "LEER LIBRO"

    // Dentro de ControladorPrestamosUsuario.java, método handleLeerLibro

    private void handleLeerLibro(Prestamo prestamo)
    {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/biblioteca_digital/vistas/Vista-Prestamo-Item.fxml"));
            Parent root = loader.load();
            ControladorLeerLibro controlador = loader.getController();
            controlador.cargarContenido(prestamo);
            javafx.stage.Stage stage = new javafx.stage.Stage();
            String titulo = (prestamo.getLibro() != null) ? prestamo.getLibro().getTitulo() : "Libro";
            stage.setTitle(titulo);
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    //AYUDA//
}
