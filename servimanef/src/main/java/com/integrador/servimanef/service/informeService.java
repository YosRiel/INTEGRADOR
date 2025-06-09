package com.integrador.servimanef.service;

import com.integrador.servimanef.entity.informe;
import com.integrador.servimanef.repository.informeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class informeService {
    @Autowired
    private informeRepository informeRepository;

    public List<informe> listarTodos() {
        return informeRepository.findAll();
    }

    public Optional<informe> buscarPorId(Long id) {
        return informeRepository.findById(id);
    }

    public informe guardar(informe informe) {
        return informeRepository.save(informe);
    }

    public void borrar(Long id) {
        informeRepository.deleteById(id);
    }

    public long contar() {
        return informeRepository.count();
    }
}
