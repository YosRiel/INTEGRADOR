package com.integrador.servimanef.controller;

import com.integrador.servimanef.entity.imagen;
import com.integrador.servimanef.entity.informe;
import com.integrador.servimanef.entity.grupo;
import com.integrador.servimanef.entity.pedido;
import com.integrador.servimanef.entity.usuario;
import com.integrador.servimanef.repository.imagenRepository;
import com.integrador.servimanef.repository.informeRepository;
import com.integrador.servimanef.repository.grupoRepository;
import com.integrador.servimanef.repository.pedidoRepository;
import com.integrador.servimanef.repository.usuarioRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.List;

@Controller
public class PageController {

    @Autowired
    private usuarioRepository usuarioRepository;

    @Autowired
    private pedidoRepository pedidoRepository;

    @Autowired
    private informeRepository informeRepository;

    @Autowired
    private grupoRepository grupoRepository;

    @Autowired
    private imagenRepository imagenRepository;

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
    public String informes(Model model) {
        model.addAttribute("informes", informeRepository.findAll());
        return "informes";
    }

    @PostMapping("/informes/crear")
    public String crearInforme(@RequestParam String nombre, RedirectAttributes redirectAttributes) {
        // Transformar el nombre: mayúsculas y reemplazar espacios por "_"
        String nombreTransformado = nombre.toUpperCase().replace(" ", "_");

        // Obtener el último informe para calcular el siguiente número
        Long count = informeRepository.count() + 1;
        String membrete = String.format("SM-%04d", count);

        informe informe = new informe();
        informe.setNombre(membrete + "-" + nombreTransformado);
        informeRepository.save(informe);

        redirectAttributes.addFlashAttribute("mensaje", "Nuevo informe creado. Ingrese los grupos.");
        redirectAttributes.addFlashAttribute("informeId", informe.getId());
        return "redirect:/informes/" + informe.getId() + "/grupos";
    }

    // Ejemplo para mostrar grupos de un informe (opcional)
    @GetMapping("/informes/{id}/grupos")
    public String mostrarFormularioGrupo(@PathVariable Long id, Model model) {
        informe informe = informeRepository.findById(id).orElseThrow();
        List<grupo> grupos = grupoRepository.findByInformeId(id);
        model.addAttribute("informe", informe);
        model.addAttribute("grupos", grupos);
        return "grupos";
    }

    @PostMapping("/informes/{id}/grupos/crear")
    public String crearGrupo(@PathVariable Long id,
                        @RequestParam String nombreGrupo,
                        @RequestParam(required = false) String descripcion,
                        @RequestParam("imagenes") MultipartFile[] imagenes,
                        @RequestParam Integer cantidadImagenes,
                        RedirectAttributes redirectAttributes) throws IOException {
        informe informe = informeRepository.findById(id).orElseThrow();

        grupo grupo = new grupo();
        grupo.setInforme(informe);
        grupo.setNombreGrupo(nombreGrupo);
        grupo.setDescripcion(descripcion);
        grupo.setCantidadImagenes(cantidadImagenes);

        grupo = grupoRepository.save(grupo);

        // Guardar imágenes en la base de datos
        for (MultipartFile file : imagenes) {
            if (!file.isEmpty()) {
                imagen imagen = new imagen();
                imagen.setDatos(file.getBytes());
                imagen.setTipo(file.getContentType());
                imagen.setGrupo(grupo);
                imagenRepository.save(imagen);
            }
        }

        redirectAttributes.addFlashAttribute("mensaje", "Grupo agregado correctamente.");
        return "redirect:/informes/" + id + "/grupos";
    }

    @GetMapping("/proforma")
    public String proforma() {
        return "proforma";
    }

    @GetMapping("/recepcion")
    public String recepcion(Model model) {
        model.addAttribute("recepciones", pedidoRepository.findAll());
        return "recepcion";
    }

    @PostMapping("/pedido")
    public String registrarPedido(@ModelAttribute pedido pedido) {
        pedidoRepository.save(pedido);
        return "redirect:/"; // Redirige a la página principal o donde prefieras
    }

    @GetMapping("/informes/{id}/editar")
    public String editarInforme(@PathVariable Long id, Model model) {
        informe informe = informeRepository.findById(id).orElseThrow();
        List<grupo> grupos = grupoRepository.findByInformeId(id);
        List<informe> informes = informeRepository.findAll(); // Para el historial
        model.addAttribute("informeEditar", informe);
        model.addAttribute("gruposEditar", grupos);
        model.addAttribute("informes", informes);
        return "informes";
    }

    @PostMapping("/informes/{id}/actualizar")
    public String actualizarInforme(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("mensaje", "Informe actualizado correctamente.");
        return "redirect:/informes";
    }

    @PostMapping("/informes/{id}/borrar")
    public String borrarInforme(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        informeRepository.deleteById(id);
        redirectAttributes.addFlashAttribute("mensaje", "Informe borrado correctamente.");
        return "redirect:/informes";
    }

    @RestController
    public class ImagenController {

        @Autowired
        private imagenRepository imagenRepository;

        @GetMapping("/imagen/{id}")
        public ResponseEntity<byte[]> verImagen(@PathVariable Long id) {
            imagen imagen = imagenRepository.findById(id).orElse(null);
            if (imagen == null) {
                return ResponseEntity.notFound().build();
            }
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(imagen.getTipo()));
            return new ResponseEntity<>(imagen.getDatos(), headers, HttpStatus.OK);
        }
    }
}