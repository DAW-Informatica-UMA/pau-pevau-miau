package es.uma.informatica.daw.miau.pau_pevau.services;

import es.uma.informatica.daw.miau.pau_pevau.exceptions.CsvLecturaException;
import es.uma.informatica.daw.miau.pau_pevau.models.EstudianteNuevoDto;
import es.uma.informatica.daw.miau.pau_pevau.models.NombreCompletoDto;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
public class CsvEstudianteParser {

    public List<EstudianteParseado> parsearLineas(List<String> lineasCsv) {
        List<EstudianteParseado> resultado = new ArrayList<>();
        
        for (String linea : lineasCsv) {
            String[] columnas = linea.split(";");
            
            // Validamos que los campos obligatorios estén presentes
            if (columnas.length < 5 || columnas[0].trim().isEmpty() || columnas[1].trim().isEmpty() || 
                columnas[4].trim().isEmpty()) {
                
                EstudianteNuevoDto dtoFaltante = new EstudianteNuevoDto();
                dtoFaltante.setDni(columnas.length > 4 ? columnas[4].trim() : "DESCONOCIDO");
                resultado.add(new EstudianteParseado(dtoFaltante, "Faltan campos obligatorios", null, null));
                continue;
            }

            // Extraer datos del CSV a DTO
            EstudianteNuevoDto dtoNuevo = new EstudianteNuevoDto();
            dtoNuevo.setDni(columnas[4].trim());
            
            NombreCompletoDto nb = new NombreCompletoDto();
            nb.setNombre(columnas[1].trim());
            nb.setApellido1(columnas[2].trim());
            if (columnas.length > 3) {
                nb.setApellido2(columnas[3].trim());
            }
            dtoNuevo.setNombreCompleto(nb);
            
            String nombreInstituto = columnas[0].trim();
            List<String> nombresMaterias = new ArrayList<>();
            if (columnas.length >= 6 && !columnas[5].trim().isEmpty()) {
                for (String m : columnas[5].split(",")) {
                    nombresMaterias.add(m.trim());
                }
            }
            
            resultado.add(new EstudianteParseado(dtoNuevo, null, nombreInstituto, nombresMaterias));
        }
        
        return resultado;
    }
    
    public static class EstudianteParseado {
        public EstudianteNuevoDto dto;
        public String errorParseo;
        public String nombreInstituto;
        public List<String> nombresMaterias;
        
        public EstudianteParseado(EstudianteNuevoDto dto, String errorParseo, String nombreInstituto, List<String> nombresMaterias) {
            this.dto = dto;
            this.errorParseo = errorParseo;
            this.nombreInstituto = nombreInstituto;
            this.nombresMaterias = nombresMaterias;
        }
    }
}