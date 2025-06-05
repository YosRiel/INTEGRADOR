package com.integrador.servimanef.repository;

import com.integrador.servimanef.entity.pedido;
import org.springframework.data.jpa.repository.JpaRepository;

public interface pedidoRepository extends JpaRepository<pedido, Long> {
}
