package com.example.biblioteca_digital.controladores;

import com.example.biblioteca_digital.modelos.Estado;
import com.example.biblioteca_digital.modelos.Prestamo;
import com.example.biblioteca_digital.modelos.Sesion;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.awt.*;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.ResourceBundle;

public class ControladorPrestamosUsuario implements Initializable
{
        @FXML
        private Label lblNumPrestamos;

        @FXML
        private VBox vbListaPrestamos;

        @FXML
        private Label lblNombreUsuario;

        @FXML
        private ToggleButton tbt_MENU;

        @FXML
        private ToggleButton tbt_PRESTAMOS;

        @FXML
        private AnchorPane contenedor;

    @FXML
    private void cambiarVista(javafx.event.ActionEvent actionEvent)
    {
        try {
            Parent nuevaVista;
            if (tbt_MENU.isSelected())
            {
                nuevaVista = FXMLLoader.load(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Menu-Usuario.fxml"));
                tbt_PRESTAMOS.setSelected(false);
            }
            else if (tbt_PRESTAMOS.isSelected())
            {
                nuevaVista = FXMLLoader.load(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Menu-Usuario.fxml"));
                tbt_PRESTAMOS.setSelected(false);
            }
            else
            {
                return;
            }
            contenedor.getChildren().setAll(nuevaVista);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        Usuario usuario = Sesion.getUsuario();
        if (usuario == null)
        {
            System.err.println("Error: No hay usuario activo en Sesion.");
            return;
        }
        cargarDatosUsuario(usuario);
        List<Prestamo> prestamosActivos = obtenerPrestamosDesdeBD(usuario.getId());
        mostrarPrestamos(prestamosActivos);
        lblNumPrestamos.setText(prestamosActivos.size() + " en préstamo");
    }
    /**
     * Obtener préstamos REALES desde tu base de datos.
     */
    private List<Prestamo> obtenerPrestamosDesdeBD(int idUsuario) {

        // TODO: conecta aquí tu DAO real
        // Ejemplo si lo tienes:
        // return PrestamoDAO.getPrestamosActivosPorUsuario(idUsuario);

        throw new UnsupportedOperationException("Debes conectar tu DAO aquí.");
    }
    private void mostrarPrestamos(List<Prestamo> prestamos)
    {
        vbListaPrestamos.getChildren().clear();
        for (Prestamo prestamo : prestamos)
        {
            try {
                FXMLLoader loader = new FXMLLoader(
                        getClass().getResource("/com/example/biblioteca_digital/vistas/PrestamoCard.fxml")
                );
                VBox card = loader.load();
                ControladorPrestamoIndividual controller = loader.getController();
                String tiempo = calcularTiempoRestante(prestamo);
                controller.setPrestamo(prestamo, tiempo, this::handleLeerLibro);
                vbListaPrestamos.getChildren().add(card);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                System.err.println("Error cargando PrestamoCard.fxml");
            }
        }
    }
    private String calcularTiempoRestante(Prestamo p)
    {
        LocalDate fin = p.getFecha_fin().toInstant()
                .atZone(ZoneId.systemDefault()).toLocalDate();
        long dias = ChronoUnit.DAYS.between(LocalDate.now(), fin);
        if (dias > 0) return dias + " días restantes";
        if (dias == 0) return "¡Vence hoy!";
        return "Vencido hace " + (-dias) + " días";
    }
    public void handleLeerLibro(Prestamo prestamo)
    {
        if (!prestamo.getEstado().equals( Estado.Activo))
        {
            mostrarAlerta("Préstamo inactivo", "No se puede leer este libro.");
            return;
        }
        String tiempo = calcularTiempoRestante(prestamo);
        if (tiempo.startsWith("Vencido")) {
            mostrarAlerta("Préstamo vencido", "El préstamo ha expirado.");
            return;
        }
        abrirLector(prestamo.getId_libro(), prestamo.getId());
    }
    private void abrirLector(int idLibro, int idPrestamo)
    {
        try
        {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Prestamo-Usuario.fxml")
            );
            VBox root = loader.load();
            Object controller = loader.getController();
            try
            {
                controller.getClass()
                        .getMethod("initializeConPrestamo", int.class, int.class)
                        .invoke(controller, idLibro, idPrestamo);
            }
            catch (NoSuchMethodException ignored) {}
            Stage stage = new Stage();
            stage.setTitle("Lector de Libros");
            stage.setScene(new Scene(root));
            stage.show();
        }
        catch (Exception e)
        {
            e.printStackTrace();
            mostrarAlerta("Error al abrir el lector", e.getMessage());
        }
    }
    private void cargarDatosUsuario(Usuario usuario)
    {
        String nombre = usuario.getNombre();
        String apellido = usuario.getPrimerApellido();
        lblNombreUsuario.setText("Bienvenido, " + nombre + " " + apellido);
    }
    private void mostrarAlerta(String titulo, String mensaje)
    {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.initModality(Modality.APPLICATION_MODAL);
        alert.showAndWait();
    }

    public void mostrarAyuda(ActionEvent actionEvent) {
    }
}
