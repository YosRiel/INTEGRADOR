package com.integrador.servimanef.service;

import com.integrador.servimanef.entity.proforma;
import com.integrador.servimanef.repository.proformaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class proformaService {
    @Autowired
    private proformaRepository proformaRepository;

    public List<proforma> listarTodos() {
        return proformaRepository.findAll();
    }

    public Optional<proforma> buscarPorId(Long id) {
        return proformaRepository.findById(id);
    }

    public proforma guardar(proforma proforma) {
        return proformaRepository.save(proforma);
    }

    public void borrar(Long id) {
        proformaRepository.deleteById(id);
    }

    public long contar() {
        return proformaRepository.count();
    }
}
