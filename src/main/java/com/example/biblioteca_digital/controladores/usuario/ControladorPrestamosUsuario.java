package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.DAO.usuario.PrestamoDAO;
import com.example.biblioteca_digital.modelos.Prestamo;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.Node; // Importación directa para mayor claridad

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

public class ControladorPrestamosUsuario
{

    @FXML private VBox contenedorPrestamos;

    private Usuario usuarioActual;
    private final PrestamoDAO prestamoDAO = new PrestamoDAO();

    /**
     * Método que inicializa el controlador.
     */
    @FXML
    public void initialize()
    {
        // El FXML ya define VBox spacing y padding, se podría omitir aquí o usar solo para asegurar.
        if (contenedorPrestamos != null)
        {
            // Solo para asegurar, aunque el FXML ya lo define.
            contenedorPrestamos.setSpacing(15);
            contenedorPrestamos.setPadding(new Insets(10));
        }
    }

    /**
     * Metodo que establece el usuario actual y carga sus préstamos.
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
        // Carga los préstamos
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
    private Node crearVistaPrestamoItem(Prestamo prestamo)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(
                    "/com/example/biblioteca_digital/vistas/usuario/Vista-Prestamo-Item.fxml"));
            // Usar Node de javafx.scene.Node para la importación directa.
            Node item = loader.load();
            ControladorPedirPrestamo controlador = loader.getController();

            String tiempoRestante = calcularTiempoRestante(prestamo.getFecha_fin());

            // Asumiendo que el método handleLeerLibro está en esta clase.
            // Si el nombre del controlador es correcto, esta línea es la que propaga los datos.
            controlador.setPrestamo(prestamo, tiempoRestante, this::handleLeerLibro, this::handleQuitarLibro);

            return item;
        }
        catch (IOException e)
        {
            e.printStackTrace();
            return new Label("Error al cargar préstamo: " + e.getMessage());
        }
    }

    /**
     * Método que calcula el tiempo restante de un préstamo.
     * @param fechaFin La fecha de fin del préstamo.
     * @return String que indica los días restantes o si está vencido.
     */
    private String calcularTiempoRestante(LocalDate fechaFin)
    {
        long dias = ChronoUnit.DAYS.between(LocalDate.now(), fechaFin);
        if (dias > 0)
        {
            return dias + " días restantes"; // Cambio: Mostrar los días restantes directamente para ser más explícito
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
     * @param prestamo El préstamo a leer.
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
            stage.setTitle("Leyendo: " + titulo); // Añadir un prefijo al título.
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();
        }
        catch (Exception e)
        {
            System.err.println("Error al abrir ventana de lectura.");
            e.printStackTrace();
        }
    }
    /**
     * Método que elimina el préstamo de la lista
     * @param prestamo El préstamo a quitar.
     */
    private void handleQuitarLibro(Prestamo prestamo)
    {
        try
        {
            prestamoDAO.eliminarPrestamo(prestamo.getId());
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Préstamo eliminado");
            alert.setHeaderText(null);
            alert.setContentText(
                    "El libro \"" + prestamo.getLibro().getTitulo() +
                            "\" ha sido eliminado de tus préstamos."
            );
            alert.showAndWait();
            cargarPrestamosUsuario();
        }
        catch (SQLException e)
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("No se pudo eliminar el préstamo");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
            e.printStackTrace();
        }
    }

}