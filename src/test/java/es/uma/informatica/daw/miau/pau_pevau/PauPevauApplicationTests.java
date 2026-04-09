package es.uma.informatica.daw.miau.pau_pevau;

import es.uma.informatica.daw.miau.pau_pevau.entities.*;
import es.uma.informatica.daw.miau.pau_pevau.repositories.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional // Revierte los cambios en BD después de cada test
class PauPevauApplicationTests {

	@Test
	void contextLoads() {
	}

	@Autowired
	private EstudianteRepository estudianteRepo;

	@Autowired
	private MateriaRepository materiaRepo;

	@Autowired
	private InstitutoRepository institutoRepo;

	@Test
	void testCrearYGuardarEstudianteConInstituto() {
		// 1. Crear Instituto
		Instituto instituto = new Instituto();
		instituto.setNombre("IES Juan XXIII");
		instituto = institutoRepo.save(instituto);

		// 2. Crear Estudiante
		Estudiante estudiante = new Estudiante();
		estudiante.setDni("12345678A");
		estudiante.setInstituto(instituto);
		estudiante.setNoEliminar(true);
		estudiante = estudianteRepo.save(estudiante);

		// 3. Comprobar que se ha guardado bien (Navegación y Cardinalidad)
		Estudiante guardado = estudianteRepo.findById(estudiante.getId()).orElse(null);
		assertNotNull(guardado, "El estudiante debería haberse guardado en BD");
		assertEquals("12345678A", guardado.getDni());
		assertNotNull(guardado.getInstituto(), "El estudiante debería tener el instituto asociado");
		assertEquals("IES Juan XXIII", guardado.getInstituto().getNombre());
	}

	@Test
	void testRelacionManyToManyEstudianteMateria() {
		// 1. Crear Materias
		Materia mates = new Materia();
		mates.setNombre("Matemáticas II");
		mates = materiaRepo.save(mates);

		Materia fisica = new Materia();
		fisica.setNombre("Física");
		fisica = materiaRepo.save(fisica);

		// 2. Crear Estudiante y matricularlo
		Estudiante estudiante = new Estudiante();
		estudiante.setDni("87654321B");
		estudiante.setMateriasMatriculadas(List.of(mates, fisica));
		estudiante = estudianteRepo.save(estudiante);

		// 3. Comprobar que la tabla intermedia (estudiante_materia) funciona
		Estudiante guardado = estudianteRepo.findById(estudiante.getId()).get();
		assertEquals(2, guardado.getMateriasMatriculadas().size(), "Debería estar matriculado en 2 materias");
		assertTrue(guardado.getMateriasMatriculadas().stream().anyMatch(m -> m.getNombre().equals("Física")));
	}
}
