package com.logiflow.backend.servicios.impl;

import com.logiflow.backend.dto.SolicitudCrearPedidoEntrega;
import com.logiflow.backend.dto.RespuestaPedidoEntrega;
import com.logiflow.backend.modelos.PedidoEntrega;
import com.logiflow.backend.repositorios.RepositorioPedidoEntrega;
import com.logiflow.backend.servicios.ServicioPedidoEntrega;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ServicioPedidoEntregaImpl implements ServicioPedidoEntrega {

    private final RepositorioPedidoEntrega repositorioPedidoEntrega;

    public ServicioPedidoEntregaImpl(RepositorioPedidoEntrega repositorioPedidoEntrega) {
        this.repositorioPedidoEntrega = repositorioPedidoEntrega;
    }

    @Override
    @Transactional
    public RespuestaPedidoEntrega crearPedido(SolicitudCrearPedidoEntrega solicitud) {
        log.info("Creando pedido de entrega para cliente: {}", solicitud.getIdCliente());

        // Validar tipo de entrega
        PedidoEntrega.TipoEntrega tipoEntrega = PedidoEntrega.TipoEntrega.valueOf(solicitud.getTipoEntrega());

        // Validar cobertura de zona
        validarZona(solicitud.getZona());

        // Calcular costo estimado
        BigDecimal costoEstimado = calcularCostoEstimado(tipoEntrega, solicitud.getPeso());

        String numeroPedido = "PED-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        PedidoEntrega pedido = PedidoEntrega.builder()
                .numeroPedido(numeroPedido)
                .idCliente(solicitud.getIdCliente())
                .origen(solicitud.getOrigen())
                .destino(solicitud.getDestino())
                .tipoEntrega(tipoEntrega)
                .peso(solicitud.getPeso())
                .estado(PedidoEntrega.EstadoPedido.RECIBIDO)
                .zona(solicitud.getZona())
                .costEstimado(costoEstimado)
                .notas(solicitud.getNotas())
                .build();

        pedido = repositorioPedidoEntrega.save(pedido);
        log.info("Pedido de entrega creado con número: {}", pedido.getNumeroPedido());

        return mapearPedidoARepuesta(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public RespuestaPedidoEntrega obtenerPedidoPorId(Long id) {
        PedidoEntrega pedido = repositorioPedidoEntrega.findById(id)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con id: " + id));
        return mapearPedidoARepuesta(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public RespuestaPedidoEntrega obtenerPedidoPorNumero(String numeroPedido) {
        PedidoEntrega pedido = repositorioPedidoEntrega.findByNumeroPedido(numeroPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado con número: " + numeroPedido));
        return mapearPedidoARepuesta(pedido);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaPedidoEntrega> obtenerPedidosPorCliente(Long idCliente) {
        return repositorioPedidoEntrega.findByIdCliente(idCliente)
                .stream()
                .map(this::mapearPedidoARepuesta)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaPedidoEntrega> obtenerPedidosPorZona(String zona) {
        return repositorioPedidoEntrega.findByZona(zona)
                .stream()
                .map(this::mapearPedidoARepuesta)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaPedidoEntrega> obtenerPedidosPorEstado(String estado) {
        PedidoEntrega.EstadoPedido estadoPedido = PedidoEntrega.EstadoPedido.valueOf(estado);
        return repositorioPedidoEntrega.findByEstado(estadoPedido)
                .stream()
                .map(this::mapearPedidoARepuesta)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RespuestaPedidoEntrega actualizarEstadoPedido(Long idPedido, String estado) {
        log.info("Actualizando estado del pedido {} a: {}", idPedido, estado);

        PedidoEntrega pedido = repositorioPedidoEntrega.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(PedidoEntrega.EstadoPedido.valueOf(estado));
        pedido = repositorioPedidoEntrega.save(pedido);

        return mapearPedidoARepuesta(pedido);
    }

    @Override
    @Transactional
    public RespuestaPedidoEntrega asignarRepartidorAPedido(Long idPedido, Long idRepartidor, Long idVehiculo) {
        log.info("Asignando repartidor {} con vehículo {} al pedido {}", idRepartidor, idVehiculo, idPedido);

        PedidoEntrega pedido = repositorioPedidoEntrega.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setIdRepartidorAsignado(idRepartidor);
        pedido.setIdVehiculoAsignado(idVehiculo);
        pedido.setEstado(PedidoEntrega.EstadoPedido.ASIGNADO);
        pedido = repositorioPedidoEntrega.save(pedido);

        return mapearPedidoARepuesta(pedido);
    }

    @Override
    @Transactional
    public RespuestaPedidoEntrega cancelarPedido(Long idPedido) {
        log.info("Cancelando pedido: {}", idPedido);

        PedidoEntrega pedido = repositorioPedidoEntrega.findById(idPedido)
                .orElseThrow(() -> new RuntimeException("Pedido no encontrado"));

        pedido.setEstado(PedidoEntrega.EstadoPedido.CANCELADO);
        pedido = repositorioPedidoEntrega.save(pedido);

        return mapearPedidoARepuesta(pedido);
    }

    private BigDecimal calcularCostoEstimado(PedidoEntrega.TipoEntrega tipo, Integer peso) {
        // Cálculo básico: costo base + factor de peso
        BigDecimal costBase = switch (tipo) {
            case ENTREGA_URBANA_RAPIDA -> new BigDecimal("2.50");
            case ENTREGA_INTERMUNICIPAL -> new BigDecimal("5.00");
            case ENTREGA_NACIONAL -> new BigDecimal("10.00");
        };

        BigDecimal factorPeso = new BigDecimal(peso).multiply(new BigDecimal("0.10"));
        return costBase.add(factorPeso);
    }

    private void validarZona(String zona) {
        // Validación básica de zona - expandir con áreas de cobertura reales
        String[] zonasValidas = {"QUITO NORTE", "QUITO CENTRO", "QUITO SUR", "QUITO VALLE", "AMBATO", "LATACUNGA", "PICHINCHA"};
        boolean valida = false;
        for (String zonaValida : zonasValidas) {
            if (zonaValida.equalsIgnoreCase(zona)) {
                valida = true;
                break;
            }
        }
        if (!valida) {
            throw new RuntimeException("Zona no cubierta: " + zona);
        }
    }

    private RespuestaPedidoEntrega mapearPedidoARepuesta(PedidoEntrega pedido) {
        return RespuestaPedidoEntrega.builder()
                .id(pedido.getId())
                .numeroPedido(pedido.getNumeroPedido())
                .idCliente(pedido.getIdCliente())
                .origen(pedido.getOrigen())
                .destino(pedido.getDestino())
                .tipoEntrega(pedido.getTipoEntrega().name())
                .peso(pedido.getPeso())
                .estado(pedido.getEstado().name())
                .idRepartidorAsignado(pedido.getIdRepartidorAsignado())
                .idVehiculoAsignado(pedido.getIdVehiculoAsignado())
                .zona(pedido.getZona())
                .costEstimado(pedido.getCostEstimado())
                .notas(pedido.getNotas())
                .fechaCreacion(pedido.getFechaCreacion())
                .fechaActualizacion(pedido.getFechaActualizacion())
                .build();
    }
}
