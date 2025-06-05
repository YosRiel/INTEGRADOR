package com.integrador.servimanef.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController {

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/intranet")
    public String intranet() {
        return "/intranet";
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