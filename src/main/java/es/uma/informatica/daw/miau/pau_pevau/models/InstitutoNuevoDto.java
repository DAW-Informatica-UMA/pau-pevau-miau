package es.uma.informatica.daw.miau.pau_pevau.models;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(name = "InstitutoNuevo")
@Data
@NoArgsConstructor
public class InstitutoNuevoDto {
    private String nombre;
    private String direccion1;
    private String direccion2;
    private String localidad;
    private Integer codigoPostal;
    private String pais;
}