package es.uma.informatica.daw.miau.pau_pevau.models;

import lombok.*;
import java.util.List;

@Data
@NoArgsConstructor
public class ImportacionEstudiantes {
    private List<EstudianteDto> importados;
    private List<ProblemaImportacion> noImportados;
}
