package es.uma.informatica.daw.miau.pau_pevau.controllers;

import es.uma.informatica.daw.miau.pau_pevau.exceptions.CsvLecturaException;
import es.uma.informatica.daw.miau.pau_pevau.services.EstudianteService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests Unitarios del Controlador de Estudiantes (CSV)")
class EstudianteControllerUnitTest {

    @Mock
    private EstudianteService estudianteService;

    @InjectMocks
    private EstudianteController estudianteController;

    @Test
    @DisplayName("Debe lanzar excepcion si el CSV no tiene cabecera")
    void importarEstudiantes_SinCabecera_LanzaExcepcion() throws Exception {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getInputStream()).thenReturn(new ByteArrayInputStream(new byte[0]));

        // Act & Assert
        assertThrows(CsvLecturaException.class, () -> estudianteController.importarEstudiantes(file));
    }

    @Test
    @DisplayName("Debe lanzar excepcion si falla la lectura del CSV")
    void importarEstudiantes_ErrorLectura_LanzaExcepcion() throws Exception {
        // Arrange
        MultipartFile file = mock(MultipartFile.class);
        when(file.isEmpty()).thenReturn(false);
        when(file.getInputStream()).thenThrow(new IOException("fallo de lectura"));

        // Act & Assert
        assertThrows(CsvLecturaException.class, () -> estudianteController.importarEstudiantes(file));
    }
}
