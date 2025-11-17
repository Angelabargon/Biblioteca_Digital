package com.example.biblioteca_digital.controladores;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ControladorMenuUsuario {

    private Usuario usuarioActual;

    private List<Libro> libros = new ArrayList<>();
    private List<Integer> favoritos = new ArrayList<>();
    private int prestamosActivos = 0;

    //Vista
    @FXML private Label labelBienvenida;
    @FXML private Label labelContadorFavoritos;
    @FXML private Label labelContadorPrestamos;
    @FXML private FlowPane contenedorLibros;
    @FXML private TextField filtroTitulo;
    @FXML private TextField filtroAutor;
    @FXML private ComboBox<String> filtroGenero;
    @FXML private ToggleButton toggleFavoritos;

    //Metodo principal
    public void setUsuario(Usuario usuario) {
        this.usuarioActual = usuario;

        //Bienvenida
        labelBienvenida.setText("Bienvenido, " + usuario.getNombre());

        //Cargar datos
        libros = obtenerTodosLosLibros();
        favoritos = obtenerFavoritosUsuario(usuario.getId());
        prestamosActivos = contarPrestamosActivos(usuario.getId());

        labelContadorFavoritos.setText(String.valueOf(favoritos.size()));
        labelContadorPrestamos.setText(String.valueOf(prestamosActivos));

        mostrarLibrosFiltrados();
    }


    //LIBROS
    private List<Libro> obtenerTodosLosLibros() {
        List<Libro> lista = new ArrayList<>();

        String sql = "SELECT * FROM libros";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
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

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }

    //FAVORITOS
    private List<Integer> obtenerFavoritosUsuario(int idUsuario) {
        List<Integer> lista = new ArrayList<>();
        String sql = "SELECT id_libro FROM favoritos WHERE id_usuario = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, idUsuario);
            ResultSet rs = pst.executeQuery();

            while (rs.next()) {
                lista.add(rs.getInt("id_libro"));
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return lista;
    }

    //Añadir y quitar favoritos
    private void alternarFavorito(int idLibro) {
        if (favoritos.contains(idLibro)) {
            eliminarFavorito(idLibro);
        } else {
            insertarFavorito(idLibro);
        }
        favoritos = obtenerFavoritosUsuario(usuarioActual.getId());
        labelContadorFavoritos.setText(String.valueOf(favoritos.size()));
        mostrarLibrosFiltrados();
    }

    private void insertarFavorito(int idLibro) {
        String sql = "INSERT INTO favoritos (id_usuario, id_libro) VALUES (?, ?)";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, usuarioActual.getId());
            pst.setInt(2, idLibro);
            pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void eliminarFavorito(int idLibro) {
        String sql = "DELETE FROM favoritos WHERE id_usuario = ? AND id_libro = ?";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, usuarioActual.getId());
            pst.setInt(2, idLibro);
            pst.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //PRESTAMOS
    private int contarPrestamosActivos(int idUsuario) {
        String sql = "SELECT COUNT(*) FROM prestamos WHERE id_usuario = ? AND estado = 'activo'";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, idUsuario);
            ResultSet rs = pst.executeQuery();

            if (rs.next()) return rs.getInt(1);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return 0;
    }

    private void pedirPrestamo(int idLibro) {
        String sql = "INSERT INTO prestamos (id_usuario, id_libro, fecha_inicio, fecha_fin, estado) " +
                "VALUES (?, ?, CURRENT_DATE, DATE_ADD(CURRENT_DATE, INTERVAL 15 DAY), 'activo')";

        try (Connection con = ConexionBD.getConexion();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, usuarioActual.getId());
            pst.setInt(2, idLibro);
            pst.executeUpdate();

            prestamosActivos++;
            labelContadorPrestamos.setText(String.valueOf(prestamosActivos));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //Mostrar Libros
    @FXML
    private void mostrarLibrosFiltrados() {
        contenedorLibros.getChildren().clear();

        String titulo = filtroTitulo.getText().toLowerCase();
        String autor = filtroAutor.getText().toLowerCase();
        String genero = filtroGenero.getValue() != null ? filtroGenero.getValue().toLowerCase() : "";

        boolean soloFavoritos = toggleFavoritos.isSelected();

        for (Libro libro : libros) {

            boolean coincide = libro.getTitulo().toLowerCase().contains(titulo)
                    && libro.getAutor().toLowerCase().contains(autor)
                    && (genero.isEmpty() || libro.getGenero().toLowerCase().equals(genero))
                    && (!soloFavoritos || favoritos.contains(libro.getId()));

            if (!coincide) continue;

            contenedorLibros.getChildren().add(crearVistaLibroItem(libro));
        }
    }


    //Item del Libro (para que sea mas visual)
    private Parent crearVistaLibroItem(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Libro-Item.fxml"));
            Parent item = loader.load();

            ControladorVistaLibroItem controlador = loader.getController();
            controlador.setDatos(libro, favoritos.contains(libro.getId()), this);

            return item;

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    // Método llamado desde VistaLibroItem
    public void clickFavorito(Libro libro) {
        alternarFavorito(libro.getId());
    }

    public void clickPedirPrestamo(Libro libro) {
        pedirPrestamo(libro.getId());
    }

    public void clickVer(Libro libro) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/biblioteca_digital/vistas/Vista-Libro-Usuario.fxml"));
            Parent root = loader.load();

            ControladorLibros controlador = loader.getController();
            controlador.setLibro(libro, usuarioActual);

            Stage stage = new Stage();
            stage.setTitle(libro.getTitulo());
            stage.setScene(new javafx.scene.Scene(root));
            stage.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

