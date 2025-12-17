package com.logiflow.backend.servicios.impl;

import com.logiflow.backend.dto.SolicitudCrearFactura;
import com.logiflow.backend.dto.RespuestaFactura;
import com.logiflow.backend.modelos.Factura;
import com.logiflow.backend.repositorios.RepositorioFactura;
import com.logiflow.backend.servicios.ServicioFacturacion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ServicioFacturacionImpl implements ServicioFacturacion {

    private final RepositorioFactura repositorioFactura;

    public ServicioFacturacionImpl(RepositorioFactura repositorioFactura) {
        this.repositorioFactura = repositorioFactura;
    }

    @Override
    @Transactional
    public RespuestaFactura crearFactura(SolicitudCrearFactura solicitud) {
        log.info("Creando factura para pedido de entrega: {}", solicitud.getIdPedidoEntrega());

        String numeroFactura = "FCT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        BigDecimal total = solicitud.getSubtotal().add(solicitud.getImpuesto());

        Factura factura = Factura.builder()
                .numeroFactura(numeroFactura)
                .idPedidoEntrega(solicitud.getIdPedidoEntrega())
                .idCliente(solicitud.getIdCliente())
                .estado(Factura.EstadoFactura.BORRADOR)
                .subtotal(solicitud.getSubtotal())
                .impuesto(solicitud.getImpuesto())
                .total(total)
                .descripcion(solicitud.getDescripcion())
                .build();

        factura = repositorioFactura.save(factura);
        log.info("Factura creada con número: {}", factura.getNumeroFactura());

        return mapearFacturaARepuesta(factura);
    }

    @Override
    @Transactional(readOnly = true)
    public RespuestaFactura obtenerFacturaPorId(Long id) {
        Factura factura = repositorioFactura.findById(id)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con id: " + id));
        return mapearFacturaARepuesta(factura);
    }

    @Override
    @Transactional(readOnly = true)
    public RespuestaFactura obtenerFacturaPorNumero(String numeroFactura) {
        Factura factura = repositorioFactura.findByNumeroFactura(numeroFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada con número: " + numeroFactura));
        return mapearFacturaARepuesta(factura);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaFactura> obtenerFacturasPorCliente(Long idCliente) {
        return repositorioFactura.findByIdCliente(idCliente)
                .stream()
                .map(this::mapearFacturaARepuesta)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaFactura> obtenerFacturasPorPedido(Long idPedido) {
        return repositorioFactura.findByIdPedidoEntrega(idPedido)
                .stream()
                .map(this::mapearFacturaARepuesta)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RespuestaFactura actualizarEstadoFactura(Long idFactura, String estado) {
        log.info("Actualizando estado de factura {} a: {}", idFactura, estado);

        Factura factura = repositorioFactura.findById(idFactura)
                .orElseThrow(() -> new RuntimeException("Factura no encontrada"));

        factura.setEstado(Factura.EstadoFactura.valueOf(estado));
        factura = repositorioFactura.save(factura);

        return mapearFacturaARepuesta(factura);
    }

    private RespuestaFactura mapearFacturaARepuesta(Factura factura) {
        return RespuestaFactura.builder()
                .id(factura.getId())
                .numeroFactura(factura.getNumeroFactura())
                .idPedidoEntrega(factura.getIdPedidoEntrega())
                .idCliente(factura.getIdCliente())
                .estado(factura.getEstado().name())
                .subtotal(factura.getSubtotal())
                .impuesto(factura.getImpuesto())
                .total(factura.getTotal())
                .descripcion(factura.getDescripcion())
                .fechaCreacion(factura.getFechaCreacion())
                .fechaActualizacion(factura.getFechaActualizacion())
                .build();
    }
}
