package com.example.biblioteca_digital.controladores;

import com.example.biblioteca_digital.modelos.Usuario;

import java.util.List;
import java.util.Optional;

public class ControladorAdministrador<T, ID> implements Crud<T, ID>
{

    public void initializeCuenta(Usuario cuenta) {

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
