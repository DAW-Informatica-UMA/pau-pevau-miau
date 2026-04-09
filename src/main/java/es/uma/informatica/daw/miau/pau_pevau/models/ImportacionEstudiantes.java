package es.uma.informatica.daw.miau.pau_pevau.models;

import es.uma.informatica.daw.miau.pau_pevau.entities.Estudiante;
import lombok.*;
import java.util.List;

@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
public class ImportacionEstudiantes {
    private List<Estudiante> importados;
    private List<ProblemaImportacion> noImportados;
}
