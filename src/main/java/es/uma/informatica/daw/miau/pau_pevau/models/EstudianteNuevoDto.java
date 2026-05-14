package es.uma.informatica.daw.miau.pau_pevau.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.LinkedHashSet;
import java.util.Set;

@Schema(name = "EstudianteNuevo")
@Data
@NoArgsConstructor
public class EstudianteNuevoDto {
    @NotNull
    @Valid
    private NombreCompletoDto nombreCompleto;
    
    @NotBlank
    private String dni;
    
    private String telefono;
    private String email;
    
    private Set<Long> materiasMatriculadas = new LinkedHashSet<>();
    
    @NotNull
    private Long idInstituto;
    
    private Long idSede;
    private Boolean noEliminar;
}
