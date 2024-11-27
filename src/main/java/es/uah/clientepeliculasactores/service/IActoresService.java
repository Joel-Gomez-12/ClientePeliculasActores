package es.uah.clientepeliculasactores.service;

import es.uah.clientepeliculasactores.model.Actor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IActoresService {

    Page<Actor> buscarTodos(Pageable pageable);

    Actor buscarActorPorId(Integer idActor);

    Page<Actor> buscarActoresPorNombre(String nombre, Pageable pageable);

    void guardarActor(Actor actor);

    void eliminarActor(Integer idActor);

    void actuarPelicula(Integer idActor, Integer idPelicula);


}
