package com.logiflow.backend.modelos;

import lombok.*;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "delivery_orders")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PedidoEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String numeroPedido;

    @Column(nullable = false)
    private Long idCliente;

    @Column(nullable = false)
    private String origen;

    @Column(nullable = false)
    private String destino;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoEntrega tipoEntrega;

    @Column(nullable = false)
    private Integer peso;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private EstadoPedido estado = EstadoPedido.RECIBIDO;

    private Long idRepartidorAsignado;

    private Long idVehiculoAsignado;

    private String zona;

    private BigDecimal costEstimado;

    private String notas;

    @Builder.Default
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Builder.Default
    private LocalDateTime fechaActualizacion = LocalDateTime.now();

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = LocalDateTime.now();
    }

    public enum TipoEntrega {
        ENTREGA_URBANA_RAPIDA,
        ENTREGA_INTERMUNICIPAL,
        ENTREGA_NACIONAL
    }

    public enum EstadoPedido {
        RECIBIDO,
        CONFIRMADO,
        ASIGNADO,
        EN_TRANSITO,
        ENTREGADO,
        CANCELADO
    }
}
