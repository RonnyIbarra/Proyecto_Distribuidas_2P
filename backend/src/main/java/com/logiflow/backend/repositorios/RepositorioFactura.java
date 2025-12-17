package com.logiflow.backend.repositorios;

import com.logiflow.backend.modelos.Factura;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface RepositorioFactura extends JpaRepository<Factura, Long> {
    Optional<Factura> findByNumeroFactura(String numeroFactura);
    List<Factura> findByIdCliente(Long idCliente);
    List<Factura> findByIdPedidoEntrega(Long idPedido);
    List<Factura> findByEstado(Factura.EstadoFactura estado);
}
