package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.*;
import java.util.List;

import lombok.*;

@Entity
@Getter
@Setter
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
public class Instituto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @EqualsAndHashCode.Include
    private Long id;
    private String nombre;
    private String direccion1;
    private String direccion2;
    private String localidad;
    private Integer codigoPostal;
    private String pais;
}
