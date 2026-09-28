package com.webclient.practica.application;

import com.webclient.practica.domain.Heroe;
import com.webclient.practica.domain.exception.BusinessRuleException;
import com.webclient.practica.domain.exception.HeroeNotFoundException;
import com.webclient.practica.port.in.HeroeServicePort;
import com.webclient.practica.port.out.HeroeRepositoryPort;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class HeroeService implements HeroeServicePort {

    private final HeroeRepositoryPort heroeRepositoryPort;

    // Default constructor injects primary persistence port (HeroePersistenceAdapter or InMemoryHeroeAdapter)
    public HeroeService(@Qualifier("heroePersistenceAdapter") HeroeRepositoryPort heroeRepositoryPort) {
        this.heroeRepositoryPort = heroeRepositoryPort;
    }

    @Override
    public Heroe registrar(Heroe heroe) {
        validarReglasDeNegocio(heroe, false);

        if (heroeRepositoryPort.existePorNombreYApellido(heroe.getNombre().trim(), heroe.getApellido().trim())) {
            throw new BusinessRuleException("Ya existe un héroe registrado con el nombre '" 
                    + heroe.getNombre().trim() + " " + heroe.getApellido().trim() + "'");
        }

        return heroeRepositoryPort.guardar(heroe);
    }

    @Override
    public List<Heroe> obtenerTodos() {
        return heroeRepositoryPort.obtenerTodos();
    }

    @Override
    public Heroe obtenerPorId(Long id) {
        return heroeRepositoryPort.obtenerPorId(id)
                .orElseThrow(() -> new HeroeNotFoundException("Héroe no encontrado con ID: " + id));
    }

    @Override
    public Heroe actualizar(Long id, Heroe heroe) {
        // Regla 9: No se puede modificar un héroe que no exista.
        Heroe heroeExistente = obtenerPorId(id);

        validarReglasDeNegocio(heroe, true);

        if (heroeRepositoryPort.existePorNombreYApellidoYIdDiferente(heroe.getNombre().trim(), heroe.getApellido().trim(), id)) {
            throw new BusinessRuleException("Ya existe otro héroe con el nombre '" 
                    + heroe.getNombre().trim() + " " + heroe.getApellido().trim() + "'");
        }

        heroeExistente.setNombre(heroe.getNombre().trim());
        heroeExistente.setApellido(heroe.getApellido().trim());
        heroeExistente.setFechaNacimiento(heroe.getFechaNacimiento());
        heroeExistente.setEstadoNacimiento(heroe.getEstadoNacimiento().trim());
        heroeExistente.setEpoca(heroe.getEpoca().trim());
        heroeExistente.setMovimiento(heroe.getMovimiento().trim());
        heroeExistente.setDescripcion(heroe.getDescripcion() != null ? heroe.getDescripcion().trim() : "");

        return heroeRepositoryPort.guardar(heroeExistente);
    }

    @Override
    public void eliminar(Long id) {
        // Regla 10: No se puede eliminar un héroe que no exista.
        obtenerPorId(id);
        heroeRepositoryPort.eliminarPorId(id);
    }

    @Override
    public List<Heroe> buscarPorEpoca(String epoca) {
        if (epoca == null || epoca.trim().isEmpty()) {
            return obtenerTodos();
        }
        return heroeRepositoryPort.buscarPorEpoca(epoca.trim());
    }

    @Override
    public List<Heroe> buscarPorMovimiento(String movimiento) {
        if (movimiento == null || movimiento.trim().isEmpty()) {
            return obtenerTodos();
        }
        return heroeRepositoryPort.buscarPorMovimiento(movimiento.trim());
    }

    @Override
    public List<Heroe> buscarPorEstado(String estado) {
        if (estado == null || estado.trim().isEmpty()) {
            return obtenerTodos();
        }
        return heroeRepositoryPort.buscarPorEstado(estado.trim());
    }

    private void validarReglasDeNegocio(Heroe heroe, boolean esActualizacion) {
        if (heroe == null) {
            throw new BusinessRuleException("Los datos del héroe no pueden ser nulos");
        }
        // Regla 1: El nombre es obligatorio.
        if (heroe.getNombre() == null || heroe.getNombre().trim().isEmpty()) {
            throw new BusinessRuleException("El nombre es obligatorio");
        }
        // Regla 2: El apellido es obligatorio.
        if (heroe.getApellido() == null || heroe.getApellido().trim().isEmpty()) {
            throw new BusinessRuleException("El apellido es obligatorio");
        }
        // Regla 3: La fecha de nacimiento es obligatoria.
        if (heroe.getFechaNacimiento() == null) {
            throw new BusinessRuleException("La fecha de nacimiento es obligatoria");
        }
        // Regla 4: La fecha de nacimiento no puede ser posterior a la fecha actual.
        if (heroe.getFechaNacimiento().isAfter(LocalDate.now())) {
            throw new BusinessRuleException("La fecha de nacimiento no puede ser posterior a la fecha actual");
        }
        // Regla 6: El estado de nacimiento es obligatorio.
        if (heroe.getEstadoNacimiento() == null || heroe.getEstadoNacimiento().trim().isEmpty()) {
            throw new BusinessRuleException("El estado de nacimiento es obligatorio");
        }
        // Regla 7: La época histórica es obligatoria.
        if (heroe.getEpoca() == null || heroe.getEpoca().trim().isEmpty()) {
            throw new BusinessRuleException("La época histórica es obligatoria");
        }
        // Regla 8: El movimiento es obligatorio.
        if (heroe.getMovimiento() == null || heroe.getMovimiento().trim().isEmpty()) {
            throw new BusinessRuleException("El movimiento es obligatorio");
        }
    }
}
