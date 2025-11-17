package com.example.biblioteca_digital.controladores;

import com.example.biblioteca_digital.modelos.Sesion;
import com.example.biblioteca_digital.modelos.Usuario;
import javafx.fxml.FXML;

import java.util.List;
import java.util.Optional;

public class ControladorAdministrador
{

    private Usuario usuarioActual;

    @FXML
    public void initialize() {
        usuarioActual = Sesion.getUsuario();
    }
}
