package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.controladores.ControladorAyuda;
import com.example.biblioteca_digital.controladores.Navegacion;
import com.example.biblioteca_digital.modelos.Estado;
import com.example.biblioteca_digital.modelos.Usuario;
import com.example.biblioteca_digital.modelos.Prestamo;
import com.example.biblioteca_digital.modelos.Libro; // Necesario para el Prestamo
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

public class ControladorPrestamosUsuario {

    @FXML private VBox contenedorPrestamos;
    @FXML private Label labelMensaje; // Opcional, si quieres mantener el mensaje de bienvenida.
    @FXML private Label labelTituloSeccion; // Asumiendo que el "Mis Préstamos Activos" de la imagen es un Label

    private Usuario usuarioActual;

    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;
        // Asumiendo que "Mis Préstamos Activos" está en la sub-vista
        if (labelTituloSeccion != null) {
            labelTituloSeccion.setText("Mis Préstamos Activos");
        }
        cargarPrestamosUsuario(usuario.getId());
    }

    @FXML
    public void initialize() {
        if (contenedorPrestamos != null) {
            contenedorPrestamos.setSpacing(15);
            contenedorPrestamos.setPadding(new Insets(10));
        }
    }

    private void cargarPrestamosUsuario(int idUsuario) {
        if (contenedorPrestamos == null) return;

        contenedorPrestamos.getChildren().clear();
        List<Prestamo> listaPrestamos = obtenerPrestamosDeDB(idUsuario);

        if (listaPrestamos.isEmpty()) {
            Label noPrestamos = new Label("No tienes libros actualmente en préstamo.");
            noPrestamos.setFont(new Font(18));
            noPrestamos.setTextFill(Color.GRAY);
            contenedorPrestamos.getChildren().add(noPrestamos);
            return;
        }

        for (Prestamo prestamo : listaPrestamos) {
            contenedorPrestamos.getChildren().add(crearVistaPrestamoItem(prestamo));
        }
    }

    /**
     * Crea y carga el FXML de la tarjeta individual.
     */
    private Parent crearVistaPrestamoItem(Prestamo prestamo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Prestamo-Item.fxml"));
            Parent item = loader.load();
            ControladorPrestamoIndividual controlador = loader.getController();

            // 1. Calcular el tiempo restante para mostrar
            String tiempoRestante = obtenerCadenaTiempo(prestamo.getFecha_fin());

            // 2. Pasar datos y el manejador de eventos al controlador de la tarjeta
            // La función lambda (this::handleLeerLibro) es el 'Consumer<Prestamo>'
            controlador.setPrestamo(prestamo, tiempoRestante, this::handleLeerLibro);

            return item;
        } catch (IOException e) {
            e.printStackTrace();
            return new Label("Error al cargar la tarjeta de préstamo: " + prestamo.getLibro().getTitulo());
        }
    }

    /**
     * Genera la cadena de tiempo restante (ej: "Vence: 29/10/2025" o "3 días restantes").
     */
    private String obtenerCadenaTiempo(LocalDate fechaFin) {
        long dias = ChronoUnit.DAYS.between(LocalDate.now(), fechaFin);
        if (dias > 0) {
            return String.format("Vence: %02d/%02d/%d", fechaFin.getDayOfMonth(), fechaFin.getMonthValue(), fechaFin.getYear());
        } else if (dias == 0) {
            return "Vence Hoy";
        } else {
            return String.format("Vencido hace %d días", Math.abs(dias));
        }
    }

    // --- MANEJADOR DEL BOTÓN "LEER LIBRO" ---

    /**
     * Método llamado por el ControladorPrestamoIndividual cuando se pulsa "Leer Libro".
     */
    private void handleLeerLibro(Prestamo prestamo) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Lectura-Libro.fxml"));
            Parent root = loader.load();

            // Asumo que tienes un ControladorLecturaLibro para manejar la vista modal
            ControladorLeerLibro controlador = loader.getController();
            controlador.cargarContenido(prestamo);

            Stage stage = new Stage();
            stage.setTitle(prestamo.getLibro().getTitulo());
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL); // Bloquea la ventana principal
            stage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de Navegación", "No se pudo cargar la vista de lectura.");
        }
    }

    // --- MÉTODOS DE BASE DE DATOS (DAO) ---

    // **NOTA**: Este método debe devolver un Prestamo que contenga el objeto Libro completo.
    private List<Prestamo> obtenerPrestamosDeDB(int idUsuario) {
        List<Prestamo> lista = new ArrayList<>();
        // Unir Préstamos (p) y Libros (l)
        String sql = "SELECT p.id as pid, p.fecha_fin, p.estado, l.* FROM prestamos p JOIN libros l ON p.id_libro = l.id WHERE p.id_usuario = ? AND p.estado = 'activo'";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, idUsuario);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    // 1. Crear objeto Libro
                    Libro libro = new Libro(
                            rs.getInt("id"), rs.getString("titulo"), rs.getString("autor"),
                            rs.getString("genero"), rs.getString("isbn"), rs.getString("descripcion"),
                            rs.getString("foto"), rs.getInt("cantidad"), rs.getBoolean("disponible")
                    );
                    // 2. Crear objeto Prestamo, incluyendo el Libro
                    Prestamo prestamo = new Prestamo(
                            rs.getInt("pid"),
                            libro,
                            rs.getDate("fecha_fin").toLocalDate(),
                            Estado.valueOf(rs.getString("estado"))
                    );
                    lista.add(prestamo);

                }
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta(Alert.AlertType.ERROR, "Error de BD", "No se pudieron cargar los préstamos.");
        }
        return lista;
    }

    // --- UTILIDADES ---
    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String texto) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(texto);
        alert.show();
    }

    public void mostrarAyuda(ActionEvent event) {
       // ControladorAyuda.mostrarAyuda();
    }
}