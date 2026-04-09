package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "estudiante")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido1;

    private String apellido2;

    @Column(unique = true, nullable = false)
    private String dni;

    private String telefono;

    private String email;
    private boolean necesidadEspecial;

    @Column(unique = true, nullable = false)
    private String codigoPegatina;

    @ElementCollection
    @CollectionTable(name = "estudiante_materia", joinColumns = @JoinColumn(name = "estudiante_id"))
    @Column(name = "materia_id", nullable = false)
    private List<Long> materiasMatriculadas = new ArrayList<>();

    @Column(name = "instituto_id", nullable = false)
    private Long idInstituto;

    @Column(name = "sede_id")
    private Long idSede;

    @ManyToOne
    @JoinColumn(name = "convocatoria_id", nullable = false)
    private Convocatoria convocatoria;
    private boolean noEliminar;
}
