package com.integrador.servimanef.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class proforma {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(name = "fecha_creacion")
    private LocalDateTime fechaCreacion = LocalDateTime.now();

    @Column(name = "pedido_id")
    private Long pedidoId;

    @Column(name = "contenido_informe", columnDefinition = "TEXT")
    private String contenidoInforme;

    @Column(name = "informe_id")
    private Long informeId;

    @Column(name = "descripcion_servicio")
    private String descripcionServicio;

    @Column(name = "valor_servicio")
    private Double valorServicio;
}
