package com.integrador.servimanef.entity;

import jakarta.persistence.*;
import lombok.Data;

@Entity
@Data
public class pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String cliente;
    private String servicio;
    private String telefono;
    private String empresa;
    private String ruc;
}
