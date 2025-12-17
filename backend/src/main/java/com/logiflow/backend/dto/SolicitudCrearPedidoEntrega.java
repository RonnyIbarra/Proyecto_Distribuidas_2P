package com.logiflow.backend.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SolicitudCrearPedidoEntrega {
    @NotNull(message = "El ID del cliente es requerido")
    private Long idCliente;

    @NotBlank(message = "El origen es requerido")
    private String origen;

    @NotBlank(message = "El destino es requerido")
    private String destino;

    @NotNull(message = "El tipo de entrega es requerido")
    private String tipoEntrega; // ENTREGA_URBANA_RAPIDA, ENTREGA_INTERMUNICIPAL, ENTREGA_NACIONAL

    @NotNull(message = "El peso es requerido")
    @Positive(message = "El peso debe ser positivo")
    private Integer peso;

    @NotBlank(message = "La zona es requerida")
    private String zona;

    private String notas;
}
