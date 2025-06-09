package com.integrador.servimanef.service;

import com.integrador.servimanef.entity.grupo;
import com.integrador.servimanef.repository.grupoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class grupoService {
    @Autowired
    private grupoRepository grupoRepository;

    public List<grupo> listarPorInformeId(Long informeId) {
        return grupoRepository.findByInformeId(informeId);
    }

    public grupo guardar(grupo grupo) {
        return grupoRepository.save(grupo);
    }
}
