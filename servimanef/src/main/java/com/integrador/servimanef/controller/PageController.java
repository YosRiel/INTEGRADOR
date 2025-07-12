package com.integrador.servimanef.controller;

import com.integrador.servimanef.entity.imagen;
import com.integrador.servimanef.entity.informe;
import com.integrador.servimanef.entity.grupo;
import com.integrador.servimanef.entity.pedido;
import com.integrador.servimanef.entity.proforma;
import com.integrador.servimanef.service.pedidoService;
import com.integrador.servimanef.service.informeService;
import com.integrador.servimanef.service.grupoService;
import com.integrador.servimanef.service.imagenService;
import com.integrador.servimanef.service.proformaService;
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

import java.security.Principal;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Controller
public class PageController {
    @Autowired
    private pedidoService pedidoService;
    @Autowired
    private informeService informeService;
    @Autowired
    private grupoService grupoService;
    @Autowired
    private imagenService imagenService;
    @Autowired
    private proformaService proformaService;


    private static final Logger logger = LoggerFactory.getLogger(PageController.class);

    @GetMapping("/")
    public String index() {
        return "index";
    }

    @GetMapping("/intranet")
    public String intranet() {
        return "intranet";
    }
    

    @GetMapping("/main_menu")
    public String mainMenu() {
        return "main_menu";
    }

    @GetMapping("/informes")
    public String informes(Model model) {
        model.addAttribute("informes", informeService.listarTodos());
        return "informes";
    }

    @PostMapping("/informes/crear")
    public String crearInforme(@RequestParam String nombre, RedirectAttributes redirectAttributes, Principal principal) {
        String nombreTransformado = nombre.toUpperCase().replace(" ", "-");
        Long count = informeService.contar() + 1;
        String membrete = String.format("SM-%04d", count);

        informe informe = new informe();
        informe.setNombre(membrete + "-" + nombreTransformado);
        informeService.guardar(informe);

        logger.info("Usuario {} creó un informe", principal.getName());

        redirectAttributes.addFlashAttribute("mensaje", "Nuevo informe creado. Ingrese los grupos.");
        redirectAttributes.addFlashAttribute("informeId", informe.getId());
        return "redirect:/informes/" + informe.getId() + "/grupos";
    }

    @GetMapping("/informes/{id}/grupos")
    public String mostrarFormularioGrupo(@PathVariable Long id, Model model) {
        informe informe = informeService.buscarPorId(id).orElseThrow();
        List<grupo> grupos = grupoService.listarPorInformeId(id);
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
        informe informe = informeService.buscarPorId(id).orElseThrow();

        grupo grupo = new grupo();
        grupo.setInforme(informe);
        grupo.setNombreGrupo(nombreGrupo);
        grupo.setDescripcion(descripcion);
        grupo.setCantidadImagenes(cantidadImagenes);

        grupo = grupoService.guardar(grupo);

        for (MultipartFile file : imagenes) {
            if (!file.isEmpty()) {
                imagen imagen = new imagen();
                imagen.setDatos(file.getBytes());
                imagen.setTipo(file.getContentType());
                imagen.setGrupo(grupo);
                imagenService.guardar(imagen);
            }
        }

        redirectAttributes.addFlashAttribute("mensaje", "Grupo agregado correctamente.");
        return "redirect:/informes/" + id + "/grupos";
    }

    @GetMapping("/recepcion")
    public String recepcion(Model model) {
        model.addAttribute("recepciones", pedidoService.listarTodos());
        return "recepcion";
    }

    @PostMapping("/pedido")
    public String registrarPedido(@ModelAttribute pedido pedido) {
        if (pedido.getEstado() == null || pedido.getEstado().isEmpty()) {
            pedido.setEstado("Pendiente"); // O el estado inicial que prefieras
        }
        pedidoService.guardar(pedido);
        return "redirect:/";
    }

    @GetMapping("/informes/{id}/editar")
    public String editarInforme(@PathVariable Long id, Model model) {
        informe informe = informeService.buscarPorId(id).orElseThrow();
        List<grupo> grupos = grupoService.listarPorInformeId(id);
        List<informe> informes = informeService.listarTodos();
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
        informeService.borrar(id);
        redirectAttributes.addFlashAttribute("mensaje", "Informe borrado correctamente.");
        return "redirect:/informes";
    }

    @RestController
    public class ImagenController {
        @GetMapping("/imagen/{id}")
        public ResponseEntity<byte[]> verImagen(@PathVariable Long id) {
            Optional<imagen> imagenOpt = imagenService.buscarPorId(id);
            if (imagenOpt.isEmpty()) {
                return ResponseEntity.notFound().build();
            }
            imagen imagen = imagenOpt.get();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(imagen.getTipo()));
            return new ResponseEntity<>(imagen.getDatos(), headers, HttpStatus.OK);
        }
    }

    @Controller
    public class ProformaController {

        @GetMapping("/proforma")
        public String listarProformas(@RequestParam(value = "editar", required = false) Long editarId, Model model, RedirectAttributes redirectAttributes) {
            try {
                model.addAttribute("proformas", proformaService.listarTodos());
                model.addAttribute("pedidos", pedidoService.listarTodos());
                model.addAttribute("informes", informeService.listarTodos());
                if (editarId != null) {
                    proforma proformaEditar = proformaService.buscarPorId(editarId).orElse(null);
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
                Long count = proformaService.contar() + 1;
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
                proformaService.guardar(proforma);

                pedido pedido = pedidoService.buscarPorId(recepcionId).orElseThrow();
                pedido.setEstado("Finalizado");
                pedidoService.guardar(pedido);
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
                proforma proforma = proformaService.buscarPorId(id).orElseThrow();
                pedido pedido = null;
                if (proforma.getPedidoId() != null) {
                    pedido = pedidoService.buscarPorId(proforma.getPedidoId()).orElse(null);
                }

                response.setContentType("application/pdf");
                response.setHeader("Content-Disposition", "attachment; filename=" + proforma.getNombre() + ".pdf");
                response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
                response.setHeader("Pragma", "no-cache");
                response.setDateHeader("Expires", 0);

                Document document = new Document();
                PdfWriter.getInstance(document, response.getOutputStream());
                document.open();

                // Encabezado
                Paragraph encabezado = new Paragraph("SERVIMANEF E.I.R.L.", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
                encabezado.setAlignment(Paragraph.ALIGN_CENTER);
                document.add(encabezado);

                document.add(new Paragraph(" "));

                // Servicio y fecha
                document.add(new Paragraph("Servicio: " + proforma.getNombre(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
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

                // Cálculo IGV e Importe Total
                if (proforma.getValorServicio() != null) {
                    double valor = proforma.getValorServicio();
                    double igv = Math.round(valor * 0.18 * 100.0) / 100.0;
                    double total = Math.round((valor + igv) * 100.0) / 100.0;

                    document.add(new Paragraph("Valor del Servicio: S/ " + valor, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                    document.add(new Paragraph("IGV (18%): S/ " + igv, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                    document.add(new Paragraph("Importe Total: S/ " + total, FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
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
                proformaService.borrar(id);
                redirectAttributes.addFlashAttribute("mensaje", "Proforma borrada correctamente.");
            } catch (Exception e) {
                redirectAttributes.addFlashAttribute("mensaje", "Error al borrar proforma: " + e.getMessage());
            }
            return "redirect:/proforma";
        }

        @GetMapping("/proforma/{id}/editar")
        public String editarProforma(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
            try {
                proforma proforma = proformaService.buscarPorId(id).orElseThrow();
                model.addAttribute("proformaEditar", proforma);
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
                proforma proforma = proformaService.buscarPorId(id).orElseThrow();
                proforma.setNombre(nombre);
                // ...actualiza otros campos...
                proformaService.guardar(proforma);
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
            pedidoService.borrar(id);
            redirectAttributes.addFlashAttribute("mensaje", "Recepción borrada correctamente.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("mensaje", "Error al borrar la recepción: " + e.getMessage());
        }
        return "redirect:/recepcion";
    }

    @GetMapping("/informes/{id}/pdf")
    public void descargarInformePdf(@PathVariable Long id, jakarta.servlet.http.HttpServletResponse response) {
        try {
            informe informe = informeService.buscarPorId(id).orElseThrow();
            List<grupo> grupos = grupoService.listarPorInformeId(id);

            response.setContentType("application/pdf");
            response.setHeader("Content-Disposition", "attachment; filename=informe_" + id + ".pdf");
            response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            Document document = new Document();
            PdfWriter.getInstance(document, response.getOutputStream());
            document.open();

            // Encabezado
            Paragraph encabezado = new Paragraph("SERVIMANEF E.I.R.L.", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 18));
            encabezado.setAlignment(Paragraph.ALIGN_CENTER);
            document.add(encabezado);

            document.add(new Paragraph(" "));
            document.add(new Paragraph("INFORME FOTOGRÁFICO CALADO EN BODEGAS", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 14)));
            document.add(new Paragraph("EVIDENCIA DE CULMINACION DE SERVICIO", FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12, com.lowagie.text.Font.BOLDITALIC)));
            document.add(new Paragraph(" "));

            // Datos generales
            document.add(new Paragraph("Contrato: SERVIMANEF"));
            document.add(new Paragraph("Puerto: CALLAO"));
            document.add(new Paragraph(" "));

            // Imágenes agrupadas
            for (grupo grupo : grupos) {
                document.add(new Paragraph("Grupo: " + grupo.getNombreGrupo(), FontFactory.getFont(FontFactory.HELVETICA_BOLD, 12)));
                document.add(new Paragraph(grupo.getDescripcion() != null ? grupo.getDescripcion() : ""));
                document.add(new Paragraph(" "));

                List<imagen> imagenes = grupo.getImagenes(); // Asegúrate de tener el método getImagenes() en tu entidad grupo
                for (imagen img : imagenes) {
                    if (img.getDatos() != null) {
                        com.lowagie.text.Image image = com.lowagie.text.Image.getInstance(img.getDatos());
                        image.scaleToFit(250, 250);
                        image.setAlignment(com.lowagie.text.Image.ALIGN_CENTER);
                        document.add(image);
                        document.add(new Paragraph(" "));
                    }
                }
                document.add(new Paragraph(" "));
            }

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
}