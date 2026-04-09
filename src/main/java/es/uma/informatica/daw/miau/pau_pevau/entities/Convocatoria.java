package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "convocatoria")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Convocatoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String cursoAcademico;
}
