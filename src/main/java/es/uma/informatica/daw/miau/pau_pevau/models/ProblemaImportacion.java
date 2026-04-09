package es.uma.informatica.daw.miau.pau_pevau.models;

import es.uma.informatica.daw.miau.pau_pevau.entities.Estudiante;
import lombok.*;

@EqualsAndHashCode
@Getter
@NoArgsConstructor
@Setter
@ToString
public class ProblemaImportacion {
    private Estudiante estudiante;
    private String problemaImportacion;
}
