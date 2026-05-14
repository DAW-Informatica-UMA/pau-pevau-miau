package es.uma.informatica.daw.miau.pau_pevau.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @Pattern(regexp="^[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]$", message="Formato de DNI inválido")
    @Schema(description = "DNI del estudiante", example = "12345678Z", pattern = "^[0-9]{8}[TRWAGMYFPDXBNJZSQVHLCKE]$")
    private String dni;
    
    @Pattern(regexp="^[0-9]{9}$", message="El teléfono debe tener 9 dígitos")
    @Schema(description = "Teléfono de contacto", example = "600123456")
    private String telefono;
    
    @Email(message="Formato de email inválido")
    @Schema(description = "Correo electrónico", example = "estudiante@ejemplo.com")
    private String email;
    
    private Set<Long> materiasMatriculadas = new LinkedHashSet<>();
    
    @NotNull
    private Long idInstituto;
    
    private Long idSede;
    private Boolean noEliminar;
}
