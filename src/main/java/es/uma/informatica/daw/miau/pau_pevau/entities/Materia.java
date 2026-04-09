package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@EqualsAndHashCode(of = "id")
@Data
@NoArgsConstructor
public class Materia {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private boolean eliminada;
    @ToString.Exclude
    @ManyToMany(mappedBy = "materiasMatriculadas")
    private List<Estudiante> estudiantes;
}