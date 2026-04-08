package es.uma.informatica.daw.miau.pau_pevau.entities;
import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
@Entity
@Data
@NoArgsConstructor
public class Materia {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String nombre;

    private boolean eliminada;
    @ManyToMany(mappedBy = "materiasMatriculadas")
    private List<Estudiante> estudiantes;
}