package com.logiflow.backend.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RespuestaTokenAutenticacion {
    private String tokenAcceso;
    private String tokenRecuperacion;
    private Long expiraEn;
    private String tipoToken;
    private RespuestaUsuario usuario;
}
