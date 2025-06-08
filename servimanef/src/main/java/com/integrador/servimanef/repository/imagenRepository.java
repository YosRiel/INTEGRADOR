package com.integrador.servimanef.repository;

import com.integrador.servimanef.entity.imagen;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface imagenRepository extends JpaRepository<imagen, Long> {
    List<imagen> findByGrupoId(Long grupoId);
}
