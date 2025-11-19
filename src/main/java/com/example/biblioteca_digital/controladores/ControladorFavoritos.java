package com.example.biblioteca_digital.controladores;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Libro;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class ControladorFavoritos {

    @FXML
    private ListView<Libro> listaFavoritos;
    @FXML
    private Label tituloLabel;
    @FXML
    private Label autorLabel;
    @FXML
    private Label categoriaLabel;
    @FXML
    private Button btnEliminar;

    private Usuario usuarioActual;

    public void inicializar(Usuario usuario) {
        this.usuarioActual = usuario;
        cargarFavoritos();
    }

    // Controladores
    private void cargarFavoritos() {
        List<Libro> libros = obtenerFavoritos(usuarioActual.getId());
        listaFavoritos.setItems(FXCollections.observableArrayList(libros));

        listaFavoritos.setOnMouseClicked(e -> mostrarDetalles(
                listaFavoritos.getSelectionModel().getSelectedItem()
        ));
    }

    private void mostrarDetalles(Libro libro) {
        if (libro == null) return;

        tituloLabel.setText(libro.getTitulo());
        autorLabel.setText(libro.getAutor());
        categoriaLabel.setText(libro.getGenero());
    }

    @FXML
    private void eliminarFavorito() {
        Libro libro = listaFavoritos.getSelectionModel().getSelectedItem();
        if (libro == null) return;

        borrarFavorito(usuarioActual.getId(), libro.getId());
        mensaje("Favorito eliminado");
        cargarFavoritos();
    }

    private void mensaje(String t) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setHeaderText(null);
        a.setContentText(t);
        a.show();
    }

    //DAO
    private Connection conectar() {
        return ConexionBD.getConexion();
    }

    private List<Libro> obtenerFavoritos(int idUsuario) {
        List<Libro> lista = new ArrayList<>();

        String sql = """
                SELECT l.* FROM libros l 
                JOIN favoritos f ON l.id = f.id_libro
                WHERE f.id_usuario = ?
                """;

        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, idUsuario);

            ResultSet rs = ps.executeQuery();

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
                        rs.getBoolean("disponible")
                );
                lista.add(libro);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return lista;
    }

    private void borrarFavorito(int idUsuario, int idLibro) {
        try (Connection conn = conectar();
             PreparedStatement ps = conn.prepareStatement(
                     "DELETE FROM favoritos WHERE id_usuario = ? AND id_libro = ?")) {

            ps.setInt(1, idUsuario);
            ps.setInt(2, idLibro);
            ps.executeUpdate();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
