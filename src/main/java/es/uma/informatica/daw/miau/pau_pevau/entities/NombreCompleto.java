package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
public class NombreCompleto {
    private String nombre;
    private String apellido1;
    private String apellido2;
}