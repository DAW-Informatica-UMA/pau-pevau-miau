package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.Entity;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.util.List;

@Entity
@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class ImportacionEstudiantes {
    private List<Estudiante> importados;
    private List<ProblemaImportacion> noImportados;
}
