package com.integrador.servimanef.controller;

import com.integrador.servimanef.entity.usuario;
import com.integrador.servimanef.repository.usuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
public class PageController {

    @Autowired
    private usuarioRepository usuarioRepository;

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/intranet")
    public String intranet() {
        return "intranet";
    }

    @PostMapping("/intranet/login")
    public String login(@RequestParam String user,
                        @RequestParam String password,
                        Model model) {
        usuario usuario = usuarioRepository.findByUsername(user);
        if (usuario != null && usuario.getPassword().equals(password)) {
            // Login exitoso
            return "redirect:/main_menu";
        } else {
            // Login fallido
            model.addAttribute("loginError", true);
            return "intranet";
        }
    }

    @GetMapping("/main_menu")
    public String mainMenu() {
        return "main_menu";
    }

    @GetMapping("/informes")
    public String informes() {
        return "informes";
    }

    @GetMapping("/proforma")
    public String proforma() {
        return "proforma";
    }

    @GetMapping("/recepcion")
    public String recepcion() {
        return "recepcion";
    }
}