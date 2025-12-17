package com.logiflow.backend.repositorios;

import com.logiflow.backend.modelos.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioVehiculo extends JpaRepository<Vehiculo, Long> {
    Optional<Vehiculo> findByPlaca(String placa);
    List<Vehiculo> findByPropietarioId(Long propietarioId);
    List<Vehiculo> findByEstado(Vehiculo.EstadoVehiculo estado);
    List<Vehiculo> findByTipoAndEstado(Vehiculo.TipoVehiculo tipo, Vehiculo.EstadoVehiculo estado);
}
