package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Data @NoArgsConstructor
public class Corrector {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Embedded private NombreCompleto nombreCompleto;
    private String email;
    private String telefonoMovil;
    private String materiaEspecialista;
    private Integer maxExamenes;
}
