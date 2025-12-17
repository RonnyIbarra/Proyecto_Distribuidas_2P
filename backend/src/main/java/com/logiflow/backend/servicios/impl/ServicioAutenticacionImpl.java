package com.logiflow.backend.servicios.impl;

import com.logiflow.backend.dto.SolicitudLoginAutenticacion;
import com.logiflow.backend.dto.SolicitudRegistroAutenticacion;
import com.logiflow.backend.dto.RespuestaTokenAutenticacion;
import com.logiflow.backend.dto.RespuestaUsuario;
import com.logiflow.backend.modelos.Rol;
import com.logiflow.backend.modelos.Usuario;
import com.logiflow.backend.repositorios.RepositorioRol;
import com.logiflow.backend.repositorios.RepositorioUsuario;
import com.logiflow.backend.servicios.ServicioAutenticacion;
import com.logiflow.backend.servicios.ProveedorTokenJwt;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
public class ServicioAutenticacionImpl implements ServicioAutenticacion {

    private final RepositorioUsuario repositorioUsuario;
    private final RepositorioRol repositorioRol;
    private final ProveedorTokenJwt proveedorTokenJwt;
    private final PasswordEncoder codificadorContrasena;

    public ServicioAutenticacionImpl(
            RepositorioUsuario repositorioUsuario,
            RepositorioRol repositorioRol,
            ProveedorTokenJwt proveedorTokenJwt,
            PasswordEncoder codificadorContrasena) {

        this.repositorioUsuario = repositorioUsuario;
        this.repositorioRol = repositorioRol;
        this.proveedorTokenJwt = proveedorTokenJwt;
        this.codificadorContrasena = codificadorContrasena;
    }

    // ========================= LOGIN =========================
    @Override
    @Transactional(readOnly = true)
    public RespuestaTokenAutenticacion iniciarSesion(SolicitudLoginAutenticacion solicitud) {

        log.info("Inicio de sesión para correo: {}", solicitud.getCorreo());

        Usuario usuario = repositorioUsuario.findByCorreo(solicitud.getCorreo())
                .orElseThrow(() ->
                        new RuntimeException("Credenciales inválidas"));

        if (!Boolean.TRUE.equals(usuario.getActivo())) {
            throw new RuntimeException("La cuenta está inactiva");
        }

        if (!codificadorContrasena.matches(
                solicitud.getContrasena(),
                usuario.getContrasena())) {
            throw new RuntimeException("Credenciales inválidas");
        }

        String tokenAcceso = proveedorTokenJwt.generarTokenAcceso(
                usuario.getId(),
                usuario.getRol().getNombre()
        );

        String tokenRecuperacion = proveedorTokenJwt.generarTokenRecuperacion(usuario.getId());

        return RespuestaTokenAutenticacion.builder()
                .tokenAcceso(tokenAcceso)
                .tokenRecuperacion(tokenRecuperacion)
                .expiraEn(86400L)
                .tipoToken("Bearer")
                .usuario(mapearUsuarioARepuesta(usuario))
                .build();
    }

    // ========================= REGISTRO =========================
    @Override
    @Transactional
    public RespuestaTokenAutenticacion registrarse(SolicitudRegistroAutenticacion solicitud) {

        log.info("Registro de usuario con correo: {}", solicitud.getCorreo());

        if (repositorioUsuario.findByCorreo(solicitud.getCorreo()).isPresent()) {
            throw new RuntimeException("El correo ya está registrado");
        }

        // 🔒 Rol fijo por seguridad
        Rol rolUsuario = repositorioRol.findByNombre("USUARIO")
                .orElseThrow(() ->
                        new RuntimeException("Rol USUARIO no configurado en la base de datos"));

        Usuario usuario = Usuario.builder()
                .correo(solicitud.getCorreo())
                .contrasena(codificadorContrasena.encode(solicitud.getContrasena()))
                .nombreCompleto(solicitud.getNombreCompleto())
                .telefonoContacto(solicitud.getTelefonoContacto())
                .rol(rolUsuario)
                .activo(true)
                .build();

        usuario = repositorioUsuario.save(usuario);

        String tokenAcceso = proveedorTokenJwt.generarTokenAcceso(
                usuario.getId(),
                rolUsuario.getNombre()
        );

        String tokenRecuperacion = proveedorTokenJwt.generarTokenRecuperacion(usuario.getId());

        return RespuestaTokenAutenticacion.builder()
                .tokenAcceso(tokenAcceso)
                .tokenRecuperacion(tokenRecuperacion)
                .expiraEn(86400L)
                .tipoToken("Bearer")
                .usuario(mapearUsuarioARepuesta(usuario))
                .build();
    }

    // ========================= REFRESH TOKEN =========================
    @Override
    public RespuestaTokenAutenticacion renovarToken(String tokenRecuperacion) {

        if (!proveedorTokenJwt.validarToken(tokenRecuperacion)) {
            throw new RuntimeException("Token de recuperación inválido");
        }

        Long idUsuario = proveedorTokenJwt.obtenerIdUsuarioDelToken(tokenRecuperacion);

        Usuario usuario = repositorioUsuario.findByIdAndActivo(idUsuario, true)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado o inactivo"));

        String tokenAcceso = proveedorTokenJwt.generarTokenAcceso(
                usuario.getId(),
                usuario.getRol().getNombre()
        );

        return RespuestaTokenAutenticacion.builder()
                .tokenAcceso(tokenAcceso)
                .tokenRecuperacion(tokenRecuperacion)
                .expiraEn(86400L)
                .tipoToken("Bearer")
                .usuario(mapearUsuarioARepuesta(usuario))
                .build();
    }

    // ========================= VALIDACIONES =========================
    @Override
    public boolean validarToken(String token) {
        return proveedorTokenJwt.validarToken(token);
    }

    @Override
    @Transactional(readOnly = true)
    public RespuestaUsuario obtenerUsuarioPorId(Long idUsuario) {

        Usuario usuario = repositorioUsuario.findByIdAndActivo(idUsuario, true)
                .orElseThrow(() ->
                        new RuntimeException("Usuario no encontrado"));

        return mapearUsuarioARepuesta(usuario);
    }

    @Override
    public void cerrarSesion(Long idUsuario) {
        log.info("Cierre de sesión solicitado para usuario {}", idUsuario);
        // Implementar blacklist en Redis si se requiere
    }

    // ========================= MAPPER =========================
    private RespuestaUsuario mapearUsuarioARepuesta(Usuario usuario) {

        return RespuestaUsuario.builder()
                .id(usuario.getId())
                .correo(usuario.getCorreo())
                .nombreCompleto(usuario.getNombreCompleto())
                .telefonoContacto(usuario.getTelefonoContacto())
                .rol(usuario.getRol().getNombre())
                .activo(usuario.getActivo())
                .fechaCreacion(usuario.getFechaCreacion())
                .fechaActualizacion(usuario.getFechaActualizacion())
                .build();
    }
}
