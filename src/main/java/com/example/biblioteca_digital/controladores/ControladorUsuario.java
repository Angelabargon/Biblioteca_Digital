package com.example.biblioteca_digital.controladores;

import java.util.List;
import java.util.Optional;

public class ControladorUsuario<T, ID> implements Crud<T, ID>
{

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
