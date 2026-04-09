package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@Getter
@NoArgsConstructor
@Setter
@ToString
public class Materia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    private String nombre;
    private boolean eliminada;
    @ToString.Exclude
    @ManyToMany(mappedBy = "materiasMatriculadas")
    private List<Estudiante> estudiantes;
}