package com.example.biblioteca_digital.controladores;

import com.example.biblioteca_digital.controladores.Crud;
import javafx.fxml.FXML;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;

import java.util.List;
import java.util.Optional;



public class ControladorLogin<T, ID> implements Crud<T, ID> {

    @FXML
    private ToggleButton tbt_usuario;

    @FXML
    private ToggleButton tbt_admin;

    @FXML
    private ToggleGroup grupoRol;

    @FXML
    public void initialize() {
        grupoRol = new ToggleGroup();
        tbt_usuario.setToggleGroup(grupoRol);
        tbt_admin.setToggleGroup(grupoRol);
    }

    /**
     * @param entity
     * @return
     */
    @Override
    public T guardar(T entity) {
        return null;
    }

    /**
     * @param id
     * @return
     */
    @Override
    public Optional<T> buscarPorId(ID id) {
        return Optional.empty();
    }

    /**
     * @return
     */
    @Override
    public List<T> listar() {
        return List.of();
    }

    /**
     * @param id
     */
    @Override
    public void eliminarPorId(ID id) {

    }

    /**
     * @param entity
     */
    @Override
    public void eliminar(T entity) {

    }
}

