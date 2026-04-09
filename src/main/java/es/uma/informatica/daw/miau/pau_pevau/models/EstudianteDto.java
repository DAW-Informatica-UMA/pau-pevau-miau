package es.uma.informatica.daw.miau.pau_pevau.models;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
@NoArgsConstructor
public class EstudianteDto {
    private Long id;
    private NombreCompletoDto nombreCompleto;
    private String dni;
    private String telefono;
    private String email;
    private Set<MateriaDto> materiasMatriculadas = new LinkedHashSet<>();
    private Long idSede;
    private InstitutoDto instituto;
    private Boolean noEliminar;
}
