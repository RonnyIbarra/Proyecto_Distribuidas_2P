package com.logiflow.backend.gateway;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.logiflow.backend.servicios.ProveedorTokenJwt;

@Service
public class FiltroAutenticacionGateway {

    private final ProveedorTokenJwt proveedorTokenJwt;

    @Autowired
    public FiltroAutenticacionGateway(ProveedorTokenJwt proveedorTokenJwt) {
        this.proveedorTokenJwt = proveedorTokenJwt;
    }

    /**
     * Valida la petición entrante usando JWT.
     * - Libera completamente /api/auth/**
     * - Protege el resto de endpoints
     */
    public void validarPeticion(HttpServletRequest request, String servicio) {

        String path = request.getRequestURI();

        // 🔓 ENDPOINTS PÚBLICOS (LOGIN, REGISTER, TOKEN, VALIDATE)
        if (path.startsWith("/api/auth")) {
            return;
        }

        // 🔐 VALIDACIÓN DE TOKEN
        String header = request.getHeader("Authorization");
        if (header == null || header.isBlank() || !header.startsWith("Bearer ")) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token de autorización ausente o mal formado"
            );
        }

        String token = header.substring(7).trim();

        if (!proveedorTokenJwt.validarToken(token)) {
            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Token inválido o expirado"
            );
        }

        // 🔎 VALIDACIÓN DE ROL (OPCIONAL)
        String rolRequerido = request.getHeader("X-Required-Role");
        if (rolRequerido != null && !rolRequerido.isBlank()) {
            String rolToken = proveedorTokenJwt.obtenerRolDelToken(token);
            if (!rolRequerido.equalsIgnoreCase(rolToken)) {
                throw new ResponseStatusException(
                        HttpStatus.FORBIDDEN,
                        "Rol insuficiente"
                );
            }
        }
    }
}
