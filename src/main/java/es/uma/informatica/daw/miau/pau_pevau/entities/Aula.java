package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@NoArgsConstructor
@Setter
@ToString
public class Aula {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private Integer aforo;
    private String disponibilidad;
    @ManyToOne private Sede sede;
}
