package es.uma.informatica.daw.miau.pau_pevau.services;

import es.uma.informatica.daw.miau.pau_pevau.entities.Instituto;
import es.uma.informatica.daw.miau.pau_pevau.models.InstitutoDto;
import es.uma.informatica.daw.miau.pau_pevau.models.InstitutoNuevoDto;
import es.uma.informatica.daw.miau.pau_pevau.repositories.EstudianteRepository;
import es.uma.informatica.daw.miau.pau_pevau.repositories.InstitutoRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios del Servicio de Institutos")
class InstitutoServiceTest {

    @Mock
    private InstitutoRepository institutoRepo;

    @Mock
    private EstudianteRepository estudianteRepo;

    @InjectMocks
    private InstitutoService institutoService;

    @Test
    @DisplayName("Debe devolver una lista de todos los institutos")
    void consultarInstitutos_Exito() {
        // Arrange
        Instituto instituto = Instituto.builder().id(1L).nombre("IES Prueba").build();
        when(institutoRepo.findAll()).thenReturn(List.of(instituto));

        // Act
        List<InstitutoDto> resultado = institutoService.consultarInstitutos();

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        assertEquals("IES Prueba", resultado.get(0).getNombre());
        verify(institutoRepo, times(1)).findAll();
    }

    @Test
    @DisplayName("Debe devolver un instituto concreto si existe")
    void consultarInstituto_Existe_Exito() {
        // Arrange
        Long id = 1L;
        Instituto instituto = Instituto.builder().id(id).nombre("IES Prueba").build();
        when(institutoRepo.findById(id)).thenReturn(Optional.of(instituto));

        // Act
        InstitutoDto resultado = institutoService.consultarInstituto(id);

        // Assert
        assertNotNull(resultado);
        assertEquals("IES Prueba", resultado.getNombre());
        verify(institutoRepo, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe lanzar excepción 404 si el instituto no existe al consultarlo")
    void consultarInstituto_NoExiste_LanzaExcepcion() {
        // Arrange
        Long id = 99L;
        when(institutoRepo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> institutoService.consultarInstituto(id));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(institutoRepo, times(1)).findById(id);
    }

    @Test
    @DisplayName("Debe crear un nuevo instituto y devolver el DTO")
    void crearInstituto_Exito() {
        // Arrange
        InstitutoNuevoDto nuevoDto = new InstitutoNuevoDto();
        nuevoDto.setNombre("IES Nuevo");

        Instituto institutoGuardado = Instituto.builder().id(1L).nombre("IES Nuevo").build();
        when(institutoRepo.save(any(Instituto.class))).thenReturn(institutoGuardado);

        // Act
        InstitutoDto resultado = institutoService.crearInstituto(nuevoDto);

        // Assert
        assertNotNull(resultado);
        assertEquals(1L, resultado.getId());
        assertEquals("IES Nuevo", resultado.getNombre());
        verify(institutoRepo, times(1)).save(any(Instituto.class));
    }

    @Test
    @DisplayName("Debe actualizar un instituto existente y devolver sus nuevos datos")
    void actualizarInstituto_Existe_Exito() {
        // Arrange
        Long id = 1L;
        InstitutoNuevoDto dtoActualizado = new InstitutoNuevoDto();
        dtoActualizado.setNombre("IES Actualizado");

        Instituto institutoActual = Instituto.builder().id(id).nombre("IES Antiguo").build();

        when(institutoRepo.findById(id)).thenReturn(Optional.of(institutoActual));
        when(institutoRepo.save(any(Instituto.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        InstitutoDto resultado = institutoService.actualizarInstituto(id, dtoActualizado);

        // Assert
        assertNotNull(resultado);
        assertEquals("IES Actualizado", resultado.getNombre());
        verify(institutoRepo, times(1)).findById(id);
        verify(institutoRepo, times(1)).save(any(Instituto.class));
    }

    @Test
    @DisplayName("Debe lanzar excepcion 404 si se intenta actualizar un instituto inexistente")
    void actualizarInstituto_NoExiste_LanzaExcepcion() {
        // Arrange
        Long id = 99L;
        InstitutoNuevoDto dtoActualizado = new InstitutoNuevoDto();
        dtoActualizado.setNombre("IES Nuevo");

        when(institutoRepo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> institutoService.actualizarInstituto(id, dtoActualizado));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(institutoRepo, never()).save(any());
    }

    @Test
    @DisplayName("Debe eliminar un instituto si existe y no tiene estudiantes asociados")
    void eliminarInstituto_SinEstudiantes_Exito() {
        // Arrange
        Long id = 1L;
        Instituto instituto = Instituto.builder().id(id).nombre("IES Prueba").build();

        when(institutoRepo.findById(id)).thenReturn(Optional.of(instituto));
        when(estudianteRepo.existsByIdInstituto(id)).thenReturn(false);

        // Act
        institutoService.eliminarInstituto(id);

        // Assert
        verify(institutoRepo, times(1)).delete(instituto);
    }

    @Test
    @DisplayName("Debe lanzar excepcion 404 si se intenta eliminar un instituto inexistente")
    void eliminarInstituto_NoExiste_LanzaExcepcion() {
        // Arrange
        Long id = 99L;
        when(institutoRepo.findById(id)).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> institutoService.eliminarInstituto(id));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
        verify(institutoRepo, never()).delete(any());
    }

    @Test
    @DisplayName("Debe lanzar excepción 409 (Conflicto) si se intenta eliminar un instituto con estudiantes")
    void eliminarInstituto_ConEstudiantes_LanzaExcepcion() {
        // Arrange
        Long id = 1L;
        Instituto instituto = Instituto.builder().id(id).nombre("IES Prueba").build();

        when(institutoRepo.findById(id)).thenReturn(Optional.of(instituto));
        when(estudianteRepo.existsByIdInstituto(id)).thenReturn(true);

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () -> institutoService.eliminarInstituto(id));
        assertEquals(HttpStatus.CONFLICT, exception.getStatusCode());
        verify(institutoRepo, never()).delete(any());
    }
}