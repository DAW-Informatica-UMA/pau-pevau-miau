package es.uma.informatica.daw.miau.pau_pevau.repositories;

import es.uma.informatica.daw.miau.pau_pevau.entities.Convocatoria;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConvocatoriaRepository extends JpaRepository<Convocatoria, Long> {

    Optional<Convocatoria> findByCursoAcademico(String cursoAcademico);

    boolean existsByCursoAcademico(String cursoAcademico);
}
