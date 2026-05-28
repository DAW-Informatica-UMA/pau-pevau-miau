package es.uma.informatica.daw.miau.pau_pevau.services;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

@DisplayName("Tests Unitarios del Parser CSV de Estudiantes")
class CsvEstudianteParserTest {

    private final CsvEstudianteParser parser = new CsvEstudianteParser();

    @Test
    @DisplayName("Debe parsear una linea completa correctamente")
    void parsearLineas_ConDatos_Exito() {
        // Arrange
        String linea = "IES Prueba;Carlos;Ruiz;Sanchez;12345678X;Matematicas,Fisica";

        // Act
        List<CsvEstudianteParser.EstudianteParseado> resultado = parser.parsearLineas(List.of(linea));

        // Assert
        assertEquals(1, resultado.size());
        CsvEstudianteParser.EstudianteParseado item = resultado.get(0);
        assertNull(item.errorParseo);
        assertEquals("IES Prueba", item.nombreInstituto);
        assertEquals(List.of("Matematicas", "Fisica"), item.nombresMaterias);
        assertEquals("12345678X", item.dto.getDni());
        assertNotNull(item.dto.getNombreCompleto());
        assertEquals("Carlos", item.dto.getNombreCompleto().getNombre());
        assertEquals("Ruiz", item.dto.getNombreCompleto().getApellido1());
        assertEquals("Sanchez", item.dto.getNombreCompleto().getApellido2());
    }

    @Test
    @DisplayName("Debe registrar error si faltan campos obligatorios")
    void parsearLineas_FaltanCampos_Error() {
        // Arrange
        String linea = "IES Prueba;Carlos;Ruiz";

        // Act
        List<CsvEstudianteParser.EstudianteParseado> resultado = parser.parsearLineas(List.of(linea));

        // Assert
        assertEquals(1, resultado.size());
        CsvEstudianteParser.EstudianteParseado item = resultado.get(0);
        assertEquals("Faltan campos obligatorios", item.errorParseo);
        assertEquals("DESCONOCIDO", item.dto.getDni());
    }
}
