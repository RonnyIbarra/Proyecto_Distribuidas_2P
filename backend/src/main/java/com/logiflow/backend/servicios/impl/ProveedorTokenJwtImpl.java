package com.logiflow.backend.servicios.impl;

import com.logiflow.backend.servicios.ProveedorTokenJwt;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Service
@Slf4j
public class ProveedorTokenJwtImpl implements ProveedorTokenJwt {

    @Value("${app.jwt.secret:MiClaveSecretaParaGeneracionYValidacionDeTokenesJWTConLongitudMinimaDeTreintayDosCaracteres}")
    private String claveSecreta;

    @Value("${app.jwt.expiration:86400000}")
    private long tiempoExpiracionMs;

    @Value("${app.jwt.refresh-expiration:604800000}")
    private long tiempoExpiracionRecuperacionMs;

    @Override
    public String generarTokenAcceso(Long idUsuario, String rol) {
        SecretKey clave = Keys.hmacShaKeyFor(claveSecreta.getBytes());
        return Jwts.builder()
                .subject(idUsuario.toString())
                .claim("rol", rol)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tiempoExpiracionMs))
                .signWith(clave)
                .compact();
    }

    @Override
    public String generarTokenRecuperacion(Long idUsuario) {
        SecretKey clave = Keys.hmacShaKeyFor(claveSecreta.getBytes());
        return Jwts.builder()
                .subject(idUsuario.toString())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + tiempoExpiracionRecuperacionMs))
                .signWith(clave)
                .compact();
    }

    @Override
    public Long obtenerIdUsuarioDelToken(String token) {
        SecretKey clave = Keys.hmacShaKeyFor(claveSecreta.getBytes());
        try {
            Claims reclamaciones = Jwts.parser()
                    .verifyWith(clave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return Long.parseLong(reclamaciones.getSubject());
        } catch (JwtException e) {
            log.error("Error al extraer ID del usuario del token: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public String obtenerRolDelToken(String token) {
        SecretKey clave = Keys.hmacShaKeyFor(claveSecreta.getBytes());
        try {
            Claims reclamaciones = Jwts.parser()
                    .verifyWith(clave)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
            return reclamaciones.get("rol", String.class);
        } catch (JwtException e) {
            log.error("Error al extraer rol del token: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean validarToken(String token) {
        SecretKey clave = Keys.hmacShaKeyFor(claveSecreta.getBytes());
        try {
            Jwts.parser()
                    .verifyWith(clave)
                    .build()
                    .parseSignedClaims(token);
            return true;
        } catch (ExpiredJwtException e) {
            log.error("Token JWT expirado");
            return false;
        } catch (UnsupportedJwtException e) {
            log.error("Token JWT no soportado");
            return false;
        } catch (MalformedJwtException e) {
            log.error("Token JWT inválido");
            return false;
        } catch (SignatureException e) {
            log.error("Validación de firma JWT fallida");
            return false;
        } catch (IllegalArgumentException e) {
            log.error("Cadena de reclamaciones JWT vacía");
            return false;
        }
    }
}
