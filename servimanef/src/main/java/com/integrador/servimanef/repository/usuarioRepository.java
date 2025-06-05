package com.integrador.servimanef.repository;

import com.integrador.servimanef.entity.usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface usuarioRepository extends JpaRepository<usuario, Long> {
    usuario findByUsername(String username);
}
