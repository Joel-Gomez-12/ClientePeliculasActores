package es.uah.clientepeliculasactores.controller;

import es.uah.clientepeliculasactores.model.Actor;
import es.uah.clientepeliculasactores.model.Pelicula;
import es.uah.clientepeliculasactores.paginator.PageRender;
import es.uah.clientepeliculasactores.service.IActoresService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;


@Controller
@RequestMapping("/pactores")
public class ActoresController {
    @Autowired
    IActoresService actoresService;

    @GetMapping("/listado")
    public String listadoActores(Model model, @RequestParam(name="page", defaultValue = "0") int page) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Actor> listado = actoresService.buscarTodos(pageable);
        PageRender<Actor> pageRender = new PageRender<Actor>("/pactores/listado", listado);
        model.addAttribute("titulo", "Listado de todos los actores");
        model.addAttribute("listadoActores", listado);
        model.addAttribute("page", pageRender);
        return "peliculas/listActor";
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
        Actor actor = actoresService.buscarActorPorId(id);
        model.addAttribute("actor", actor);
        model.addAttribute("titulo", "Detalle del actor" + actor.getNombre());
        return "peliculas/detalleActor";
    }

    @GetMapping("/nombre")
    public String buscarActoresPorNombre(Model model,
                                         @RequestParam(name = "page", defaultValue = "0") int page,
                                         @RequestParam("nombre") String nombre) {
        Pageable pageable = PageRequest.of(page, 10);
        Page<Actor> listado;

        if (nombre.equals("")) {
            listado = actoresService.buscarTodos(pageable);
        } else {
            listado = actoresService.buscarActoresPorNombre(nombre, pageable);
        }
        PageRender<Actor> pageRender = new PageRender<Actor>("/listado", listado);
        model.addAttribute("nombre", "Listado de actores por nombre");
        model.addAttribute("listadoActores", listado);
        model.addAttribute("page", pageRender);
        return "peliculas/listActor";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        Actor actor = new Actor();
        model.addAttribute("actor", actor);
        model.addAttribute("titulo", "Crear Actor");
        return "peliculas/formActor";
    }

    @PostMapping("/guardar")
    public String guardarActor(Model model, Actor actor, RedirectAttributes attributes) {
        try {
            // Recuperar el actor existente si tiene un ID
            if (actor.getIdActor() != null && actor.getIdActor() > 0) {
                Actor existingActor = actoresService.buscarActorPorId(actor.getIdActor());
                if (existingActor != null) {
                    // Mantener las asociaciones existentes
                    actor.setPeliculas(existingActor.getPeliculas());
                }
            }
            actoresService.guardarActor(actor);
            model.addAttribute("titulo", "Nuevo actor");
            attributes.addFlashAttribute("msg", "Los datos del actor fueron guardados!");
            attributes.addFlashAttribute("msgType", "success");
        } catch (Exception e) {
            attributes.addFlashAttribute("msg", "Error al guardar los datos del actor!");
            attributes.addFlashAttribute("msgType", "error");
        }
        return "redirect:/pactores/listado";
    }

    @GetMapping("/editar/{id}")
    public String editarActor(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes  ) {
        Actor actor = actoresService.buscarActorPorId(id);
        if (actor != null) {
            model.addAttribute("titulo", "Editar actor");
            model.addAttribute("actor", actor);
            return "peliculas/formActor";
        } else {
            attributes.addFlashAttribute("msg", "¡Actor no encontrado!");
            attributes.addFlashAttribute("msgType", "error");
            return "redirect:/pactores/listado";
        }
    }

    @GetMapping("/borrar/{id}")
    public String eliminarActor(@PathVariable("id") Integer id, Model model,  RedirectAttributes attributes) {
        try {
            Actor actor = actoresService.buscarActorPorId(id);
            if (actor != null) {
                actoresService.eliminarActor(id);
                attributes.addFlashAttribute("msg", "¡Los datos del actor fueron borrados!");
                attributes.addFlashAttribute("msgType", "success");
            } else {
                attributes.addFlashAttribute("msg", "¡Actor no encontrado!");
                attributes.addFlashAttribute("msgType", "error");
            }
        } catch (Exception e) {
            attributes.addFlashAttribute("msg", "Error al eliminar los datos del actor: " + e.getMessage());
            attributes.addFlashAttribute("msgType", "error");
        }
        return "redirect:/pactores/listado";
    }

}
