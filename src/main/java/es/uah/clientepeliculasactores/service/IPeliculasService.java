package es.uah.clientepeliculasactores.service;

import es.uah.clientepeliculasactores.model.Pelicula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPeliculasService {
    Page<Pelicula> buscarTodas(Pageable pageable);

    Pelicula buscarPeliculaPorId(Integer idPelicula);

    Page<Pelicula> buscarPeliculasPorTitulo(String titulo, Pageable pageable);

    Page<Pelicula> buscarPeliculasPorGenero(String genero, Pageable pageable);

    void guardarPelicula(Pelicula pelicula);

    void eliminarPelicula(Integer idPelicula);
}
