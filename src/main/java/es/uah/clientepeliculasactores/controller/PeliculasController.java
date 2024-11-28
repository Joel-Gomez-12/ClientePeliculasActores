package es.uah.clientepeliculasactores.controller;


import es.uah.clientepeliculasactores.model.Pelicula;
import es.uah.clientepeliculasactores.paginator.PageRender;
import es.uah.clientepeliculasactores.service.IActoresService;
import es.uah.clientepeliculasactores.service.IPeliculasService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;

@Controller
@RequestMapping("/ppeliculas")
public class PeliculasController {

    @Autowired
    IPeliculasService peliculasService;

    @Autowired
    IActoresService actoresService;

    @Value("${upload.dir}")
    private String uploadDir;

    // Listar todas las películas
    @GetMapping("/listado")
    public String listadoPeliculas(Model model, @RequestParam(name="page", defaultValue="0") int page) {
        Pageable pageable = PageRequest.of(page, 4);
        Page<Pelicula> listado = peliculasService.buscarTodas(pageable);
        PageRender<Pelicula> pageRender = new PageRender<Pelicula>("/ppeliculas/listado", listado);
        model.addAttribute("titulo", "Listado de todas las películas");
        model.addAttribute("listadoPeliculas", listado);
        model.addAttribute("page", pageRender);
        return "peliculas/listPelicula";
    }

    @GetMapping("/detalle/{id}")
    public String detalle(@PathVariable("id") Integer id, Model model, RedirectAttributes attributes) {
        Pelicula pelicula = peliculasService.buscarPeliculaPorId(id);
        model.addAttribute("pelicula", pelicula);
        model.addAttribute("titulo", "Detalle de la película" + pelicula.getTitulo());
        return "peliculas/detallePelicula";
    }

    @GetMapping("/crear")
    public String crear(Model model) {
        Pelicula pelicula = new Pelicula();
        model.addAttribute("pelicula", pelicula);
        model.addAttribute("titulo", "Crear película");

        return "peliculas/formPelicula";
    }

    @GetMapping("/buscar")
    public String buscar(Model model) {
        return "peliculas/buscarP";
    }

    @GetMapping("/titulo")
    public String buscarPeliculasPorTitulo(Model model, @RequestParam(name="page", defaultValue = "0") int page, @RequestParam("titulo") String titulo) {
        Pageable pageable = PageRequest.of(page, 4);
        Page<Pelicula> listado;
        if (titulo.equals("")) {
            listado = peliculasService.buscarTodas(pageable);
        } else {
            listado = peliculasService.buscarPeliculasPorTitulo(titulo, pageable);
        }
        PageRender<Pelicula> pageRender = new PageRender<Pelicula>("/listado", listado);
        model.addAttribute("titulo", "Listado de peliculas por título");
        model.addAttribute("listadoPeliculas", listado);
        model.addAttribute("page", pageRender);
        return "peliculas/listPelicula";
    }

    @GetMapping("/genero")
    public String buscarPeliculasPorGenero(Model model, @RequestParam(name="page", defaultValue = "0") int page, @RequestParam("genero") String genero) {
        Pageable pageable = PageRequest.of(page, 4);
        Page<Pelicula> listado;
        if (genero.equals("")) {
            listado = peliculasService.buscarTodas(pageable);
        } else {
            listado = peliculasService.buscarPeliculasPorGenero(genero, pageable);
        }
        PageRender<Pelicula> pageRender = new PageRender<Pelicula>("/listado", listado);
        model.addAttribute("titulo", "Listado de peliculas por género");
        model.addAttribute("listadoPeliculas", listado);
        model.addAttribute("page", pageRender);
        return "peliculas/listPelicula";
    }

    @GetMapping("/asociarp")
    public String mostrarFormularioAsociacion(Model model) {
        // Obtener todos los actores y películas disponibles
        model.addAttribute("actores", actoresService.buscarTodos(PageRequest.of(0, Integer.MAX_VALUE)).getContent());
        model.addAttribute("peliculas", peliculasService.buscarTodas(PageRequest.of(0, Integer.MAX_VALUE)).getContent());
        model.addAttribute("titulo", "Asociar Actor a Película");
        return "peliculas/formAsociar";
    }

    @PostMapping("/asociar")
    public String procesarAsociacion(@RequestParam("actorId") Integer actorId,
                                     @RequestParam("peliculaId") Integer peliculaId,
                                     RedirectAttributes attributes) {
        try {
            actoresService.actuarPelicula(actorId, peliculaId);
            attributes.addFlashAttribute("msg", "El actor fue asociado correctamente a la película!");
            attributes.addFlashAttribute("msgType", "success");
        } catch (Exception e) {
            attributes.addFlashAttribute("msg", "Error al asociar el actor a la película: " + e.getMessage());
            attributes.addFlashAttribute("msgType", "error");
        }
        return "redirect:/ppeliculas/listado";
    }


    @PostMapping("/guardar")
    public String guardarPelicula(Model model, Pelicula pelicula,
                                  @RequestParam("imagen") MultipartFile imagen, RedirectAttributes attributes) {

        if (!imagen.isEmpty()) {
            try {
                // Crear la ruta del archivo en el directorio de recursos estáticos
                String filePath = new File("src/main/resources/static/uploads" + uploadDir + imagen.getOriginalFilename()).getAbsolutePath();
                File destFile = new File(filePath);

                // Crear directorios si no existen
                destFile.getParentFile().mkdirs();

                // Guardar la imagen en el directorio configurado
                imagen.transferTo(destFile);

                // Establecer la ruta de la imagen en la película (accesible en la web)
                pelicula.setImagenPortada("/uploads/" + imagen.getOriginalFilename());
            } catch (IOException e) {
                e.printStackTrace();
                // Manejo de errores
                return "redirect:/ppeliculas/crear?error=upload";
            }
        }

        peliculasService.guardarPelicula(pelicula);
        model.addAttribute("titulo", "Nueva película");
        attributes.addFlashAttribute("msg", "Los datos de la película fueron guardados!");
        return "redirect:/ppeliculas/listado";
    }


    @GetMapping("/editar/{id}")
    public String editarPelicula(Model model, @PathVariable("id") Integer id) {
        Pelicula pelicula = peliculasService.buscarPeliculaPorId(id);
        model.addAttribute("titulo", "Editar película");
        model.addAttribute("pelicula", pelicula);
        return "peliculas/formPelicula";
    }

    @GetMapping("/borrar/{id}")
    public String eliminarPelicula(Model model, @PathVariable("id") Integer id, RedirectAttributes attributes) {
        Pelicula pelicula = peliculasService.buscarPeliculaPorId(id);
        peliculasService.eliminarPelicula(id);
        attributes.addFlashAttribute("msg", "Los datos de la película fueron borrados!");
        return "redirect:/ppeliculas/listado";
    }

}
