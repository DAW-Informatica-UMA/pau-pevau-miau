package es.uma.informatica.daw.miau.pau_pevau.repositories;

import es.uma.informatica.daw.miau.pau_pevau.entities.Materia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MateriaRepository extends JpaRepository<Materia, Long> {
    Optional<Materia> findByNombre(String nombre);
}