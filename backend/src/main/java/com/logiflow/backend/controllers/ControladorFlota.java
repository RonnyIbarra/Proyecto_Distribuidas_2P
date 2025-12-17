package com.logiflow.backend.controllers;

import com.logiflow.backend.dto.SolicitudCrearVehiculo;
import com.logiflow.backend.dto.RespuestaVehiculo;
import com.logiflow.backend.servicios.ServicioFlota;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/fleet")
@Slf4j
public class ControladorFlota {

    private final ServicioFlota servicioFlota;

    public ControladorFlota(ServicioFlota servicioFlota) {
        this.servicioFlota = servicioFlota;
    }

    @PostMapping("/vehicles")
    public ResponseEntity<RespuestaVehiculo> crearVehiculo(@Valid @RequestBody SolicitudCrearVehiculo solicitud) {
        log.info("Creando vehículo con placa: {}", solicitud.getPlaca());
        RespuestaVehiculo respuesta = servicioFlota.crearVehiculo(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/vehicles/{id}")
    public ResponseEntity<RespuestaVehiculo> obtenerPorId(@PathVariable Long id) {
        log.info("Obteniendo vehículo por id: {}", id);
        RespuestaVehiculo respuesta = servicioFlota.obtenerVehiculoPorId(id);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/vehicles/placa/{placa}")
    public ResponseEntity<RespuestaVehiculo> obtenerPorPlaca(@PathVariable String placa) {
        log.info("Obteniendo vehículo por placa: {}", placa);
        RespuestaVehiculo respuesta = servicioFlota.obtenerVehiculoPorPlaca(placa);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/vehicles/propietario/{idPropietario}")
    public ResponseEntity<List<RespuestaVehiculo>> obtenerPorPropietario(@PathVariable Long idPropietario) {
        log.info("Obteniendo vehículos para propietario: {}", idPropietario);
        List<RespuestaVehiculo> vehiculos = servicioFlota.obtenerVehiculosPorPropietario(idPropietario);
        return ResponseEntity.ok(vehiculos);
    }

    @GetMapping("/vehicles/disponibles")
    public ResponseEntity<List<RespuestaVehiculo>> obtenerDisponibles() {
        log.info("Obteniendo vehículos disponibles");
        List<RespuestaVehiculo> vehiculos = servicioFlota.obtenerVehiculosDisponibles();
        return ResponseEntity.ok(vehiculos);
    }

    @GetMapping("/vehicles/tipo/{tipo}")
    public ResponseEntity<List<RespuestaVehiculo>> obtenerPorTipo(@PathVariable String tipo) {
        log.info("Obteniendo vehículos por tipo: {}", tipo);
        List<RespuestaVehiculo> vehiculos = servicioFlota.obtenerVehiculosPorTipo(tipo);
        return ResponseEntity.ok(vehiculos);
    }

    @PatchMapping("/vehicles/{id}/estado")
    public ResponseEntity<RespuestaVehiculo> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        log.info("Actualizando estado del vehículo {} a: {}", id, estado);
        RespuestaVehiculo respuesta = servicioFlota.actualizarEstadoVehiculo(id, estado);
        return ResponseEntity.ok(respuesta);
    }

    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<Void> eliminarVehiculo(@PathVariable Long id) {
        log.info("Eliminando vehículo: {}", id);
        servicioFlota.eliminarVehiculo(id);
        return ResponseEntity.noContent().build();
    }
}
