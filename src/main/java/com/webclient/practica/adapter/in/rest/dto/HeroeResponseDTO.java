package com.webclient.practica.adapter.in.rest.dto;

import com.webclient.practica.domain.Heroe;

import java.time.LocalDate;

public class HeroeResponseDTO {

    private Long id;
    private String nombre;
    private String apellido;
    private LocalDate fechaNacimiento;
    private String estadoNacimiento;
    private String epoca;
    private String movimiento;
    private String descripcion;

    public HeroeResponseDTO() {
    }

    public HeroeResponseDTO(Long id, String nombre, String apellido, LocalDate fechaNacimiento, String estadoNacimiento, String epoca, String movimiento, String descripcion) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.fechaNacimiento = fechaNacimiento;
        this.estadoNacimiento = estadoNacimiento;
        this.epoca = epoca;
        this.movimiento = movimiento;
        this.descripcion = descripcion;
    }

    public static HeroeResponseDTO fromDomain(Heroe heroe) {
        if (heroe == null) return null;
        return new HeroeResponseDTO(
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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getEstadoNacimiento() {
        return estadoNacimiento;
    }

    public void setEstadoNacimiento(String estadoNacimiento) {
        this.estadoNacimiento = estadoNacimiento;
    }

    public String getEpoca() {
        return epoca;
    }

    public void setEpoca(String epoca) {
        this.epoca = epoca;
    }

    public String getMovimiento() {
        return movimiento;
    }

    public void setMovimiento(String movimiento) {
        this.movimiento = movimiento;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }
}
