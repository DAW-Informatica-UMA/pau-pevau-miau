package es.uma.informatica.daw.miau.pau_pevau.repositories;

import es.uma.informatica.daw.miau.pau_pevau.entities.Estudiante;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstudianteRepository extends JpaRepository<Estudiante, Long> {
	
}