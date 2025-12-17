package com.logiflow.backend.repositorios;

import com.logiflow.backend.modelos.PedidoEntrega;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioPedidoEntrega extends JpaRepository<PedidoEntrega, Long> {
    Optional<PedidoEntrega> findByNumeroPedido(String numeroPedido);
    List<PedidoEntrega> findByIdCliente(Long idCliente);
    List<PedidoEntrega> findByIdRepartidorAsignado(Long repartidorId);
    List<PedidoEntrega> findByZona(String zona);
    List<PedidoEntrega> findByEstado(PedidoEntrega.EstadoPedido estado);
    List<PedidoEntrega> findByZonaAndEstado(String zona, PedidoEntrega.EstadoPedido estado);
}
