package com.integrador.servimanef.service;

import com.integrador.servimanef.entity.imagen;
import com.integrador.servimanef.repository.imagenRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class imagenService {
    @Autowired
    private imagenRepository imagenRepository;

    public Optional<imagen> buscarPorId(Long id) {
        return imagenRepository.findById(id);
    }

    public imagen guardar(imagen imagen) {
        return imagenRepository.save(imagen);
    }
}
