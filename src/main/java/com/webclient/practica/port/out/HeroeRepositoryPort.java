package com.webclient.practica.port.out;

import com.webclient.practica.domain.Heroe;
import java.util.List;
import java.util.Optional;

public interface HeroeRepositoryPort {
    Heroe guardar(Heroe heroe);
    List<Heroe> obtenerTodos();
    Optional<Heroe> obtenerPorId(Long id);
    boolean existePorNombreYApellido(String nombre, String apellido);
    boolean existePorNombreYApellidoYIdDiferente(String nombre, String apellido, Long id);
    void eliminarPorId(Long id);
    List<Heroe> buscarPorEpoca(String epoca);
    List<Heroe> buscarPorMovimiento(String movimiento);
    List<Heroe> buscarPorEstado(String estado);
}
