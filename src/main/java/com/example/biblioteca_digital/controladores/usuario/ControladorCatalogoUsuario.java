package com.example.biblioteca_digital.controladores.usuario;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ControladorCatalogoUsuario { // Nombre del controlador de la sub-vista

    // Atributos de estado
    private Usuario usuarioActual;
    private List<Libro> libros = new ArrayList<>();
    private List<Integer> favoritos = new ArrayList<>();
    private int prestamosActivos = 0;

    // Elementos FXML de la VISTA CATÁLOGO
    @FXML private Label labelBienvenida;
    @FXML private Label labelContadorFavoritos;
    @FXML private Label labelContadorPrestamos;
    @FXML private FlowPane contenedorLibros; // Contiene las tarjetas de los libros
    @FXML private TextField filtroTitulo;
    @FXML private TextField filtroAutor;
    @FXML private ChoiceBox<String> filtroGenero; // Se ajustó a ChoiceBox según tu FXML
    @FXML private ToggleButton toggleFavoritos;
    @FXML private Button botonBuscarFavoritos; // Botón "Mostrar Favoritos"


    /**
     * Inicializa el controlador. Se usa para enlazar listeners a los filtros.
     */
    @FXML
    public void initialize() {
        // Enlazar listeners para que el filtrado se haga automáticamente al escribir o cambiar
        if (filtroTitulo != null) filtroTitulo.textProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());
        if (filtroAutor != null) filtroAutor.textProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());
        if (filtroGenero != null) filtroGenero.getSelectionModel().selectedItemProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());

        // El botón "Mostrar Favoritos" ahora es un ToggleButton en el FXML
        if (toggleFavoritos != null) toggleFavoritos.selectedProperty().addListener((obs, oldV, newV) -> mostrarLibrosFiltrados());

        // Alternativamente, si usas el botón con onAction
        // if (botonBuscarFavoritos != null) botonBuscarFavoritos.setOnAction(e -> mostrarLibrosFiltrados());
    }

    /**
     * Método principal para establecer el usuario y cargar los datos iniciales.
     */
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;

        if (labelBienvenida != null) labelBienvenida.setText("Bienvenido, " + usuario.getNombre());

        // Cargar todos los datos desde la base de datos
        libros = obtenerTodosLosLibros();
        favoritos = obtenerFavoritosUsuario(usuario.getId());
        prestamosActivos = contarPrestamosActivos(usuario.getId());

        // Actualizar contadores de la vista
        if (labelContadorFavoritos != null) labelContadorFavoritos.setText(String.valueOf(favoritos.size()));
        if (labelContadorPrestamos != null) labelContadorPrestamos.setText(String.valueOf(prestamosActivos));

        // Mostrar la lista inicial de libros
        mostrarLibrosFiltrados();
    }


    // --- LÓGICA DE FILTRADO Y VISUALIZACIÓN ---

    /**
     * Aplica los filtros actuales y actualiza el FlowPane de libros.
     */
    @FXML
    public void mostrarLibrosFiltrados() {
        if (contenedorLibros == null) return;

        contenedorLibros.getChildren().clear();
        String titulo = filtroTitulo.getText().toLowerCase();
        String autor = filtroAutor.getText().toLowerCase();
        String genero = filtroGenero.getValue() != null && !filtroGenero.getValue().equals("Todas")
                ? filtroGenero.getValue().toLowerCase() : "";

        boolean soloFavoritos = toggleFavoritos.isSelected();

        for (Libro libro : libros) {
            boolean coincide = libro.getTitulo().toLowerCase().contains(titulo)
                    && libro.getAutor().toLowerCase().contains(autor)
                    && (genero.isEmpty() || libro.getGenero().toLowerCase().equals(genero))
                    && (!soloFavoritos || favoritos.contains(libro.getId())); // Filtro de favoritos

            if (coincide) {
                contenedorLibros.getChildren().add(crearVistaLibroItem(libro));
            }
        }
    }

    // --- LÓGICA DE INTERACCIÓN DE TARJETAS (MÉTODOS PÚBLICOS) ---

    /**
     * Método llamado por el ControladorVistaLibroItem para alternar el favorito.
     */
    public void clickFavorito(Libro libro) {
        alternarFavorito(libro.getId());
        // El alternarFavorito ya llama a mostrarLibrosFiltrados() para refrescar
    }

    /**
     * Método llamado por el ControladorVistaLibroItem para pedir un préstamo.
     */
    public void clickPedirPrestamo(Libro libro) {
        if (pedirPrestado(libro.getId())) {
            // Refrescar la vista y mostrar confirmación
            mostrarAlerta("Préstamo Exitoso", "Has solicitado el préstamo de " + libro.getTitulo() + ".");
            // Actualizar la disponibilidad en el catálogo
            libros = obtenerTodosLosLibros();
            mostrarLibrosFiltrados();
        } else {
            mostrarAlertaError("Límite de Préstamo", "No puedes solicitar más préstamos o el libro no está disponible.");
        }
    }

    /**
     * Método llamado por el ControladorVistaLibroItem para ver detalles.
     */
    public void clickVer(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Libro-Usuario.fxml"));
            Parent root = loader.load();

            // Asume que este controlador maneja la vista de detalle
            ControladorLibrosUsuario controlador = loader.getController();
            controlador.setLibro(libro, usuarioActual);

            Stage stage = new Stage();
            stage.setTitle(libro.getTitulo());
            stage.setScene(new Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
            mostrarAlertaError("Error de Carga", "No se pudo cargar la vista de detalle del libro.");
        }
    }

    // --- LÓGICA DE CARGA DE VISTAS (Item del Libro) ---

    private Node crearVistaLibroItem(Libro libro) {
        try {
            // Asume que tienes un FXML para la tarjeta individual
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Libro-Item.fxml"));
            Parent item = loader.load();
            // Asume que tienes un controlador específico para la tarjeta de libro
            ControladorVistaLibro controlador = loader.getController();
            controlador.setDatos(libro, favoritos.contains(libro.getId()), this);

            return item;
        } catch (IOException e) {
            e.printStackTrace();
            // Retorna un error visual si falla la carga
            return new Label("Error al cargar item: " + libro.getTitulo());
        }
    }

    // --- LÓGICA DE BASE DE DATOS (Mismos métodos que tenías) ---

    // LIBROS
    private List<Libro> obtenerTodosLosLibros() { /* ... Tu lógica de DB ... */
        List<Libro> lista = new ArrayList<>();
        String sql = "SELECT * FROM libros";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery())
        {
            while (rs.next())
            {
                Libro libro = new Libro(
                        rs.getInt("id"),
                        rs.getString("titulo"),
                        rs.getString("autor"),
                        rs.getString("descripcion"),
                        rs.getString("genero"),
                        rs.getString("isbn"),
                        rs.getString("foto"),
                        rs.getInt("cantidad"),
                        rs.getBoolean("disponible")
                );
                lista.add(libro);
            }

        } catch (SQLException e)
        {
            e.printStackTrace();
        }
        return lista;
    }

    // FAVORITOS
    private List<Integer> obtenerFavoritosUsuario(int idUsuario) { /* ... Tu lógica de DB ... */
        List<Integer> lista = new ArrayList<>();
        String sql = "SELECT id_libro FROM favoritos WHERE id_usuario = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql))
        {
            pst.setInt(1, idUsuario);
            ResultSet rs = pst.executeQuery();
            while (rs.next())
            {
                lista.add(rs.getInt("id_libro"));
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        return lista;
    }

    private void alternarFavorito(int idLibro) {
        if (favoritos.contains(idLibro)) {
            eliminarFavorito(idLibro);
        } else {
            insertarFavorito(idLibro);
        }
        // Actualizar la lista local y el contador
        favoritos = obtenerFavoritosUsuario(usuarioActual.getId());
        if (labelContadorFavoritos != null) labelContadorFavoritos.setText(String.valueOf(favoritos.size()));

        // Refrescar el catálogo para actualizar el ícono de corazón
        mostrarLibrosFiltrados();
    }

    private void insertarFavorito(int idLibro) { /* ... Tu lógica de DB ... */
        String sql = "INSERT INTO favoritos (id_usuario, id_libro) VALUES (?, ?)";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql))
        {
            pst.setInt(1, usuarioActual.getId());
            pst.setInt(2, idLibro);
            pst.executeUpdate();

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void eliminarFavorito(int idLibro) { /* ... Tu lógica de DB ... */
        String sql = "DELETE FROM favoritos WHERE id_usuario = ? AND id_libro = ?";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql))
        {
            pst.setInt(1, usuarioActual.getId());
            pst.setInt(2, idLibro);
            pst.executeUpdate();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    // PRÉSTAMOS
    private int contarPrestamosActivos(int idUsuario) { /* ... Tu lógica de DB ... */
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_usuario = ? AND estado = 'activo'";
        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql))
        {
            pst.setInt(1, idUsuario);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) return rs.getInt(1);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return 0;
    }

    private boolean pedirPrestado(int idLibro) {
        // Lógica de validación antes de la inserción (por ejemplo, límite de préstamos)
        if (prestamosActivos >= 5) { // Ejemplo de límite
            mostrarAlertaError("Límite Alcanzado", "No puedes tener más de 5 libros en préstamo a la vez.");
            return false;
        }

        // Obtener el libro para verificar disponibilidad
        Libro libroAPrestar = libros.stream().filter(l -> l.getId() == idLibro).findFirst().orElse(null);
        if (libroAPrestar == null || libroAPrestar.getCantidad() <= 0) {
            // Mostrar error si el libro no está disponible
            return false;
        }

        String sql = "INSERT INTO prestamos (id_usuario, id_libro, fecha_inicio, fecha_fin, estado) " +
                "VALUES (?, ?, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 15 DAY), 'activo')";
        String sqlUpdate = "UPDATE libros SET cantidad = cantidad - 1 WHERE id = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql);
             PreparedStatement pstUpdate = con.prepareStatement(sqlUpdate))
        {
            // 1. Insertar préstamo
            pst.setInt(1, usuarioActual.getId());
            pst.setInt(2, idLibro);
            pst.executeUpdate();

            // 2. Decrementar disponibilidad del libro
            pstUpdate.setInt(1, idLibro);
            pstUpdate.executeUpdate();

            prestamosActivos++;
            if (labelContadorPrestamos != null) labelContadorPrestamos.setText(String.valueOf(prestamosActivos));
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlertaError("Error de Base de Datos", "No se pudo completar la solicitud de préstamo.");
            return false;
        }
    }

    // --- UTILIDADES ---
    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Información");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAlertaError(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
