package com.logiflow.backend.controllers;

import com.logiflow.backend.dto.SolicitudCrearPedidoEntrega;
import com.logiflow.backend.dto.RespuestaPedidoEntrega;
import com.logiflow.backend.servicios.ServicioPedidoEntrega;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/pedidos")
@Slf4j
public class ControladorPedidoEntrega {

    private final ServicioPedidoEntrega servicioPedidoEntrega;

    public ControladorPedidoEntrega(ServicioPedidoEntrega servicioPedidoEntrega) {
        this.servicioPedidoEntrega = servicioPedidoEntrega;
    }

    @PostMapping
    public ResponseEntity<RespuestaPedidoEntrega> crearPedido(@Valid @RequestBody SolicitudCrearPedidoEntrega solicitud) {
        log.info("Creando pedido de entrega");
        RespuestaPedidoEntrega respuesta = servicioPedidoEntrega.crearPedido(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespuestaPedidoEntrega> obtenerPorId(@PathVariable Long id) {
        log.info("Obteniendo pedido por id: {}", id);
        RespuestaPedidoEntrega respuesta = servicioPedidoEntrega.obtenerPedidoPorId(id);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/numero/{numeroPedido}")
    public ResponseEntity<RespuestaPedidoEntrega> obtenerPorNumero(@PathVariable String numeroPedido) {
        log.info("Obteniendo pedido por número: {}", numeroPedido);
        RespuestaPedidoEntrega respuesta = servicioPedidoEntrega.obtenerPedidoPorNumero(numeroPedido);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<RespuestaPedidoEntrega>> obtenerPorCliente(@PathVariable Long idCliente) {
        log.info("Obteniendo pedidos para cliente: {}", idCliente);
        List<RespuestaPedidoEntrega> pedidos = servicioPedidoEntrega.obtenerPedidosPorCliente(idCliente);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/zona/{zona}")
    public ResponseEntity<List<RespuestaPedidoEntrega>> obtenerPorZona(@PathVariable String zona) {
        log.info("Obteniendo pedidos para zona: {}", zona);
        List<RespuestaPedidoEntrega> pedidos = servicioPedidoEntrega.obtenerPedidosPorZona(zona);
        return ResponseEntity.ok(pedidos);
    }

    @GetMapping("/estado/{estado}")
    public ResponseEntity<List<RespuestaPedidoEntrega>> obtenerPorEstado(@PathVariable String estado) {
        log.info("Obteniendo pedidos por estado: {}", estado);
        List<RespuestaPedidoEntrega> pedidos = servicioPedidoEntrega.obtenerPedidosPorEstado(estado);
        return ResponseEntity.ok(pedidos);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<RespuestaPedidoEntrega> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        log.info("Actualizando estado del pedido {} a: {}", id, estado);
        RespuestaPedidoEntrega respuesta = servicioPedidoEntrega.actualizarEstadoPedido(id, estado);
        return ResponseEntity.ok(respuesta);
    }

    @PatchMapping("/{id}/asignar")
    public ResponseEntity<RespuestaPedidoEntrega> asignarRepartidor(
            @PathVariable Long id,
            @RequestParam Long idRepartidor,
            @RequestParam Long idVehiculo) {
        log.info("Asignando repartidor {} con vehículo {} al pedido {}", idRepartidor, idVehiculo, id);
        RespuestaPedidoEntrega respuesta = servicioPedidoEntrega.asignarRepartidorAPedido(id, idRepartidor, idVehiculo);
        return ResponseEntity.ok(respuesta);
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<RespuestaPedidoEntrega> cancelarPedido(@PathVariable Long id) {
        log.info("Cancelando pedido: {}", id);
        RespuestaPedidoEntrega respuesta = servicioPedidoEntrega.cancelarPedido(id);
        return ResponseEntity.ok(respuesta);
    }
}
