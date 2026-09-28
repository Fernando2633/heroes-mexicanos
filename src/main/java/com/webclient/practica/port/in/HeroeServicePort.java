package com.webclient.practica.port.in;

import com.webclient.practica.domain.Heroe;
import java.util.List;

public interface HeroeServicePort {
    Heroe registrar(Heroe heroe);
    List<Heroe> obtenerTodos();
    Heroe obtenerPorId(Long id);
    Heroe actualizar(Long id, Heroe heroe);
    void eliminar(Long id);
    List<Heroe> buscarPorEpoca(String epoca);
    List<Heroe> buscarPorMovimiento(String movimiento);
    List<Heroe> buscarPorEstado(String estado);
}
