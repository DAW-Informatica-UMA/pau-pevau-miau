package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Entity
@Data
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
    private boolean noEliminar = false;
	
	@ManyToMany
	@JoinTable(
		name = "estudiante_materia", 
		joinColumns = @JoinColumn(name = "estudiante_id"), 
		inverseJoinColumns = @JoinColumn(name = "materia_id"))
	private List<Materia> materiasMatriculadas;
}