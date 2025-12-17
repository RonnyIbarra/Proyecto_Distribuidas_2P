package com.logiflow.backend.dto;

import lombok.*;
import jakarta.validation.constraints.*;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SolicitudCrearVehiculo {
    @NotBlank(message = "La placa es requerida")
    private String placa;

    @NotNull(message = "El tipo de vehículo es requerido")
    private String tipo; // MOTOCICLETA, VEHICULO_LIVIANO, CAMION_MEDIANO, CAMION_PESADO

    @NotNull(message = "La capacidad es requerida")
    @Positive(message = "La capacidad debe ser positiva")
    private Integer capacidad;

    @NotNull(message = "El costo por km es requerido")
    @Positive(message = "El costo debe ser positivo")
    private BigDecimal costoPorKm;

    @NotNull(message = "El ID del propietario es requerido")
    private Long propietarioId;
}
