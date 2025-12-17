package com.logiflow.backend.modelos;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "invoices")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Factura {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroFactura;

    @Column(nullable = false)
    private Long idPedidoEntrega;

    @Column(nullable = false)
    private Long idCliente;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoFactura estado = EstadoFactura.BORRADOR;

    @Column(nullable = false)
    private BigDecimal subtotal;

    @Builder.Default
    private BigDecimal impuesto = BigDecimal.ZERO;

    @Column(nullable = false)
    private BigDecimal total;

    private String descripcion;

    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    public enum EstadoFactura {
        BORRADOR,
        EMITIDA,
        PAGADA,
        CANCELADA
    }
}
