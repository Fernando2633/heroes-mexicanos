package com.webclient.practica.adapter.out.persistence;

import com.webclient.practica.domain.Heroe;
import com.webclient.practica.port.out.HeroeRepositoryPort;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component("heroePersistenceAdapter")
public class HeroePersistenceAdapter implements HeroeRepositoryPort {

    private final SpringDataHeroeRepository springDataHeroeRepository;

    public HeroePersistenceAdapter(SpringDataHeroeRepository springDataHeroeRepository) {
        this.springDataHeroeRepository = springDataHeroeRepository;
    }

    @Override
    public Heroe guardar(Heroe heroe) {
        HeroeJpaEntity entity = toEntity(heroe);
        HeroeJpaEntity savedEntity = springDataHeroeRepository.save(entity);
        return toDomain(savedEntity);
    }

    @Override
    public List<Heroe> obtenerTodos() {
        return springDataHeroeRepository.findAll()
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Heroe> obtenerPorId(Long id) {
        return springDataHeroeRepository.findById(id)
                .map(this::toDomain);
    }

    @Override
    public boolean existePorNombreYApellido(String nombre, String apellido) {
        return springDataHeroeRepository.existsByNombreIgnoreCaseAndApellidoIgnoreCase(nombre, apellido);
    }

    @Override
    public boolean existePorNombreYApellidoYIdDiferente(String nombre, String apellido, Long id) {
        return springDataHeroeRepository.existsByNombreIgnoreCaseAndApellidoIgnoreCaseAndIdNot(nombre, apellido, id);
    }

    @Override
    public void eliminarPorId(Long id) {
        springDataHeroeRepository.deleteById(id);
    }

    @Override
    public List<Heroe> buscarPorEpoca(String epoca) {
        return springDataHeroeRepository.findByEpocaContainingIgnoreCase(epoca)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Heroe> buscarPorMovimiento(String movimiento) {
        return springDataHeroeRepository.findByMovimientoContainingIgnoreCase(movimiento)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Heroe> buscarPorEstado(String estado) {
        return springDataHeroeRepository.findByEstadoNacimientoContainingIgnoreCase(estado)
                .stream()
                .map(this::toDomain)
                .collect(Collectors.toList());
    }

    private HeroeJpaEntity toEntity(Heroe heroe) {
        return new HeroeJpaEntity(
                heroe.getId(),
                heroe.getNombre(),
                heroe.getApellido(),
                heroe.getFechaNacimiento(),
                heroe.getEstadoNacimiento(),
                heroe.getEpoca(),
                heroe.getMovimiento(),
                heroe.getDescripcion()
        );
    }

    private Heroe toDomain(HeroeJpaEntity entity) {
        return new Heroe(
                entity.getId(),
                entity.getNombre(),
                entity.getApellido(),
                entity.getFechaNacimiento(),
                entity.getEstadoNacimiento(),
                entity.getEpoca(),
                entity.getMovimiento(),
                entity.getDescripcion()
        );
    }
}
