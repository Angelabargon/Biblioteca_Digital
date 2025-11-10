package com.example.biblioteca_digital.controladores;

import java.util.List;
import java.util.Optional;

public interface Crud<T, ID>
{
    /**
     * @param entity
     * @return
     */
    T guardar(T entity);
    /**
     * @param id
     * @return
     */
    Optional<T> buscarPorId(ID id);
    /**
     * @return
     */
    List<T> listar();
    /**
     * @param id
     */
    void eliminarPorId(ID id);
    /**
     * @param entity
     */
    void eliminar(T entity);
}
