package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Data @NoArgsConstructor
public class Aula {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private Integer aforo;
    private String disponibilidad;
    @ManyToOne private Sede sede;
}
