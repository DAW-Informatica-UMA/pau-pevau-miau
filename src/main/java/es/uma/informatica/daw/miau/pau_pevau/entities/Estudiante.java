package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(
    name = "estudiante",
    uniqueConstraints = {
        @UniqueConstraint(name = "uk_estudiante_dni_convocatoria", columnNames = {"dni", "convocatoria_id"}),
        @UniqueConstraint(name = "uk_estudiante_pegatina_convocatoria", columnNames = {"codigo_pegatina", "convocatoria_id"})
    }
)
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"dni", "idConvocatoria"})
public class Estudiante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String apellido1;

    private String apellido2;

    @Column(nullable = false)
    private String dni;

    private String telefono;

    private String email;

    @Column(name = "necesidad_especial", nullable = false)
    private boolean necesidadEspecial;

    @Column(name = "codigo_pegatina", nullable = false)
    private String codigoPegatina;

    @ElementCollection
    @CollectionTable(name = "estudiante_materia", joinColumns = @JoinColumn(name = "estudiante_id"))
    @Column(name = "materia_id", nullable = false)
    private List<Long> materiasMatriculadas = new ArrayList<>();

    @Column(name = "instituto_id", nullable = false)
    private Long idInstituto;

    @Column(name = "sede_id")
    private Long idSede;

    @Column(name = "convocatoria_id", nullable = false)
    private Long idConvocatoria;

    @Column(name = "no_eliminar", nullable = false)
    private boolean noEliminar;
}
