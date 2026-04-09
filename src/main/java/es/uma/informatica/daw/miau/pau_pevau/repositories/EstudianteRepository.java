package es.uma.informatica.daw.miau.pau_pevau.repositories;

import es.uma.informatica.daw.miau.pau_pevau.entities.Estudiante;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {

    List<Estudiante> findByIdsedeId(Long idSede);

    List<Estudiante> findByIdsedeId(Long idSede, Sort sort);
}