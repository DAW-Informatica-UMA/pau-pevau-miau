package es.uma.informatica.daw.miau.pau_pevau.models;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MateriaDto {
    private Long id;
    private String nombre;
    private Boolean eliminada;
}
