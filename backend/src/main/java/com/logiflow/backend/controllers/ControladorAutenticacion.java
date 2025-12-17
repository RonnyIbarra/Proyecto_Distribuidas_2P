package com.logiflow.backend.controllers;

import com.logiflow.backend.dto.SolicitudLoginAutenticacion;
import com.logiflow.backend.dto.SolicitudRegistroAutenticacion;
import com.logiflow.backend.dto.RespuestaTokenAutenticacion;
import com.logiflow.backend.servicios.ServicioAutenticacion;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/auth")
@Slf4j
public class ControladorAutenticacion {

    private final ServicioAutenticacion servicioAutenticacion;

    public ControladorAutenticacion(ServicioAutenticacion servicioAutenticacion) {
        this.servicioAutenticacion = servicioAutenticacion;
    }

    @PostMapping("/login")
    public ResponseEntity<RespuestaTokenAutenticacion> iniciarSesion(@Valid @RequestBody SolicitudLoginAutenticacion solicitud) {
        log.info("Endpoint de inicio de sesión llamado para correo: {}", solicitud.getCorreo());
        RespuestaTokenAutenticacion respuesta = servicioAutenticacion.iniciarSesion(solicitud);
        return ResponseEntity.ok(respuesta);
    }

    @PostMapping("/register")
    public ResponseEntity<RespuestaTokenAutenticacion> registrarse(@Valid @RequestBody SolicitudRegistroAutenticacion solicitud) {
        log.info("Endpoint de registro llamado para correo: {}", solicitud.getCorreo());
        RespuestaTokenAutenticacion respuesta = servicioAutenticacion.registrarse(solicitud);
        return ResponseEntity.status(HttpStatus.CREATED).body(respuesta);
    }

    @PostMapping("/token/refresh")
    public ResponseEntity<RespuestaTokenAutenticacion> renovarToken(@RequestParam String tokenRecuperacion) {
        log.info("Endpoint de renovación de token llamado");
        RespuestaTokenAutenticacion respuesta = servicioAutenticacion.renovarToken(tokenRecuperacion);
        return ResponseEntity.ok(respuesta);
    }

    @GetMapping("/validate")
    public ResponseEntity<Boolean> validarToken(@RequestParam String token) {
        boolean esValido = servicioAutenticacion.validarToken(token);
        return ResponseEntity.ok(esValido);
    }
}
