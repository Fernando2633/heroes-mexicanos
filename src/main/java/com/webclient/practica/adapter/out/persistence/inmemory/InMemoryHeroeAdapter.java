package com.webclient.practica.adapter.out.persistence.inmemory;

import com.webclient.practica.domain.Heroe;
import com.webclient.practica.port.out.HeroeRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.*;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Component("inMemoryHeroeAdapter")
public class InMemoryHeroeAdapter implements HeroeRepositoryPort {

    private final Map<Long, Heroe> heroeStore = new HashMap<>();
    private final AtomicLong idSequence = new AtomicLong(100);

    @Override
    public Heroe guardar(Heroe heroe) {
        if (heroe.getId() == null) {
            heroe.setId(idSequence.incrementAndGet());
        }
        heroeStore.put(heroe.getId(), heroe);
        return heroe;
    }

    @Override
    public List<Heroe> obtenerTodos() {
        return new ArrayList<>(heroeStore.values());
    }

    @Override
    public Optional<Heroe> obtenerPorId(Long id) {
        return Optional.ofNullable(heroeStore.get(id));
    }

    @Override
    public boolean existePorNombreYApellido(String nombre, String apellido) {
        return heroeStore.values().stream()
                .anyMatch(h -> h.getNombre().equalsIgnoreCase(nombre) && h.getApellido().equalsIgnoreCase(apellido));
    }

    @Override
    public boolean existePorNombreYApellidoYIdDiferente(String nombre, String apellido, Long id) {
        return heroeStore.values().stream()
                .anyMatch(h -> !h.getId().equals(id) && h.getNombre().equalsIgnoreCase(nombre) && h.getApellido().equalsIgnoreCase(apellido));
    }

    @Override
    public void eliminarPorId(Long id) {
        heroeStore.remove(id);
    }

    @Override
    public List<Heroe> buscarPorEpoca(String epoca) {
        return heroeStore.values().stream()
                .filter(h -> h.getEpoca() != null && h.getEpoca().toLowerCase().contains(epoca.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Heroe> buscarPorMovimiento(String movimiento) {
        return heroeStore.values().stream()
                .filter(h -> h.getMovimiento() != null && h.getMovimiento().toLowerCase().contains(movimiento.toLowerCase()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Heroe> buscarPorEstado(String estado) {
        return heroeStore.values().stream()
                .filter(h -> h.getEstadoNacimiento() != null && h.getEstadoNacimiento().toLowerCase().contains(estado.toLowerCase()))
                .collect(Collectors.toList());
    }
}
