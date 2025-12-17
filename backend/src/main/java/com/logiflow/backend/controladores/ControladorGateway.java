package com.logiflow.backend.controladores;

import java.io.IOException;
import java.util.Enumeration;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.logiflow.backend.gateway.FiltroAutenticacionGateway;
import com.logiflow.backend.gateway.ServicioLimitadorTasa;

@RestController
@RequestMapping("/api")
public class ControladorGateway {

    private final RestTemplate restTemplate;
    private final FiltroAutenticacionGateway filtro;
    private final ServicioLimitadorTasa limitador;

    @Autowired
    public ControladorGateway(FiltroAutenticacionGateway filtro, ServicioLimitadorTasa limitador) {
        this.restTemplate = new RestTemplate();
        this.filtro = filtro;
        this.limitador = limitador;
    }

    @RequestMapping("/{servicio}/**")
    public ResponseEntity<byte[]> proxy(@PathVariable String servicio, HttpServletRequest request) throws IOException {
        String uri = request.getRequestURI();
        // Forward path: keep everything after /api
        String forwardPath = uri.substring("/api".length());
        String target = "http://localhost:8081" + forwardPath;

        // Autenticación y autorización via filtro
        filtro.validarPeticion(request, servicio);

        // Rate limiting por X-API-Key o sub del token
        String apiKey = request.getHeader("X-API-Key");
        if (!limitador.permitir(apiKey)) {
            return ResponseEntity.status(429).body("Rate limit exceeded".getBytes());
        }

        // Copiar headers
        HttpHeaders headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                Enumeration<String> values = request.getHeaders(name);
                while (values.hasMoreElements()) {
                    headers.add(name, values.nextElement());
                }
            }
        }

        HttpMethod method = HttpMethod.valueOf(request.getMethod());
        byte[] body = StreamUtils.copyToByteArray(request.getInputStream());
        HttpEntity<byte[]> entity = new HttpEntity<>(body, headers);

        ResponseEntity<byte[]> response = restTemplate.exchange(target, method, entity, byte[].class);
        return ResponseEntity.status(response.getStatusCode()).headers(response.getHeaders()).body(response.getBody());
    }
}
