package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@NoArgsConstructor
@Setter
@ToString
public class Instituto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String nombre;
    private String direccion1;
    private String direccion2;
    private String localidad;
    private Integer codigoPostal;
    private String pais;
}
