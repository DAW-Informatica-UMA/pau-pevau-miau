package es.uma.informatica.daw.miau.pau_pevau.models;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "NombreCompleto")
@Data
@NoArgsConstructor
public class NombreCompletoDto {
    @NotBlank
    private String apellido1;
    private String apellido2;
    @NotBlank
    private String nombre;
}
