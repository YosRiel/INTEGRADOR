package com.integrador.servimanef.repository;

import com.integrador.servimanef.entity.grupo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface grupoRepository extends JpaRepository<grupo, Long> {
    List<grupo> findByInformeId(Long informeId);
}
