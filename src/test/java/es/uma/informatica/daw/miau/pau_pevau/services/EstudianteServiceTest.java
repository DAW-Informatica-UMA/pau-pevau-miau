package es.uma.informatica.daw.miau.pau_pevau.services;

import es.uma.informatica.daw.miau.pau_pevau.clients.CatalogoClient;
import es.uma.informatica.daw.miau.pau_pevau.entities.Estudiante;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.EstudianteBloqueadoException;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.EstudianteNoEncontradoException;
import es.uma.informatica.daw.miau.pau_pevau.mappers.EstudianteMapper;
import es.uma.informatica.daw.miau.pau_pevau.models.EstudianteDto;
import es.uma.informatica.daw.miau.pau_pevau.repositories.EstudianteRepository;
import es.uma.informatica.daw.miau.pau_pevau.repositories.InstitutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios del Servicio de Estudiantes")
class EstudianteServiceTest {

    @Mock
    private EstudianteRepository estudianteRepo;

    @Mock
    private EstudianteMapper mapper;

    @Mock
    private CatalogoClient catalogoClient;

    @Mock
    private InstitutoRepository institutoRepo;

    @Mock
    private CsvEstudianteParser csvParser;

    @InjectMocks
    private EstudianteService estudianteService;

    @Test
    @DisplayName("Debe lanzar excepción al intentar consultar un estudiante que no existe")
    void consultarEstudiante_NoExiste_LanzaExcepcion() {
        // Arrange
        Long id = 99L;
        when(estudianteRepo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        EstudianteNoEncontradoException exception = assertThrows(EstudianteNoEncontradoException.class, () -> {
            estudianteService.consultarEstudiante(id);
        });
        assertEquals("Estudiante no encontrado", exception.getMessage());
        verify(estudianteRepo, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción si se intenta eliminar un estudiante marcado como no eliminable")
    void eliminarEstudiante_Bloqueado_LanzaExcepcion() {
        // Arrange
        Long id = 1L;
        Estudiante estudiante = new Estudiante();
        estudiante.setId(id);
        estudiante.setNoEliminar(true); // Condición crítica de la rúbrica / OpenAPI

        when(estudianteRepo.findById(id)).thenReturn(Optional.of(estudiante));

        // Act & Assert
        EstudianteBloqueadoException exception = assertThrows(EstudianteBloqueadoException.class, () -> {
            estudianteService.eliminarEstudiante(id);
        });
        assertEquals("Este estudiante está bloqueado y no se puede borrar", exception.getMessage());

        // Verificamos que el repositorio nunca llega a ejecutar el borrado
        verify(estudianteRepo, never()).delete(any());
    }

    @Test
    @DisplayName("Debe crear un nuevo estudiante correctamente")
    void crearEstudiante_Exito() {
        // Arrange
        es.uma.informatica.daw.miau.pau_pevau.models.EstudianteNuevoDto nuevoDto = new es.uma.informatica.daw.miau.pau_pevau.models.EstudianteNuevoDto();
        nuevoDto.setDni("12345678X");

        // Simulamos que el DNI no existe en la convocatoria actual
        when(estudianteRepo.existsByDniAndIdConvocatoria(anyString(), any())).thenReturn(false);

        Estudiante entidadMapeada = new Estudiante();
        entidadMapeada.setDni("12345678X");
        when(mapper.aEntidad(any(), any())).thenReturn(entidadMapeada);

        Estudiante entidadGuardada = new Estudiante();
        entidadGuardada.setId(1L);
        entidadGuardada.setDni("12345678X");
        when(estudianteRepo.save(any(Estudiante.class))).thenReturn(entidadGuardada);

        EstudianteDto dtoFinal = new EstudianteDto();
        dtoFinal.setId(1L);
        when(mapper.aDto(any(), any(), any())).thenReturn(dtoFinal);

        // Act
        EstudianteDto resultado = estudianteService.crearEstudiante(nuevoDto);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        verify(estudianteRepo, times(1)).save(any(Estudiante.class));
    }

    @Test
    @DisplayName("Debe lanzar excepción al crear si el DNI está duplicado")
    void crearEstudiante_DniDuplicado_LanzaExcepcion() {
        // Arrange
        es.uma.informatica.daw.miau.pau_pevau.models.EstudianteNuevoDto nuevoDto = new es.uma.informatica.daw.miau.pau_pevau.models.EstudianteNuevoDto();
        nuevoDto.setDni("12345678X");

        when(estudianteRepo.existsByDniAndIdConvocatoria(eq("12345678X"), any())).thenReturn(true);

        // Act & Assert
        assertThrows(es.uma.informatica.daw.miau.pau_pevau.exceptions.DniDuplicadoException.class, () -> {
            estudianteService.crearEstudiante(nuevoDto);
        });

        verify(estudianteRepo, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción al intentar cambiar noEliminar de true a false")
    void actualizarEstudiante_RevocarNoEliminar_LanzaExcepcion() {
        // Arrange
        Long id = 1L;
        Estudiante actual = new Estudiante();
        actual.setId(id);
        actual.setDni("12345678X");
        actual.setNoEliminar(true); // El estudiante ya está bloqueado en base de datos

        when(estudianteRepo.findById(id)).thenReturn(Optional.of(actual));

        es.uma.informatica.daw.miau.pau_pevau.models.EstudianteNuevoDto modificadoDto =
                new es.uma.informatica.daw.miau.pau_pevau.models.EstudianteNuevoDto();
        modificadoDto.setDni("12345678X");
        modificadoDto.setNoEliminar(false); // Simulamos que el usuario intenta revocarlo

        // Act & Assert
        assertThrows(es.uma.informatica.daw.miau.pau_pevau.exceptions.EstudianteBloqueadoException.class, () -> {
            estudianteService.actualizarEstudiante(id, modificadoDto);
        });

        // Verificamos que se abortó la operación y nunca se llegó a guardar
        verify(estudianteRepo, never()).save(any());
    }
}