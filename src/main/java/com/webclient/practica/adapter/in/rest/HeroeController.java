package com.webclient.practica.adapter.in.rest;

import com.webclient.practica.adapter.in.rest.dto.HeroeRequestDTO;
import com.webclient.practica.adapter.in.rest.dto.HeroeResponseDTO;
import com.webclient.practica.domain.Heroe;
import com.webclient.practica.port.in.HeroeServicePort;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/heroes")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class HeroeController {

    private final HeroeServicePort heroeServicePort;

    public HeroeController(HeroeServicePort heroeServicePort) {
        this.heroeServicePort = heroeServicePort;
    }

    @PostMapping
    public ResponseEntity<HeroeResponseDTO> registrar(@Valid @RequestBody HeroeRequestDTO requestDTO) {
        Heroe heroe = toDomain(requestDTO);
        Heroe heroeCreado = heroeServicePort.registrar(heroe);
        return ResponseEntity.status(HttpStatus.CREATED).body(HeroeResponseDTO.fromDomain(heroeCreado));
    }

    @GetMapping
    public ResponseEntity<List<HeroeResponseDTO>> obtenerTodos() {
        List<HeroeResponseDTO> lista = heroeServicePort.obtenerTodos()
                .stream()
                .map(HeroeResponseDTO::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/{id}")
    public ResponseEntity<HeroeResponseDTO> obtenerPorId(@PathVariable Long id) {
        Heroe heroe = heroeServicePort.obtenerPorId(id);
        return ResponseEntity.ok(HeroeResponseDTO.fromDomain(heroe));
    }

    @PutMapping("/{id}")
    public ResponseEntity<HeroeResponseDTO> actualizar(@PathVariable Long id, @Valid @RequestBody HeroeRequestDTO requestDTO) {
        Heroe heroe = toDomain(requestDTO);
        Heroe heroeActualizado = heroeServicePort.actualizar(id, heroe);
        return ResponseEntity.ok(HeroeResponseDTO.fromDomain(heroeActualizado));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        heroeServicePort.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/epoca/{epoca}")
    public ResponseEntity<List<HeroeResponseDTO>> buscarPorEpoca(@PathVariable String epoca) {
        List<HeroeResponseDTO> lista = heroeServicePort.buscarPorEpoca(epoca)
                .stream()
                .map(HeroeResponseDTO::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/movimiento/{movimiento}")
    public ResponseEntity<List<HeroeResponseDTO>> buscarPorMovimiento(@PathVariable String movimiento) {
        List<HeroeResponseDTO> lista = heroeServicePort.buscarPorMovimiento(movimiento)
                .stream()
                .map(HeroeResponseDTO::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<HeroeResponseDTO>> buscarPorEstado(@PathVariable String estado) {
        List<HeroeResponseDTO> lista = heroeServicePort.buscarPorEstado(estado)
                .stream()
                .map(HeroeResponseDTO::fromDomain)
                .collect(Collectors.toList());
        return ResponseEntity.ok(lista);
    }

    private Heroe toDomain(HeroeRequestDTO dto) {
        return new Heroe(
                null,
                dto.getNombre(),
                dto.getApellido(),
                dto.getFechaNacimiento(),
                dto.getEstadoNacimiento(),
                dto.getEpoca(),
                dto.getMovimiento(),
                dto.getDescripcion()
        );
    }
}
