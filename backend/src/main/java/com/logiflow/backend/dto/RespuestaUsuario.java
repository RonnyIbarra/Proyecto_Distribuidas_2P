package com.logiflow.backend.dto;

import lombok.*;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RespuestaUsuario {
    private Long id;
    private String correo;
    private String nombreCompleto;
    private String telefonoContacto;
    private String rol;
    private Boolean activo;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaActualizacion;
}
