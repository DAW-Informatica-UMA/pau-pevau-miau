package es.uma.informatica.daw.miau.pau_pevau.repositories;

import es.uma.informatica.daw.miau.pau_pevau.entities.Estudiante;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    Optional<Estudiante> findByDniAndConvocatoriaId(String dni, Long idConvocatoria);

    boolean existsByDniAndConvocatoriaId(String dni, Long idConvocatoria);

    boolean existsByDniAndConvocatoriaIdAndIdNot(String dni, Long idConvocatoria, Long id);

    boolean existsByIdAndNoEliminarTrue(Long id);

    List<Estudiante> findByIdSede(Long idSede);

    List<Estudiante> findByIdSede(Long idSede, Sort sort);

    List<Estudiante> findByConvocatoriaId(Long idConvocatoria);

    List<Estudiante> findByConvocatoriaId(Long idConvocatoria, Sort sort);

    List<Estudiante> findByConvocatoriaIdAndNoEliminarFalse(Long idConvocatoria);

    List<Estudiante> findByIdSedeAndConvocatoriaId(Long idSede, Long idConvocatoria);

    List<Estudiante> findByIdSedeAndConvocatoriaId(Long idSede, Long idConvocatoria, Sort sort);
}