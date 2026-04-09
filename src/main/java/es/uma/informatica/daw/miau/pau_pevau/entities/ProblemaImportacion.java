package es.uma.informatica.daw.miau.pau_pevau.entities;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Setter
@Getter
@EqualsAndHashCode
@NoArgsConstructor
public class ProblemaImportacion {
    @ManyToOne
    @JoinColumn(name = "estudiante_id")
    private Estudiante estudiante;
    private String problemaImportacion;
}
