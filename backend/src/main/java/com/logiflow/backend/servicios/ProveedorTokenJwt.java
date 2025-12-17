package com.logiflow.backend.servicios;

public interface ProveedorTokenJwt {
    String generarTokenAcceso(Long idUsuario, String rol);
    String generarTokenRecuperacion(Long idUsuario);
    Long obtenerIdUsuarioDelToken(String token);
    String obtenerRolDelToken(String token);
    boolean validarToken(String token);
}
