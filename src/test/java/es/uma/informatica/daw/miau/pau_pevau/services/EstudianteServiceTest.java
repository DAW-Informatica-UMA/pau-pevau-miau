package es.uma.informatica.daw.miau.pau_pevau.services;

import es.uma.informatica.daw.miau.pau_pevau.clients.CatalogoClient;
import es.uma.informatica.daw.miau.pau_pevau.entities.Estudiante;
import es.uma.informatica.daw.miau.pau_pevau.entities.Instituto;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.DniDuplicadoException;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.EstudianteBloqueadoException;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.EstudianteNoEncontradoException;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.InstitutoNoEncontradoException;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.MateriaNoEncontradaException;
import es.uma.informatica.daw.miau.pau_pevau.mappers.EstudianteMapper;
import es.uma.informatica.daw.miau.pau_pevau.models.EstudianteDto;
import es.uma.informatica.daw.miau.pau_pevau.models.EstudianteNuevoDto;
import es.uma.informatica.daw.miau.pau_pevau.models.ImportacionEstudiantes;
import es.uma.informatica.daw.miau.pau_pevau.models.InstitutoDto;
import es.uma.informatica.daw.miau.pau_pevau.models.MateriaDto;
import es.uma.informatica.daw.miau.pau_pevau.models.NombreCompletoDto;
import es.uma.informatica.daw.miau.pau_pevau.repositories.EstudianteRepository;
import es.uma.informatica.daw.miau.pau_pevau.repositories.InstitutoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
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

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(estudianteService, "CONVOCATORIA_VIGENTE", 1L);
    }

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
    @DisplayName("Debe devolver un estudiante con instituto y materias")
    void consultarEstudiante_Existe_DevuelveDtoCompleto() {
        // Arrange
        Long id = 1L;
        Estudiante estudiante = new Estudiante();
        estudiante.setId(id);
        estudiante.setIdInstituto(5L);
        estudiante.setMateriasMatriculadas(new ArrayList<>(List.of(10L, 11L)));

        Instituto instituto = Instituto.builder().id(5L).nombre("IES Centro").build();

        MateriaDto materia = new MateriaDto();
        materia.setId(10L);
        materia.setNombre("Matematicas");

        when(estudianteRepo.findById(id)).thenReturn(Optional.of(estudiante));
        when(institutoRepo.findById(5L)).thenReturn(Optional.of(instituto));
        when(catalogoClient.getMateria(10L)).thenReturn(materia);
        when(catalogoClient.getMateria(11L)).thenReturn(null);

        EstudianteDto dto = new EstudianteDto();
        dto.setId(id);
        when(mapper.aDto(eq(estudiante), any(), any())).thenReturn(dto);

        // Act
        EstudianteDto resultado = estudianteService.consultarEstudiante(id);

        // Assert
        assertNotNull(resultado);
        assertEquals(id, resultado.getId());
        ArgumentCaptor<InstitutoDto> institutoCaptor = ArgumentCaptor.forClass(InstitutoDto.class);
        ArgumentCaptor<Set> materiasCaptor = ArgumentCaptor.forClass(Set.class);
        verify(mapper).aDto(eq(estudiante), institutoCaptor.capture(), materiasCaptor.capture());
        assertEquals("IES Centro", institutoCaptor.getValue().getNombre());
        assertEquals(1, materiasCaptor.getValue().size());
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
    @DisplayName("Debe eliminar un estudiante si no esta bloqueado")
    void eliminarEstudiante_Exito() {
        // Arrange
        Long id = 1L;
        Estudiante estudiante = new Estudiante();
        estudiante.setId(id);
        estudiante.setNoEliminar(false);
        when(estudianteRepo.findById(id)).thenReturn(Optional.of(estudiante));

        // Act
        estudianteService.eliminarEstudiante(id);

        // Assert
        verify(estudianteRepo, times(1)).delete(estudiante);
    }

    @Test
    @DisplayName("Debe crear un nuevo estudiante correctamente")
    void crearEstudiante_Exito() {
        // Arrange
        EstudianteNuevoDto nuevoDto = new EstudianteNuevoDto();
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
        EstudianteNuevoDto nuevoDto = new EstudianteNuevoDto();
        nuevoDto.setDni("12345678X");

        when(estudianteRepo.existsByDniAndIdConvocatoria(eq("12345678X"), any())).thenReturn(true);

        // Act & Assert
        assertThrows(es.uma.informatica.daw.miau.pau_pevau.exceptions.DniDuplicadoException.class, () -> {
            estudianteService.crearEstudiante(nuevoDto);
        });

        verify(estudianteRepo, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el instituto no existe al crear")
    void crearEstudiante_InstitutoNoExiste_LanzaExcepcion() {
        // Arrange
        EstudianteNuevoDto nuevoDto = new EstudianteNuevoDto();
        nuevoDto.setDni("12345678X");
        nuevoDto.setIdInstituto(10L);

        when(institutoRepo.existsById(10L)).thenReturn(false);

        // Act & Assert
        assertThrows(InstitutoNoEncontradoException.class, () -> estudianteService.crearEstudiante(nuevoDto));
        verify(estudianteRepo, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepcion si alguna materia no existe al crear")
    void crearEstudiante_MateriaNoExiste_LanzaExcepcion() {
        // Arrange
        EstudianteNuevoDto nuevoDto = new EstudianteNuevoDto();
        nuevoDto.setDni("12345678X");
        nuevoDto.setIdInstituto(1L);
        nuevoDto.setMateriasMatriculadas(Set.of(20L));

        when(institutoRepo.existsById(1L)).thenReturn(true);
        when(catalogoClient.getMateria(20L)).thenReturn(null);

        // Act & Assert
        assertThrows(MateriaNoEncontradaException.class, () -> estudianteService.crearEstudiante(nuevoDto));
        verify(estudianteRepo, never()).save(any());
    }

    @Test
    @DisplayName("Debe lanzar excepcion si el instituto no existe al actualizar")
    void actualizarEstudiante_InstitutoNoExiste_LanzaExcepcion() {
        // Arrange
        EstudianteNuevoDto modificado = new EstudianteNuevoDto();
        modificado.setDni("12345678X");
        modificado.setIdInstituto(99L);

        when(institutoRepo.existsById(99L)).thenReturn(false);

        // Act & Assert
        assertThrows(InstitutoNoEncontradoException.class,
                () -> estudianteService.actualizarEstudiante(1L, modificado));
        verify(estudianteRepo, never()).findById(any());
    }

    @Test
    @DisplayName("Debe lanzar excepcion si falta una materia al actualizar")
    void actualizarEstudiante_MateriaNoExiste_LanzaExcepcion() {
        // Arrange
        EstudianteNuevoDto modificado = new EstudianteNuevoDto();
        modificado.setDni("12345678X");
        modificado.setIdInstituto(1L);
        modificado.setMateriasMatriculadas(Set.of(30L));

        when(institutoRepo.existsById(1L)).thenReturn(true);
        when(catalogoClient.getMateria(30L)).thenReturn(null);

        // Act & Assert
        assertThrows(MateriaNoEncontradaException.class,
                () -> estudianteService.actualizarEstudiante(1L, modificado));
        verify(estudianteRepo, never()).findById(any());
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

    @Test
    @DisplayName("Debe lanzar excepcion si cambia el DNI y ya existe")
    void actualizarEstudiante_DniDuplicado_LanzaExcepcion() {
        // Arrange
        Long id = 1L;
        Estudiante actual = new Estudiante();
        actual.setId(id);
        actual.setDni("12345678X");

        when(estudianteRepo.findById(id)).thenReturn(Optional.of(actual));
        when(estudianteRepo.existsByDniAndIdConvocatoria("22222222B", 1L)).thenReturn(true);

        EstudianteNuevoDto modificado = new EstudianteNuevoDto();
        modificado.setDni("22222222B");

        // Act & Assert
        assertThrows(DniDuplicadoException.class, () -> estudianteService.actualizarEstudiante(id, modificado));
        verify(estudianteRepo, never()).save(any());
    }

    @Test
    @DisplayName("Debe mantener noEliminar en true si ya estaba bloqueado")
    void actualizarEstudiante_NoEliminarSeMantieneTrue() {
        // Arrange
        Long id = 1L;
        Estudiante actual = new Estudiante();
        actual.setId(id);
        actual.setDni("12345678X");
        actual.setNoEliminar(true);
        actual.setCodigoPegatina("PEG1");

        when(estudianteRepo.findById(id)).thenReturn(Optional.of(actual));

        EstudianteNuevoDto modificado = new EstudianteNuevoDto();
        modificado.setDni("12345678X");

        Estudiante entidad = new Estudiante();
        when(mapper.aEntidad(any(EstudianteNuevoDto.class), eq(1L), eq("PEG1"))).thenReturn(entidad);
        when(estudianteRepo.save(any(Estudiante.class))).thenReturn(entidad);

        // Act
        estudianteService.actualizarEstudiante(id, modificado);

        // Assert
        ArgumentCaptor<EstudianteNuevoDto> dtoCaptor = ArgumentCaptor.forClass(EstudianteNuevoDto.class);
        verify(mapper).aEntidad(dtoCaptor.capture(), eq(1L), eq("PEG1"));
        assertEquals(Boolean.TRUE, dtoCaptor.getValue().getNoEliminar());
    }

    @Test
    @DisplayName("Debe consultar estudiantes con convocatoria vigente por defecto")
    void consultarEstudiantes_SinConvocatoria_UsaVigente() {
        // Arrange
        Estudiante estudiante = new Estudiante();
        estudiante.setId(1L);

        when(estudianteRepo.findByIdConvocatoria(1L)).thenReturn(List.of(estudiante));
        when(mapper.aDto(eq(estudiante), any(), any())).thenReturn(new EstudianteDto());

        // Act
        List<EstudianteDto> resultado = estudianteService.consultarEstudiantes(null, null);

        // Assert
        assertEquals(1, resultado.size());
        verify(estudianteRepo, times(1)).findByIdConvocatoria(1L);
        verify(estudianteRepo, never()).findByIdSedeAndIdConvocatoria(any(), any());
    }

    @Test
    @DisplayName("Debe consultar estudiantes filtrando por sede y convocatoria")
    void consultarEstudiantes_ConSede_YConvocatoria() {
        // Arrange
        Estudiante estudiante = new Estudiante();
        estudiante.setId(2L);

        when(estudianteRepo.findByIdSedeAndIdConvocatoria(5L, 7L)).thenReturn(List.of(estudiante));
        when(mapper.aDto(eq(estudiante), any(), any())).thenReturn(new EstudianteDto());

        // Act
        List<EstudianteDto> resultado = estudianteService.consultarEstudiantes(5L, 7L);

        // Assert
        assertEquals(1, resultado.size());
        verify(estudianteRepo, times(1)).findByIdSedeAndIdConvocatoria(5L, 7L);
        verify(estudianteRepo, never()).findByIdConvocatoria(any());
    }

    @Test
    @DisplayName("Debe importar estudiantes cuando el CSV es correcto")
    void importarEstudiantes_Exito() {
        // Arrange
        EstudianteNuevoDto dto = new EstudianteNuevoDto();
        dto.setDni("12345678X");
        NombreCompletoDto nombre = new NombreCompletoDto();
        nombre.setNombre("Ana");
        nombre.setApellido1("Lopez");
        dto.setNombreCompleto(nombre);

        CsvEstudianteParser.EstudianteParseado parseado =
                new CsvEstudianteParser.EstudianteParseado(dto, null, "IES Prueba", List.of("Matematicas"));

        when(csvParser.parsearLineas(any())).thenReturn(List.of(parseado));

        Instituto instituto = Instituto.builder().id(10L).nombre("IES Prueba").build();
        when(institutoRepo.findByNombreIgnoreCase("IES Prueba")).thenReturn(Optional.of(instituto));
        when(institutoRepo.existsById(10L)).thenReturn(true);
        when(institutoRepo.findById(10L)).thenReturn(Optional.of(instituto));

        MateriaDto materia = new MateriaDto();
        materia.setId(20L);
        materia.setNombre("Matematicas");
        when(catalogoClient.buscarMateriaPorNombre("Matematicas")).thenReturn(materia);
        when(catalogoClient.getMateria(20L)).thenReturn(materia);

        when(estudianteRepo.existsByDniAndIdConvocatoria("12345678X", 1L)).thenReturn(false);

        Estudiante entidad = new Estudiante();
        entidad.setId(5L);
        entidad.setIdInstituto(10L);
        entidad.setMateriasMatriculadas(new ArrayList<>(List.of(20L)));
        when(mapper.aEntidad(any(EstudianteNuevoDto.class), eq(1L))).thenReturn(entidad);
        when(estudianteRepo.save(any(Estudiante.class))).thenReturn(entidad);

        EstudianteDto guardado = new EstudianteDto();
        guardado.setId(5L);
        when(mapper.aDto(eq(entidad), any(), any())).thenReturn(guardado);

        // Act
        ImportacionEstudiantes resultado = estudianteService.importarEstudiantes(List.of("linea"));

        // Assert
        assertNotNull(resultado);
        assertEquals(1, resultado.getImportados().size());
        assertEquals(0, resultado.getNoImportados().size());
    }

    @Test
    @DisplayName("Debe registrar el error si el parseo del CSV falla")
    void importarEstudiantes_ErrorParseo() {
        // Arrange
        EstudianteNuevoDto dto = new EstudianteNuevoDto();
        dto.setDni("99999999Z");
        CsvEstudianteParser.EstudianteParseado parseado =
                new CsvEstudianteParser.EstudianteParseado(dto, "Faltan campos obligatorios", null, null);
        when(csvParser.parsearLineas(any())).thenReturn(List.of(parseado));

        // Act
        ImportacionEstudiantes resultado = estudianteService.importarEstudiantes(List.of("linea"));

        // Assert
        assertEquals(0, resultado.getImportados().size());
        assertEquals(1, resultado.getNoImportados().size());
        assertEquals("Faltan campos obligatorios", resultado.getNoImportados().get(0).getProblemaImportacion());
    }

    @Test
    @DisplayName("Debe registrar el error si el instituto no existe en la importacion")
    void importarEstudiantes_InstitutoNoEncontrado() {
        // Arrange
        EstudianteNuevoDto dto = new EstudianteNuevoDto();
        dto.setDni("12345678X");
        CsvEstudianteParser.EstudianteParseado parseado =
                new CsvEstudianteParser.EstudianteParseado(dto, null, "IES Fantasma", List.of());
        when(csvParser.parsearLineas(any())).thenReturn(List.of(parseado));
        when(institutoRepo.findByNombreIgnoreCase("IES Fantasma")).thenReturn(Optional.empty());

        // Act
        ImportacionEstudiantes resultado = estudianteService.importarEstudiantes(List.of("linea"));

        // Assert
        assertEquals(0, resultado.getImportados().size());
        assertEquals(1, resultado.getNoImportados().size());
        assertTrue(resultado.getNoImportados().get(0).getProblemaImportacion().contains("Instituto no encontrado"));
    }

    @Test
    @DisplayName("Debe registrar el error si falta una materia en la importacion")
    void importarEstudiantes_MateriaNoEncontrada() {
        // Arrange
        EstudianteNuevoDto dto = new EstudianteNuevoDto();
        dto.setDni("12345678X");
        CsvEstudianteParser.EstudianteParseado parseado =
                new CsvEstudianteParser.EstudianteParseado(dto, null, "IES Prueba", List.of("Historia"));
        when(csvParser.parsearLineas(any())).thenReturn(List.of(parseado));

        Instituto instituto = Instituto.builder().id(10L).nombre("IES Prueba").build();
        when(institutoRepo.findByNombreIgnoreCase("IES Prueba")).thenReturn(Optional.of(instituto));
        when(catalogoClient.buscarMateriaPorNombre("Historia")).thenReturn(null);

        // Act
        ImportacionEstudiantes resultado = estudianteService.importarEstudiantes(List.of("linea"));

        // Assert
        assertEquals(0, resultado.getImportados().size());
        assertEquals(1, resultado.getNoImportados().size());
        assertTrue(resultado.getNoImportados().get(0).getProblemaImportacion().contains("Materia no encontrada"));
    }
}