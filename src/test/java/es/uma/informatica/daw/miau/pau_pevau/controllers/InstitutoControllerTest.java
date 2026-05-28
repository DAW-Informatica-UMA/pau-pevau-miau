package es.uma.informatica.daw.miau.pau_pevau.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import es.uma.informatica.daw.miau.pau_pevau.models.InstitutoDto;
import es.uma.informatica.daw.miau.pau_pevau.models.InstitutoNuevoDto;
import es.uma.informatica.daw.miau.pau_pevau.services.InstitutoService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(InstitutoController.class)
@AutoConfigureMockMvc(addFilters = false)
@DisplayName("Tests del Controlador REST de Institutos")
class InstitutoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @MockitoBean
    private InstitutoService institutoService;

    @Test
    @DisplayName("GET /institutos - Debe devolver 200 OK y la lista de institutos")
    @WithMockUser(roles = "ADMINISTRADOR")
    void getInstitutos_Exito() throws Exception {
        // Arrange
        InstitutoDto dto = new InstitutoDto();
        dto.setId(1L);
        dto.setNombre("IES Prueba");
        when(institutoService.consultarInstitutos()).thenReturn(List.of(dto));

        // Act & Assert
        mockMvc.perform(get("/institutos")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].nombre").value("IES Prueba"));
    }

    @Test
    @DisplayName("GET /institutos/{id} - Debe devolver 200 OK y el instituto solicitado")
    @WithMockUser(roles = "ADMINISTRADOR")
    void getInstituto_Exito() throws Exception {
        // Arrange
        Long id = 1L;
        InstitutoDto dto = new InstitutoDto();
        dto.setId(id);
        dto.setNombre("IES Prueba");
        when(institutoService.consultarInstituto(id)).thenReturn(dto);

        // Act & Assert
        mockMvc.perform(get("/institutos/{idInstituto}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("IES Prueba"));
    }

    @Test
    @DisplayName("GET /institutos/{id} - Debe devolver 404 si no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void getInstituto_NoExiste() throws Exception {
    // Arrange
    Long id = 99L;
    when(institutoService.consultarInstituto(id))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Instituto no encontrado"));

    // Act & Assert
    mockMvc.perform(get("/institutos/{idInstituto}", id)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("POST /institutos - Debe devolver 201 Created al crear un instituto")
    @WithMockUser(roles = "ADMINISTRADOR")
    void createInstituto_Exito() throws Exception {
        // Arrange
        InstitutoNuevoDto nuevoDto = new InstitutoNuevoDto();
        nuevoDto.setNombre("IES Nuevo");

        InstitutoDto guardadoDto = new InstitutoDto();
        guardadoDto.setId(1L);
        guardadoDto.setNombre("IES Nuevo");

        when(institutoService.crearInstituto(any(InstitutoNuevoDto.class))).thenReturn(guardadoDto);

        // Act & Assert
        mockMvc.perform(post("/institutos")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(nuevoDto)))
                .andExpect(status().isCreated())
                .andExpect(header().exists("Location"))
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("PUT /institutos/{id} - Debe devolver 200 OK al actualizar un instituto")
    @WithMockUser(roles = "ADMINISTRADOR")
    void updateInstituto_Exito() throws Exception {
        // Arrange
        Long id = 1L;
        InstitutoNuevoDto updateDto = new InstitutoNuevoDto();
        updateDto.setNombre("IES Actualizado");

        InstitutoDto actualizadoDto = new InstitutoDto();
        actualizadoDto.setId(id);
        actualizadoDto.setNombre("IES Actualizado");

        when(institutoService.actualizarInstituto(eq(id), any(InstitutoNuevoDto.class))).thenReturn(actualizadoDto);

        // Act & Assert
        mockMvc.perform(put("/institutos/{idInstituto}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("IES Actualizado"));
    }

    @Test
    @DisplayName("PUT /institutos/{id} - Debe devolver 404 si no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void updateInstituto_NoExiste() throws Exception {
    // Arrange
    Long id = 99L;
    InstitutoNuevoDto updateDto = new InstitutoNuevoDto();
    updateDto.setNombre("IES Nuevo");

    when(institutoService.actualizarInstituto(eq(id), any(InstitutoNuevoDto.class)))
        .thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Instituto no encontrado"));

    // Act & Assert
    mockMvc.perform(put("/institutos/{idInstituto}", id)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(updateDto)))
        .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /institutos/{id} - Debe devolver 200 OK al eliminar correctamente")
    @WithMockUser(roles = "ADMINISTRADOR")
    void deleteInstituto_Exito() throws Exception {
        // Arrange
        Long id = 1L;
        // Act & Assert
        mockMvc.perform(delete("/institutos/{idInstituto}", id)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("DELETE /institutos/{id} - Debe devolver 404 si no existe")
    @WithMockUser(roles = "ADMINISTRADOR")
    void deleteInstituto_NoExiste() throws Exception {
    // Arrange
    Long id = 99L;
    doThrow(new ResponseStatusException(HttpStatus.NOT_FOUND, "Instituto no encontrado"))
        .when(institutoService).eliminarInstituto(id);

    // Act & Assert
    mockMvc.perform(delete("/institutos/{idInstituto}", id)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("DELETE /institutos/{id} - Debe devolver 409 si tiene estudiantes")
    @WithMockUser(roles = "ADMINISTRADOR")
    void deleteInstituto_ConEstudiantes() throws Exception {
    // Arrange
    Long id = 1L;
    doThrow(new ResponseStatusException(HttpStatus.CONFLICT, "Instituto con estudiantes"))
        .when(institutoService).eliminarInstituto(id);

    // Act & Assert
    mockMvc.perform(delete("/institutos/{idInstituto}", id)
            .contentType(MediaType.APPLICATION_JSON))
        .andExpect(status().isConflict());
    }
}