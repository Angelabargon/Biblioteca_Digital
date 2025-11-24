package com.example.biblioteca_digital.controladores;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Rol;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ControladorUsuario
{
    /*
    private Usuario usuario;
    public void initializeCuenta(Usuario usuario) {
        this.usuario = usuario;
    }
    Image imagen = new Image(getClass().getResourceAsStream("/imagenes/icono.png"));
    ImageView imageView = new ImageView(imagen);

    // Crear botón y asignar la imagen
    Button boton = new Button();
        boton.setGraphic(imageView);

    // Acción del botón
        boton.setOnAction(e -> System.out.println("¡Botón con imagen presionado!"));

    StackPane root = new StackPane(boton);
    Scene scene = new Scene(root, 300, 200);
        primaryStage.setScene(scene);
        primaryStage.setTitle("ImageButton Demo");
        primaryStage.show();
        */
}
