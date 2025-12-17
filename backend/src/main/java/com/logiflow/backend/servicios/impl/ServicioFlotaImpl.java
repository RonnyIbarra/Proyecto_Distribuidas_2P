package com.logiflow.backend.servicios.impl;

import com.logiflow.backend.dto.SolicitudCrearVehiculo;
import com.logiflow.backend.dto.RespuestaVehiculo;
import com.logiflow.backend.modelos.Vehiculo;
import com.logiflow.backend.repositorios.RepositorioVehiculo;
import com.logiflow.backend.servicios.ServicioFlota;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
public class ServicioFlotaImpl implements ServicioFlota {

    private final RepositorioVehiculo repositorioVehiculo;

    public ServicioFlotaImpl(RepositorioVehiculo repositorioVehiculo) {
        this.repositorioVehiculo = repositorioVehiculo;
    }

    @Override
    @Transactional
    public RespuestaVehiculo crearVehiculo(SolicitudCrearVehiculo solicitud) {
        log.info("Creando vehículo con placa: {}", solicitud.getPlaca());

        if (repositorioVehiculo.findByPlaca(solicitud.getPlaca()).isPresent()) {
            throw new RuntimeException("Ya existe un vehículo con esta placa: " + solicitud.getPlaca());
        }

        Vehiculo.TipoVehiculo tipoVehiculo = Vehiculo.TipoVehiculo.valueOf(solicitud.getTipo());

        Vehiculo vehiculo = Vehiculo.builder()
                .placa(solicitud.getPlaca())
                .tipo(tipoVehiculo)
                .capacidad(solicitud.getCapacidad())
                .estado(Vehiculo.EstadoVehiculo.DISPONIBLE)
                .costoPorKm(solicitud.getCostoPorKm())
                .propietarioId(solicitud.getPropietarioId())
                .build();

        vehiculo = repositorioVehiculo.save(vehiculo);
        log.info("Vehículo creado con id: {}", vehiculo.getId());

        return mapearVehiculoARepuesta(vehiculo);
    }

    @Override
    @Transactional(readOnly = true)
    public RespuestaVehiculo obtenerVehiculoPorId(Long id) {
        Vehiculo vehiculo = repositorioVehiculo.findById(id)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con id: " + id));
        return mapearVehiculoARepuesta(vehiculo);
    }

    @Override
    @Transactional(readOnly = true)
    public RespuestaVehiculo obtenerVehiculoPorPlaca(String placa) {
        Vehiculo vehiculo = repositorioVehiculo.findByPlaca(placa)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado con placa: " + placa));
        return mapearVehiculoARepuesta(vehiculo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaVehiculo> obtenerVehiculosPorPropietario(Long idPropietario) {
        return repositorioVehiculo.findByPropietarioId(idPropietario)
                .stream()
                .map(this::mapearVehiculoARepuesta)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaVehiculo> obtenerVehiculosDisponibles() {
        return repositorioVehiculo.findByEstado(Vehiculo.EstadoVehiculo.DISPONIBLE)
                .stream()
                .map(this::mapearVehiculoARepuesta)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<RespuestaVehiculo> obtenerVehiculosPorTipo(String tipo) {
        Vehiculo.TipoVehiculo tipoVehiculo = Vehiculo.TipoVehiculo.valueOf(tipo);
        return repositorioVehiculo.findByTipoAndEstado(tipoVehiculo, Vehiculo.EstadoVehiculo.DISPONIBLE)
                .stream()
                .map(this::mapearVehiculoARepuesta)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public RespuestaVehiculo actualizarEstadoVehiculo(Long idVehiculo, String estado) {
        log.info("Actualizando estado del vehículo {} a: {}", idVehiculo, estado);

        Vehiculo vehiculo = repositorioVehiculo.findById(idVehiculo)
                .orElseThrow(() -> new RuntimeException("Vehículo no encontrado"));

        vehiculo.setEstado(Vehiculo.EstadoVehiculo.valueOf(estado));
        vehiculo = repositorioVehiculo.save(vehiculo);

        return mapearVehiculoARepuesta(vehiculo);
    }

    @Override
    @Transactional
    public void eliminarVehiculo(Long idVehiculo) {
        log.info("Eliminando vehículo: {}", idVehiculo);
        repositorioVehiculo.deleteById(idVehiculo);
    }

    private RespuestaVehiculo mapearVehiculoARepuesta(Vehiculo vehiculo) {
        return RespuestaVehiculo.builder()
                .id(vehiculo.getId())
                .placa(vehiculo.getPlaca())
                .tipo(vehiculo.getTipo().name())
                .capacidad(vehiculo.getCapacidad())
                .estado(vehiculo.getEstado().name())
                .costoPorKm(vehiculo.getCostoPorKm())
                .propietarioId(vehiculo.getPropietarioId())
                .fechaCreacion(vehiculo.getFechaCreacion())
                .fechaActualizacion(vehiculo.getFechaActualizacion())
                .build();
    }
}
