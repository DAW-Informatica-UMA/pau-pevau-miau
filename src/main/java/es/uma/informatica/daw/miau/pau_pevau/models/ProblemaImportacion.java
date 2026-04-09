package es.uma.informatica.daw.miau.pau_pevau.models;

import es.uma.informatica.daw.miau.pau_pevau.entities.Estudiante;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class ProblemaImportacion {
    private Estudiante estudiante;
    private String problemaImportacion;
}
