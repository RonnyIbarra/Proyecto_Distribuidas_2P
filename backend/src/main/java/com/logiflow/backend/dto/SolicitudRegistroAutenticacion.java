package com.logiflow.backend.dto;

import lombok.*;
import jakarta.validation.constraints.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SolicitudRegistroAutenticacion {
    @NotBlank(message = "El correo es requerido")
    @Email(message = "El correo debe ser válido")
    private String correo;

    @NotBlank(message = "La contraseña es requerida")
    @Size(min = 6, message = "La contraseña debe tener al menos 6 caracteres")
    private String contrasena;

    @NotBlank(message = "El nombre completo es requerido")
    private String nombreCompleto;

    @NotBlank(message = "El número de teléfono es requerido")
    @Pattern(regexp = "^\\+?[0-9]{10,}$", message = "El número de teléfono debe ser válido")
    private String telefonoContacto;


}
