package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.*;
import lombok.*;
import java.util.List;

@Entity
@EqualsAndHashCode(of = "id")
@Data
@NoArgsConstructor
public class Sede {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nombre;
    private String direccion;
    private String coordenadasGPS; // Para latitud/longitud
    @ToString.Exclude
    @OneToMany(mappedBy = "sede")
    private List<Aula> aulas;
}
