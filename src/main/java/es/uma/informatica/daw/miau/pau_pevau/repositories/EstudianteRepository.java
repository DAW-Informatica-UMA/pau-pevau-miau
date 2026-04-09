package es.uma.informatica.daw.miau.pau_pevau.repositories;

import es.uma.informatica.daw.miau.pau_pevau.entities.Estudiante;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    List<Estudiante> findByIdSede(Long idSede);

    List<Estudiante> findByIdSede(Long idSede, Sort sort);

    List<Estudiante> findByConvocatoriaId(Long idConvocatoria);

    List<Estudiante> findByConvocatoriaId(Long idConvocatoria, Sort sort);

    List<Estudiante> findByIdSedeAndConvocatoriaId(Long idSede, Long idConvocatoria);

    List<Estudiante> findByIdSedeAndConvocatoriaId(Long idSede, Long idConvocatoria, Sort sort);
}