package es.uma.informatica.daw.miau.pau_pevau.repositories;

import es.uma.informatica.daw.miau.pau_pevau.entities.Estudiante;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    Optional<Estudiante> findByDniAndIdConvocatoria(String dni, Long idConvocatoria);

    boolean existsByDniAndIdConvocatoria(String dni, Long idConvocatoria);

    boolean existsByIdInstituto(Long idInstituto);

    boolean existsByDniAndIdConvocatoriaAndIdNot(String dni, Long idConvocatoria, Long id);

    boolean existsByIdAndNoEliminarTrue(Long id);

    List<Estudiante> findByIdSede(Long idSede);

    List<Estudiante> findByIdSede(Long idSede, Sort sort);

    List<Estudiante> findByIdConvocatoria(Long idConvocatoria);

    List<Estudiante> findByIdConvocatoria(Long idConvocatoria, Sort sort);

    List<Estudiante> findByIdConvocatoriaAndNoEliminarFalse(Long idConvocatoria);

    List<Estudiante> findByIdSedeAndIdConvocatoria(Long idSede, Long idConvocatoria);

    List<Estudiante> findByIdSedeAndIdConvocatoria(Long idSede, Long idConvocatoria, Sort sort);
}
