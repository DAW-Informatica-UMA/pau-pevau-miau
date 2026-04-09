package es.uma.informatica.daw.miau.pau_pevau.models;

import es.uma.informatica.daw.miau.pau_pevau.entities.Estudiante;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Setter
@Getter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class ImportacionEstudiantes {
    private List<Estudiante> importados;
    private List<ProblemaImportacion> noImportados;
}
