package com.logiflow.backend.servicios;

import com.logiflow.backend.dto.SolicitudCrearFactura;
import com.logiflow.backend.dto.RespuestaFactura;
import java.util.List;

public interface ServicioFacturacion {
    RespuestaFactura crearFactura(SolicitudCrearFactura solicitud);
    RespuestaFactura obtenerFacturaPorId(Long id);
    RespuestaFactura obtenerFacturaPorNumero(String numeroFactura);
    List<RespuestaFactura> obtenerFacturasPorCliente(Long idCliente);
    List<RespuestaFactura> obtenerFacturasPorPedido(Long idPedido);
    RespuestaFactura actualizarEstadoFactura(Long idFactura, String estado);
}
