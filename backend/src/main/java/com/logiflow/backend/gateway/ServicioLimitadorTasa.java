package com.logiflow.backend.gateway;

import java.time.Instant;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

@Service
public class ServicioLimitadorTasa {

    private final Map<String, Deque<Long>> historial = new ConcurrentHashMap<>();
    private final int limitePorVentana;
    private final long ventanaMillis;

    public ServicioLimitadorTasa() {
        // Por defecto: 60 solicitudes por minuto
        this.limitePorVentana = 60;
        this.ventanaMillis = 60_000L;
    }

    public boolean permitir(String llaveCliente) {
        if (llaveCliente == null) llaveCliente = "__anon__";
        long ahora = Instant.now().toEpochMilli();
        Deque<Long> deque = historial.computeIfAbsent(llaveCliente, k -> new ArrayDeque<>());

        synchronized (deque) {
            // Eliminar timestamps fuera de ventana
            while (!deque.isEmpty() && deque.peekFirst() + ventanaMillis <= ahora) {
                deque.pollFirst();
            }
            if (deque.size() >= limitePorVentana) {
                return false;
            }
            deque.addLast(ahora);
            return true;
        }
    }
}
