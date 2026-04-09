package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.Embeddable;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class NombreCompleto {
    private String apellido1;
    private String apellido2;
    private String nombre;
}