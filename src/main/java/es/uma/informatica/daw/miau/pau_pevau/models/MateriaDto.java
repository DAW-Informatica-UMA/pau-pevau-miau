package es.uma.informatica.daw.miau.pau_pevau.models;

import lombok.Data;
import lombok.NoArgsConstructor;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Materia")
@Data
@NoArgsConstructor
public class MateriaDto {
    private Long id;
    private String nombre;
    private Boolean eliminada;
}
