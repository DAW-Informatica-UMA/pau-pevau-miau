package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity @Data @NoArgsConstructor
public class Vicerrectorado {
    @Id @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    private String email;
}
