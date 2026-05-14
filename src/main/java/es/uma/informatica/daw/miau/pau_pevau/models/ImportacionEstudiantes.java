package es.uma.informatica.daw.miau.pau_pevau.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Schema(name = "ImportacionEstudiantes")
@Data
@NoArgsConstructor
public class ImportacionEstudiantes {
    private List<EstudianteDto> importados;
    private List<ProblemaImportacion> noImportados;
}
