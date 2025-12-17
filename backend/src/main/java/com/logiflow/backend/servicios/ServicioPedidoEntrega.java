package com.logiflow.backend.servicios;

import com.logiflow.backend.dto.SolicitudCrearPedidoEntrega;
import com.logiflow.backend.dto.RespuestaPedidoEntrega;
import java.util.List;

public interface ServicioPedidoEntrega {
    RespuestaPedidoEntrega crearPedido(SolicitudCrearPedidoEntrega solicitud);
    RespuestaPedidoEntrega obtenerPedidoPorId(Long id);
    RespuestaPedidoEntrega obtenerPedidoPorNumero(String numeroPedido);
    List<RespuestaPedidoEntrega> obtenerPedidosPorCliente(Long idCliente);
    List<RespuestaPedidoEntrega> obtenerPedidosPorZona(String zona);
    List<RespuestaPedidoEntrega> obtenerPedidosPorEstado(String estado);
    RespuestaPedidoEntrega actualizarEstadoPedido(Long idPedido, String estado);
    RespuestaPedidoEntrega asignarRepartidorAPedido(Long idPedido, Long idRepartidor, Long idVehiculo);
    RespuestaPedidoEntrega cancelarPedido(Long idPedido);
}
