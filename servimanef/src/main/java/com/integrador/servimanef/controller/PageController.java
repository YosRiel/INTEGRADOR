package com.integrador.servimanef.controller;

import com.integrador.servimanef.entity.imagen;
import com.integrador.servimanef.entity.informe;
import com.integrador.servimanef.entity.grupo;
import com.integrador.servimanef.entity.pedido;
import com.integrador.servimanef.entity.usuario;
import com.integrador.servimanef.entity.proforma;
import com.integrador.servimanef.repository.imagenRepository;
import com.integrador.servimanef.repository.informeRepository;
import com.integrador.servimanef.repository.grupoRepository;
import com.integrador.servimanef.repository.pedidoRepository;
import com.integrador.servimanef.repository.proformaRepository;
import com.integrador.servimanef.repository.usuarioRepository;
import com.lowagie.text.Document;
import com.lowagie.text.Paragraph;
import com.lowagie.text.pdf.PdfWriter;
import com.lowagie.text.FontFactory;
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
        String nombreTransformado = nombre.toUpperCase().replace(" ", "-");

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

    @Controller
    public class ProformaController {

        @Autowired
        private proformaRepository proformaRepository;
        @Autowired
        private pedidoRepository pedidoRepository;
        @Autowired
        private informeRepository informeRepository;

        @GetMapping("/proforma")
        public String listarProformas(@RequestParam(value = "editar", required = false) Long editarId, Model model, RedirectAttributes redirectAttributes) {
            try {
                model.addAttribute("proformas", proformaRepository.findAll());
                model.addAttribute("pedidos", pedidoRepository.findAll());
                model.addAttribute("informes", informeRepository.findAll());
                if (editarId != null) {
                    proforma proformaEditar = proformaRepository.findById(editarId).orElse(null);
                    model.addAttribute("proformaEditar", proformaEditar);
                }
                return "proforma";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("mensaje", "Error al cargar proformas: " + e.getMessage());
                return "redirect:/";
            }
        }

        @PostMapping("/proforma/crear")
        public String crearProforma(
            @RequestParam String nombre,
            @RequestParam Long recepcionId,
            @RequestParam(required = false) String contenidoInforme,
            @RequestParam(required = false) Long informeId,
            @RequestParam(required = false) String descripcionServicio,
            @RequestParam(required = false) Double valorServicio,
            RedirectAttributes redirectAttributes
        ) {
            try {
                Long count = proformaRepository.count() + 1;
                String numero = String.format("%03d", count);
                String anio = String.valueOf(java.time.Year.now().getValue());
                String nombreFormateado = nombre.toUpperCase().replace(" ", "-");
                String membrete = numero + "-" + anio + "-" + nombreFormateado;

                proforma proforma = new proforma();
                proforma.setNombre(membrete);
                proforma.setPedidoId(recepcionId);
                proforma.setContenidoInforme(contenidoInforme);
                proforma.setInformeId(informeId);
                proforma.setDescripcionServicio(descripcionServicio);
                proforma.setValorServicio(valorServicio);
                proformaRepository.save(proforma);

                pedido pedido = pedidoRepository.findById(recepcionId).orElseThrow();
                pedido.setEstado("Finalizado");
                pedidoRepository.save(pedido);
                redirectAttributes.addFlashAttribute("mensaje", "Proforma creada correctamente.");
                return "redirect:/proforma";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("mensaje", "Error al crear proforma: " + e.getMessage());
                return "redirect:/proforma";
            }
        }

        @GetMapping("/proforma/{id}/pdf")
        public void descargarPdf(@PathVariable Long id, jakarta.servlet.http.HttpServletResponse response) {
            try {
                proforma proforma = proformaRepository.findById(id).orElseThrow();
                pedido pedido = null;
                if (proforma.getPedidoId() != null) {
                    pedido = pedidoRepository.findById(proforma.getPedidoId()).orElse(null);
                }

                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=" + proforma.getNombre() + ".pdf");
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);

                Document document = new Document();
                PdfWriter.getInstance(document, response.getOutputStream());
                document.open();

                Paragraph encabezado = new Paragraph("SERVIMANEF E.I.R.L.", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
                encabezado.setAlignment(Paragraph.ALIGN_CENTER);
                document.add(encabezado);

                document.add(new Paragraph(" "));
                document.add(new Paragraph("PROFORMA", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 16)));
                document.add(new Paragraph("N°: " + proforma.getNombre()));
                document.add(new Paragraph("Fecha: " + java.time.LocalDate.now()));
                document.add(new Paragraph(" "));

                if (pedido != null) {
                    document.add(new Paragraph("Cliente: " + pedido.getCliente()));
                    document.add(new Paragraph("Servicio: " + pedido.getServicio()));
                    document.add(new Paragraph("Empresa: " + pedido.getEmpresa()));
                    document.add(new Paragraph("RUC: " + pedido.getRuc()));
                    document.add(new Paragraph("Teléfono: " + pedido.getTelefono()));
                    document.add(new Paragraph(" "));
                }

                if (proforma.getContenidoInforme() != null && !proforma.getContenidoInforme().isEmpty()) {
                    document.add(new Paragraph("Contenido del Informe:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                    document.add(new Paragraph(proforma.getContenidoInforme()));
                    document.add(new Paragraph(" "));
                }

                if (proforma.getDescripcionServicio() != null && !proforma.getDescripcionServicio().isEmpty()) {
                    document.add(new Paragraph("Detalle del Servicio:", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                    document.add(new Paragraph(proforma.getDescripcionServicio()));
                    document.add(new Paragraph(" "));
                }

                if (proforma.getValorServicio() != null) {
                    document.add(new Paragraph("Valor del Servicio: S/ " + proforma.getValorServicio(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                    document.add(new Paragraph(" "));
                }

                Paragraph pie = new Paragraph("SERVIMANEF E.I.R.L. - Todos los derechos reservados © " + java.time.Year.now(), FontFactory.getFont(FontFactory.HELVETICA, 10));
                pie.setAlignment(Paragraph.ALIGN_CENTER);
                document.add(pie);

                document.close();
            } catch (Exception e) {
                try {
                    response.reset();
                    response.setContentType("text/plain");
                    response.getWriter().write("Error al generar el PDF: " + e.getMessage());
                } catch (IOException ioException) {
                    // No se pudo escribir el error en la respuesta
                }
            }
        }

        @PostMapping("/proforma/{id}/borrar")
        public String borrarProforma(@PathVariable Long id, RedirectAttributes redirectAttributes) {
            try {
                proformaRepository.deleteById(id);
                redirectAttributes.addFlashAttribute("mensaje", "Proforma borrada correctamente.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("mensaje", "Error al borrar proforma: " + e.getMessage());
            }
            return "redirect:/proforma";
        }

        @GetMapping("/proforma/{id}/editar")
        public String editarProforma(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
            try {
                proforma proforma = proformaRepository.findById(id).orElseThrow();
                model.addAttribute("proformaEditar", proforma);
                // ...agrega otros atributos necesarios...
                return "proforma_editar";
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("mensaje", "Error al cargar proforma para editar: " + e.getMessage());
                return "redirect:/proforma";
            }
        }

        @PostMapping("/proforma/actualizar")
        public String actualizarProforma(
            @RequestParam Long id,
            @RequestParam String nombre,
            // ...otros campos...
            RedirectAttributes redirectAttributes
        ) {
            try {
                proforma proforma = proformaRepository.findById(id).orElseThrow();
                proforma.setNombre(nombre);
                // ...actualiza otros campos...
                proformaRepository.save(proforma);
                redirectAttributes.addFlashAttribute("mensaje", "Proforma actualizada correctamente.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("mensaje", "Error al actualizar proforma: " + e.getMessage());
            }
            return "redirect:/proforma";
        }
    }

    @PostMapping("/recepcion/{id}/borrar")
    public String borrarRecepcion(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            pedidoRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("mensaje", "Recepción borrada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al borrar la recepción: " + e.getMessage());
        }
        return "redirect:/recepcion";
    }
}