package com.webclient.practica.adapter.out.persistence;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SpringDataHeroeRepository extends JpaRepository<HeroeJpaEntity, Long> {

    boolean existsByNombreIgnoreCaseAndApellidoIgnoreCase(String nombre, String apellido);

    boolean existsByNombreIgnoreCaseAndApellidoIgnoreCaseAndIdNot(String nombre, String apellido, Long id);

    @Query("SELECT h FROM HeroeJpaEntity h WHERE LOWER(h.epoca) LIKE LOWER(CONCAT('%', :epoca, '%'))")
    List<HeroeJpaEntity> findByEpocaContainingIgnoreCase(@Param("epoca") String epoca);

    @Query("SELECT h FROM HeroeJpaEntity h WHERE LOWER(h.movimiento) LIKE LOWER(CONCAT('%', :movimiento, '%'))")
    List<HeroeJpaEntity> findByMovimientoContainingIgnoreCase(@Param("movimiento") String movimiento);

    @Query("SELECT h FROM HeroeJpaEntity h WHERE LOWER(h.estadoNacimiento) LIKE LOWER(CONCAT('%', :estado, '%'))")
    List<HeroeJpaEntity> findByEstadoNacimientoContainingIgnoreCase(@Param("estado") String estado);
}
