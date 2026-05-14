package es.uma.informatica.daw.miau.pau_pevau.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Schema(name = "ProblemaImportacion")
@Data
@NoArgsConstructor
public class ProblemaImportacion {
    private EstudianteNuevoDto estudiante;
    private String problemaImportacion;
}
