package es.uma.informatica.daw.miau.pau_pevau.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class EstudianteNuevoDto {
    private NombreCompletoDto nombreCompleto;
    private String dni;
    private String telefono;
    private String email;
    private Set<Long> materiasMatriculadas = new LinkedHashSet<>();
    private Long idInstituto;
    private Long idSede;
    private Boolean noEliminar;
}
