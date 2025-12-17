package com.logiflow.backend.servicios;

import com.logiflow.backend.dto.SolicitudCrearVehiculo;
import com.logiflow.backend.dto.RespuestaVehiculo;
import java.util.List;

public interface ServicioFlota {
    RespuestaVehiculo crearVehiculo(SolicitudCrearVehiculo solicitud);
    RespuestaVehiculo obtenerVehiculoPorId(Long id);
    RespuestaVehiculo obtenerVehiculoPorPlaca(String placa);
    List<RespuestaVehiculo> obtenerVehiculosPorPropietario(Long idPropietario);
    List<RespuestaVehiculo> obtenerVehiculosDisponibles();
    List<RespuestaVehiculo> obtenerVehiculosPorTipo(String tipo);
    RespuestaVehiculo actualizarEstadoVehiculo(Long idVehiculo, String estado);
    void eliminarVehiculo(Long idVehiculo);
}
