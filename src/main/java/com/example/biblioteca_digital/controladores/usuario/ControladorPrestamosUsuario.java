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

    private Usuario usuarioActual;
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    /**
     * Método que inicializa el controlador con el usuario actual
     */
    @FXML
    public void initialize()
    {
        if (contenedorPrestamos != null)
        {
            contenedorPrestamos.setSpacing(15);
            contenedorPrestamos.setPadding(new Insets(10));
        }
    }
    /**
     * Metodo que establece el usuario actual y carga sus libros favoritos.
     * @param usuario El objeto Usuario actualmente logueado.
     */
    public void setUsuario(Usuario usuario)
    {
        this.usuarioActual = usuario;
        if (usuarioActual != null)
        {
            cargarPrestamosUsuario();
        }
        else
        {
            System.err.println("Usuario nulo en ControladorPrestamosUsuario.");
        }
    }

    /**
     * Método que carga la lista de préstamos activos y genera la vista dinámica.
     */
    private void cargarPrestamosUsuario()
    {
        contenedorPrestamos.getChildren().clear();
        List<Prestamo> prestamos = prestamoDAO.obtenerPrestamosDeUsuario(usuarioActual.getId());
        if (prestamos.isEmpty())
        {
            contenedorPrestamos.getChildren().add(new Label("No tienes préstamos activos en este momento."));
        }
        else
        {
            for (Prestamo prestamo : prestamos)
            {
                contenedorPrestamos.getChildren().add(crearVistaPrestamoItem(prestamo));
            }
        }
    }

    /**
     * Método que crea y configura un Node para un único préstamo.
     */
    private javafx.scene.Node crearVistaPrestamoItem(Prestamo prestamo)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/biblioteca_digital/vistas/usuario/Vista-Prestamo-Item.fxml"));
            javafx.scene.Node item = loader.load();
            ControladorPedirPrestamo controlador = loader.getController();
            String tiempoRestante = calcularTiempoRestante(prestamo.getFecha_fin());
            controlador.setPrestamo(prestamo, tiempoRestante, this::handleLeerLibro);
            return item;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return new Label("Error al cargar préstamo: " + e.getMessage());
        }
    }

    /**
     * Método que calcula el tiempo restante de un préstamo
     * @param fechaFin
     * @return
     */
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

    /**
     * Método que lleva a la ventana para leer el libro
     * @param prestamo
     */
    private void handleLeerLibro(Prestamo prestamo)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/biblioteca_digital/vistas/usuario/Vista-Leer-Libro.fxml"));
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
}
