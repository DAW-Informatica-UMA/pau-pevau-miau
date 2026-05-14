package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "instituto")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Instituto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    private String direccion1;
    private String direccion2;
    private String localidad;

    @Column(name = "codigo_postal")
    private Integer codigoPostal;

    private String pais;
}