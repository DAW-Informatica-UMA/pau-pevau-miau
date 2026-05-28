package es.uma.informatica.daw.miau.pau_pevau.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.DniDuplicadoException;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.EstudianteBloqueadoException;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.EstudianteNoEncontradoException;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.GlobalExceptionHandler;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.InstitutoNoEncontradoException;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.MateriaNoEncontradaException;
import es.uma.informatica.daw.miau.pau_pevau.models.EstudianteDto;
import es.uma.informatica.daw.miau.pau_pevau.models.NombreCompletoDto;
import es.uma.informatica.daw.miau.pau_pevau.services.EstudianteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import es.uma.informatica.daw.miau.pau_pevau.models.EstudianteNuevoDto;


import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;

@WebMvcTest(EstudianteController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
@DisplayName("Tests del Controlador REST de Estudiantes")
class EstudianteControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private EstudianteService estudianteService;

    @Test
    @DisplayName("GET /estudiantes/{id} - Debe devolver 200 OK y el estudiante solicitado")
    @WithMockUser(roles = "ADMINISTRADOR")
    void getEstudiante_Exito() throws Exception {
        // Arrange
        Long id = 1L;
        EstudianteDto dto = new EstudianteDto();
        dto.setId(id);
        dto.setDni("12345678X");

        NombreCompletoDto nombreDto = new NombreCompletoDto();
        nombreDto.setNombre("Mihai");
        dto.setNombreCompleto(nombreDto);

        when(estudianteService.consultarEstudiante(id)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/estudiantes/{idEstudiante}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value("12345678X"))
                .andExpect(jsonPath("$.nombreCompleto.nombre").value("Mihai"));
    }

    @Test
    @DisplayName("GET /estudiantes/{id} - Debe devolver 404 si no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void getEstudiante_NoExiste() throws Exception {
        // Arrange
        Long id = 99L;
        when(estudianteService.consultarEstudiante(id))
                .thenThrow(new EstudianteNoEncontradoException("Estudiante no encontrado"));

        // Act & Assert
        mockMvc.perform(get("/estudiantes/{idEstudiante}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /estudiantes/{id} - Debe devolver 200 OK al eliminar correctamente")
    @WithMockUser(roles = "ADMINISTRADOR")
    void deleteEstudiante_Exito() throws Exception {
        // Arrange
        Long id = 1L;

        // Act & Assert
        mockMvc.perform(delete("/estudiantes/{idEstudiante}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /estudiantes/{id} - Debe devolver 404 si no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void deleteEstudiante_NoExiste() throws Exception {
        // Arrange
        Long id = 99L;
        doThrow(new EstudianteNoEncontradoException("Estudiante no encontrado"))
                .when(estudianteService).eliminarEstudiante(id);

        // Act & Assert
        mockMvc.perform(delete("/estudiantes/{idEstudiante}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /estudiantes/{id} - Debe devolver 409 si esta bloqueado")
    @WithMockUser(roles = "ADMINISTRADOR")
    void deleteEstudiante_Bloqueado() throws Exception {
        // Arrange
        Long id = 1L;
        doThrow(new EstudianteBloqueadoException("Estudiante bloqueado"))
                .when(estudianteService).eliminarEstudiante(id);

        // Act & Assert
        mockMvc.perform(delete("/estudiantes/{idEstudiante}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("POST /estudiantes - Debe devolver 201 Created al crear un estudiante")
    @WithMockUser(roles = "ADMINISTRADOR")
    void createEstudiante_Exito() throws Exception {
        // Arrange
        NombreCompletoDto nombreDto = new NombreCompletoDto();
        nombreDto.setNombre("Carlos");
        nombreDto.setApellido1("Ruiz");

        EstudianteNuevoDto nuevoDto = new EstudianteNuevoDto();
        nuevoDto.setDni("87654321Y");
        nuevoDto.setIdInstituto(1L);
        nuevoDto.setNombreCompleto(nombreDto);

        EstudianteDto guardadoDto = new EstudianteDto();
        guardadoDto.setId(1L);
        guardadoDto.setDni("87654321Y");

        when(estudianteService.crearEstudiante(any(EstudianteNuevoDto.class))).thenReturn(guardadoDto);

        // Act & Assert
        mockMvc.perform(post("/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("POST /estudiantes - Debe devolver 400 si faltan campos obligatorios")
    @WithMockUser(roles = "ADMINISTRADOR")
    void createEstudiante_DatosInvalidos() throws Exception {
        // Arrange
        EstudianteNuevoDto nuevoDto = new EstudianteNuevoDto();

        // Act & Assert
        mockMvc.perform(post("/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoDto)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("POST /estudiantes - Debe devolver 404 si el instituto no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void createEstudiante_InstitutoNoExiste() throws Exception {
        // Arrange
        NombreCompletoDto nombreDto = new NombreCompletoDto();
        nombreDto.setNombre("Carlos");
        nombreDto.setApellido1("Ruiz");

        EstudianteNuevoDto nuevoDto = new EstudianteNuevoDto();
        nuevoDto.setDni("87654321Y");
        nuevoDto.setIdInstituto(99L);
        nuevoDto.setNombreCompleto(nombreDto);

        when(estudianteService.crearEstudiante(any(EstudianteNuevoDto.class)))
                .thenThrow(new InstitutoNoEncontradoException("Instituto no encontrado"));

        // Act & Assert
        mockMvc.perform(post("/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /estudiantes - Debe devolver 409 Conflict si el DNI ya existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void createEstudiante_DniDuplicado_LanzaConflicto() throws Exception {
        // Arrange
        NombreCompletoDto nombreDto = new NombreCompletoDto();
        nombreDto.setNombre("Ana");
        nombreDto.setApellido1("Gómez");

        EstudianteNuevoDto nuevoDto = new EstudianteNuevoDto();
        nuevoDto.setDni("12345678X");
        nuevoDto.setIdInstituto(1L);
        nuevoDto.setNombreCompleto(nombreDto);

        when(estudianteService.crearEstudiante(any(EstudianteNuevoDto.class)))
                .thenThrow(new es.uma.informatica.daw.miau.pau_pevau.exceptions.DniDuplicadoException("El DNI ya existe"));

        // Act & Assert
        mockMvc.perform(post("/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /estudiantes/{id} - Debe devolver 200 OK al actualizar un estudiante")
    @WithMockUser(roles = "ADMINISTRADOR")
    void updateEstudiante_Exito() throws Exception {
        // Arrange
        Long id = 1L;
        NombreCompletoDto nombreDto = new NombreCompletoDto();
        nombreDto.setNombre("Luis");
        nombreDto.setApellido1("Martínez");

        EstudianteNuevoDto updateDto = new EstudianteNuevoDto();
        updateDto.setDni("11111111A");
        updateDto.setIdInstituto(1L);
        updateDto.setNombreCompleto(nombreDto);

        EstudianteDto actualizadoDto = new EstudianteDto();
        actualizadoDto.setId(id);
        actualizadoDto.setDni("11111111A");

        when(estudianteService.actualizarEstudiante(eq(id), any(EstudianteNuevoDto.class))).thenReturn(actualizadoDto);

        // Act & Assert
        mockMvc.perform(put("/estudiantes/{idEstudiante}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.dni").value("11111111A"));
    }

    @Test
    @DisplayName("PUT /estudiantes/{id} - Debe devolver 404 si no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void updateEstudiante_NoExiste() throws Exception {
        // Arrange
        Long id = 99L;
                NombreCompletoDto nombreDto = new NombreCompletoDto();
                nombreDto.setNombre("Luis");
                nombreDto.setApellido1("Martinez");

        EstudianteNuevoDto updateDto = new EstudianteNuevoDto();
        updateDto.setDni("11111111A");
                updateDto.setIdInstituto(1L);
                updateDto.setNombreCompleto(nombreDto);

        when(estudianteService.actualizarEstudiante(eq(id), any(EstudianteNuevoDto.class)))
                .thenThrow(new EstudianteNoEncontradoException("Estudiante no encontrado"));

        // Act & Assert
        mockMvc.perform(put("/estudiantes/{idEstudiante}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("PUT /estudiantes/{id} - Debe devolver 409 si hay conflicto de DNI")
    @WithMockUser(roles = "ADMINISTRADOR")
    void updateEstudiante_DniDuplicado() throws Exception {
        // Arrange
        Long id = 1L;
                NombreCompletoDto nombreDto = new NombreCompletoDto();
                nombreDto.setNombre("Luis");
                nombreDto.setApellido1("Martinez");

        EstudianteNuevoDto updateDto = new EstudianteNuevoDto();
        updateDto.setDni("11111111A");
                updateDto.setIdInstituto(1L);
                updateDto.setNombreCompleto(nombreDto);

        when(estudianteService.actualizarEstudiante(eq(id), any(EstudianteNuevoDto.class)))
                .thenThrow(new DniDuplicadoException("DNI duplicado"));

        // Act & Assert
        mockMvc.perform(put("/estudiantes/{idEstudiante}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /estudiantes/{id} - Debe devolver 409 si el estudiante está si ya existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void updateEstudiante_Existente() throws Exception {
        // Arrange
        Long id = 1L;
        NombreCompletoDto nombreDto = new NombreCompletoDto();
        nombreDto.setNombre("Luis");
        nombreDto.setApellido1("Martinez");

        EstudianteNuevoDto updateDto = new EstudianteNuevoDto();
        updateDto.setDni("11111111A");
        updateDto.setIdInstituto(1L);
        updateDto.setNombreCompleto(nombreDto);

        when(estudianteService.actualizarEstudiante(eq(id), any(EstudianteNuevoDto.class)))
                .thenThrow(new EstudianteBloqueadoException("Estudiante bloqueado"));

        // Act & Assert
        mockMvc.perform(put("/estudiantes/{idEstudiante}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isConflict());
    }

    @Test
    @DisplayName("PUT /estudiantes/{id} - Debe devolver 404 si alguna materia no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void updateEstudiante_MateriaNoExiste() throws Exception {
        // Arrange
        Long id = 1L;
        NombreCompletoDto nombreDto = new NombreCompletoDto();
        nombreDto.setNombre("Luis");
        nombreDto.setApellido1("Martinez");

        EstudianteNuevoDto updateDto = new EstudianteNuevoDto();
        updateDto.setDni("11111111A");
        updateDto.setIdInstituto(1L);
        updateDto.setNombreCompleto(nombreDto);

        when(estudianteService.actualizarEstudiante(eq(id), any(EstudianteNuevoDto.class)))
                .thenThrow(new MateriaNoEncontradaException("Materia no encontrada"));

        // Act & Assert
        mockMvc.perform(put("/estudiantes/{idEstudiante}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("GET /estudiantes - Debe devolver 200 OK y la lista de estudiantes")
    @WithMockUser(roles = "ADMINISTRADOR")
    void getEstudiantes_Exito() throws Exception {
        EstudianteDto dto = new EstudianteDto();
        dto.setId(1L);
        dto.setDni("12345678X");

        when(estudianteService.consultarEstudiantes(any(), any())).thenReturn(java.util.List.of(dto));

        mockMvc.perform(get("/estudiantes")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].dni").value("12345678X"));
    }

        @Test
        @DisplayName("GET /estudiantes - Debe usar los parámetros de consulta")
        @WithMockUser(roles = "ADMINISTRADOR")
        void getEstudiantes_ConParametros() throws Exception {
                // Arrange
                EstudianteDto dto = new EstudianteDto();
                dto.setId(2L);
                dto.setDni("87654321Y");

                when(estudianteService.consultarEstudiantes(eq(5L), eq(7L))).thenReturn(java.util.List.of(dto));

                // Act & Assert
                mockMvc.perform(get("/estudiantes")
                                                .param("idSede", "5")
                                                .param("idConvocatoria", "7")
                                                .contentType(MediaType.APPLICATION_JSON))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$[0].dni").value("87654321Y"));
        }

    @Test
    @DisplayName("POST /estudiantes/upload - Debe devolver 200 OK al subir el CSV")
    @WithMockUser(roles = "ADMINISTRADOR")
    void importarEstudiantes_Exito() throws Exception {
        org.springframework.mock.web.MockMultipartFile file =
                new org.springframework.mock.web.MockMultipartFile(
                        "ficheroEstudiantes",
                        "estudiantes.csv",
                        "text/csv",
                        "Instituto;Nombre;Apellido1;Apellido2;DNI;Materias\nIES Prueba;Carlos;Ruiz;;12345678X;".getBytes()
                );

        es.uma.informatica.daw.miau.pau_pevau.models.ImportacionEstudiantes importacion =
                new es.uma.informatica.daw.miau.pau_pevau.models.ImportacionEstudiantes();
        importacion.setImportados(java.util.List.of());
        importacion.setNoImportados(java.util.List.of());

        when(estudianteService.importarEstudiantes(any())).thenReturn(importacion);

        mockMvc.perform(multipart("/estudiantes/upload")
                        .file(file))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("POST /estudiantes/upload - Debe devolver 400 si el CSV esta vacio")
    @WithMockUser(roles = "ADMINISTRADOR")
    void importarEstudiantes_ArchivoVacio() throws Exception {
        org.springframework.mock.web.MockMultipartFile file =
                new org.springframework.mock.web.MockMultipartFile(
                        "ficheroEstudiantes",
                        "estudiantes.csv",
                        "text/csv",
                        new byte[0]
                );

        mockMvc.perform(multipart("/estudiantes/upload")
                        .file(file))
                .andExpect(status().isBadRequest());
    }


}
