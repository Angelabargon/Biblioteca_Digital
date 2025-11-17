package com.example.biblioteca_digital.controladores;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.sql.*;


public class ControladorLibrosUsuario {

    //Elementos Vista
    @FXML
    private ListView<Libro> listaLibros;
    @FXML
    private Label tituloLabel;
    @FXML
    private Label autorLabel;
    @FXML
    private Label categoriaLabel;
    @FXML
    private Label isbnLabel;
    @FXML
    private Label disponiblesLabel;
    @FXML
    private ImageView imagenLibro;
    @FXML
    private TextArea descripcionArea;
    @FXML
    private Button btnAgregarFavorito;
    @FXML
    private Button btnPedirPrestado;


    private Usuario usuarioActual;

    // Inicializar vista
    public void inicializar(Usuario usuario) {
        this.usuarioActual = usuario;
        cargarLibros();
    }

    //Controladores
    private void cargarLibros() {
        ObservableList<Libro> data = FXCollections.observableArrayList(obtenerTodosLosLibros());
        listaLibros.setItems(data);

        listaLibros.setOnMouseClicked(e ->
                mostrarDetalles(listaLibros.getSelectionModel().getSelectedItem()));
    }

    private void mostrarDetalles(Libro libro) {
        if (libro == null) return;

        tituloLabel.setText(libro.getTitulo());
        autorLabel.setText(libro.getAutor());
        categoriaLabel.setText(libro.getGenero());
        isbnLabel.setText(libro.getIsbn());
        disponiblesLabel.setText(String.valueOf(libro.getDisponible()));
        descripcionArea.setText(libro.getDescripcion());

        if (libro.getFoto() != null)
            imagenLibro.setImage(new Image(libro.getFoto()));

        btnPedirPrestado.setDisable(libro.getCantidad() <= 0);
    }

    @FXML
    private void agregarFavorito() {
        Libro libro = listaLibros.getSelectionModel().getSelectedItem();
        if (libro == null) return;

        insertarFavorito(usuarioActual.getId(), libro.getId());

        mensaje("Añadido a favoritos.");
    }

    @FXML
    private void pedirPrestado() {
        Libro libro = listaLibros.getSelectionModel().getSelectedItem();
        if (libro == null) return;

        boolean exito = crearPrestamo(usuarioActual.getId(), libro.getId());
        mensaje(exito ? "Préstamo creado" : "No se pudo crear el préstamo");

        cargarLibros();
    }

    private void mensaje(String texto) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(null);
        alert.setContentText(texto);
        alert.show();
    }

    //DAO
    private Connection conectar() {
        return ConexionBD.getConexion();
    }

    // Obtener todos los libros
    private ObservableList<Libro> obtenerTodosLosLibros() {
        ObservableList<Libro> lista = FXCollections.observableArrayList();

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement("SELECT * FROM libros");
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                Libro libro = new Libro(
                                        rs.getInt("id"),
                                        rs.getString("titulo"),
                                        rs.getString("autor"),
                                        rs.getString("genero"),
                                        rs.getString("isbn"),
                                        rs.getString("descripcion"),
                                        rs.getString("foto"),
                                        rs.getInt("cantidad"),
                                        rs.getBoolean("disponibles")
                                );
                lista.add(libro);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    // Insertar favorito
    private void insertarFavorito(int idUsuario, int idLibro) {
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT INTO favoritos (id_usuario, id_libro) VALUES (?, ?)")) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idLibro);
            ps.executeUpdate();

        } catch (SQLIntegrityConstraintViolationException ignored) {
            mensaje("Ya estaba en favoritos.");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Crear préstamo
    private boolean crearPrestamo(int idUsuario, int idLibro) {
        try (Connection conn = conectar()) {

            // Crear préstamo
            PreparedStatement ps = conn.prepareStatement(
                    "INSERT INTO prestamos (id_usuario, id_libro, fecha_inicio, fecha_fin, estado) " +
                            "VALUES (?, ?, NOW(), DATE_ADD(NOW(), INTERVAL 15 DAY), 'PENDIENTE')");

            ps.setInt(1, idUsuario);
            ps.setInt(2, idLibro);
            int filas = ps.executeUpdate();

            // Restar una copia disponible del libro
            PreparedStatement ps2 = conn.prepareStatement(
                    "UPDATE libros SET copias_disponibles = copias_disponibles - 1 WHERE id = ?");

            ps2.setInt(1, idLibro);
            ps2.executeUpdate();

            return filas > 0;

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }
}