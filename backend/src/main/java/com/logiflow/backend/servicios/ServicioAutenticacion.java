package com.logiflow.backend.servicios;

import com.logiflow.backend.dto.SolicitudLoginAutenticacion;
import com.logiflow.backend.dto.SolicitudRegistroAutenticacion;
import com.logiflow.backend.dto.RespuestaTokenAutenticacion;
import com.logiflow.backend.dto.RespuestaUsuario;

public interface ServicioAutenticacion {
    RespuestaTokenAutenticacion iniciarSesion(SolicitudLoginAutenticacion solicitud);
    RespuestaTokenAutenticacion registrarse(SolicitudRegistroAutenticacion solicitud);
    RespuestaTokenAutenticacion renovarToken(String tokenRecuperacion);
    RespuestaUsuario obtenerUsuarioPorId(Long idUsuario);
    void cerrarSesion(Long idUsuario);
    boolean validarToken(String token);
}
