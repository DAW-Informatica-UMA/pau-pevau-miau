package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Data
@NoArgsConstructor
public class NombreCompleto {
    private String apellido1;
    private String apellido2;
    private String nombre;
}