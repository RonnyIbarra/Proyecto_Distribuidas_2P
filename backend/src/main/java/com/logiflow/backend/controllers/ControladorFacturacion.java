package com.logiflow.backend.controllers;

import com.logiflow.backend.dto.SolicitudCrearFactura;
import com.logiflow.backend.dto.RespuestaFactura;
import com.logiflow.backend.servicios.ServicioFacturacion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/facturas")
@Slf4j
public class ControladorFacturacion {

    private final ServicioFacturacion servicioFacturacion;

    public ControladorFacturacion(ServicioFacturacion servicioFacturacion) {
        this.servicioFacturacion = servicioFacturacion;
    }

    @PostMapping
    public ResponseEntity<RespuestaFactura> crearFactura(@Valid @RequestBody SolicitudCrearFactura solicitud) {
        log.info("Creando factura para pedido de entrega: {}", solicitud.getIdPedidoEntrega());
        RespuestaFactura respuesta = servicioFacturacion.crearFactura(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @GetMapping("/{id}")
    public ResponseEntity<RespuestaFactura> obtenerPorId(@PathVariable Long id) {
        log.info("Obteniendo factura por id: {}", id);
        RespuestaFactura respuesta = servicioFacturacion.obtenerFacturaPorId(id);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/numero/{numeroFactura}")
    public ResponseEntity<RespuestaFactura> obtenerPorNumero(@PathVariable String numeroFactura) {
        log.info("Obteniendo factura por número: {}", numeroFactura);
        RespuestaFactura respuesta = servicioFacturacion.obtenerFacturaPorNumero(numeroFactura);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/cliente/{idCliente}")
    public ResponseEntity<List<RespuestaFactura>> obtenerPorCliente(@PathVariable Long idCliente) {
        log.info("Obteniendo facturas para cliente: {}", idCliente);
        List<RespuestaFactura> facturas = servicioFacturacion.obtenerFacturasPorCliente(idCliente);
        return ResponseEntity.ok(facturas);
    }

    @GetMapping("/pedido/{idPedido}")
    public ResponseEntity<List<RespuestaFactura>> obtenerPorPedido(@PathVariable Long idPedido) {
        log.info("Obteniendo facturas para pedido: {}", idPedido);
        List<RespuestaFactura> facturas = servicioFacturacion.obtenerFacturasPorPedido(idPedido);
        return ResponseEntity.ok(facturas);
    }

    @PatchMapping("/{id}/estado")
    public ResponseEntity<RespuestaFactura> actualizarEstado(
            @PathVariable Long id,
            @RequestParam String estado) {
        log.info("Actualizando estado de factura {} a: {}", id, estado);
        RespuestaFactura respuesta = servicioFacturacion.actualizarEstadoFactura(id, estado);
        return ResponseEntity.ok(respuesta);
    }
}
