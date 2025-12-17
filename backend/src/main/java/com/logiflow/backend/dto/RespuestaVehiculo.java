package com.logiflow.backend.dto;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RespuestaVehiculo {
    private Long id;
    private String placa;
    private String tipo;
    private Integer capacidad;
    private String estado;
    private BigDecimal costoPorKm;
    private Long propietarioId;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
