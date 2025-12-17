package com.logiflow.backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RespuestaPedidoEntrega {
    private Long id;
    private String numeroPedido;
    private Long idCliente;
    private String origen;
    private String destino;
    private String tipoEntrega;
    private Integer peso;
    private String estado;
    private Long idRepartidorAsignado;
    private Long idVehiculoAsignado;
    private String zona;
    private BigDecimal costEstimado;
    private String notas;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
