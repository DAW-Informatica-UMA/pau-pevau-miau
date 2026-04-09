package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Getter
@Setter
@EqualsAndHashCode
@NoArgsConstructor
public class Estudiante {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Embedded
    private NombreCompleto nombreCompleto;

    @Column(unique = true, nullable = false)
    private String dni;
    private String telefono;
    private String email;

    @ManyToMany
    @JoinTable(
            name = "estudiante_materia",
            joinColumns = @JoinColumn(name = "estudiante_id"),
            inverseJoinColumns = @JoinColumn(name = "materia_id")
    )
    private List<Materia> materiasMatriculadas;

    @ManyToOne private Sede idsede;
    @ManyToOne private Instituto instituto;
    private boolean noEliminar;
}