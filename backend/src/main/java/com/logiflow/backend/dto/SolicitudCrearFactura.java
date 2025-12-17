package com.logiflow.backend.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SolicitudCrearFactura {
    @NotNull(message = "El ID del pedido es requerido")
    private Long idPedidoEntrega;

    @NotNull(message = "El ID del cliente es requerido")
    private Long idCliente;

    @NotNull(message = "El subtotal es requerido")
    @Positive(message = "El subtotal debe ser positivo")
    private BigDecimal subtotal;

    @NotNull(message = "El impuesto es requerido")
    @PositiveOrZero(message = "El impuesto no puede ser negativo")
    private BigDecimal impuesto;

    private String descripcion;
}
