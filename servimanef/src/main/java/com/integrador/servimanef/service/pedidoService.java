package com.integrador.servimanef.service;

import com.integrador.servimanef.entity.pedido;
import com.integrador.servimanef.repository.pedidoRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class pedidoService {
    @Autowired
    private pedidoRepository pedidoRepository;

    public List<pedido> listarTodos() {
        return pedidoRepository.findAll();
    }

    public Optional<pedido> buscarPorId(Long id) {
        return pedidoRepository.findById(id);
    }

    public pedido guardar(pedido pedido) {
        return pedidoRepository.save(pedido);
    }

    public void borrar(Long id) {
        pedidoRepository.deleteById(id);
    }
}
