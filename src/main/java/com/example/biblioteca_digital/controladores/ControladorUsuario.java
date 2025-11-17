package com.example.biblioteca_digital.controladores;

import com.example.biblioteca_digital.conexion.ConexionBD;
import com.example.biblioteca_digital.modelos.Rol;
import com.example.biblioteca_digital.modelos.Sesion;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class ControladorUsuario
{

    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        usuarioActual = Sesion.getUsuario();
    }

}
