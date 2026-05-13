package es.uma.informatica.daw.miau.pau_pevau.mappers;

import es.uma.informatica.daw.miau.pau_pevau.entities.Estudiante;
import es.uma.informatica.daw.miau.pau_pevau.models.*;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;

@Component
public class EstudianteMapper {

    public Estudiante aEntidad(EstudianteNuevoDto dto, Long idConvocatoria) {
        if (dto == null) {
            return null;
        }
        
        Estudiante entity = new Estudiante();
        if (dto.getNombreCompleto() != null) {
            entity.setNombre(dto.getNombreCompleto().getNombre());
            entity.setApellido1(dto.getNombreCompleto().getApellido1());
            entity.setApellido2(dto.getNombreCompleto().getApellido2());
        }
        
        entity.setDni(dto.getDni());
        entity.setTelefono(dto.getTelefono());
        entity.setEmail(dto.getEmail());
        
        // Generamos codigo random para la pegatina ya que no se proporciona
        entity.setCodigoPegatina(UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        entity.setNecesidadEspecial(false);
        
        if (dto.getMateriasMatriculadas() != null) {
            entity.setMateriasMatriculadas(new ArrayList<>(dto.getMateriasMatriculadas()));
        }
        
        entity.setIdInstituto(dto.getIdInstituto());
        entity.setIdSede(dto.getIdSede());
        entity.setIdConvocatoria(idConvocatoria);
        entity.setNoEliminar(dto.getNoEliminar() != null ? dto.getNoEliminar() : false);
        
        return entity;
    }

    public EstudianteDto aDto(Estudiante entity, InstitutoDto instituto, Set<MateriaDto> materias) {
        if (entity == null) {
            return null;
        }
        
        EstudianteDto dto = new EstudianteDto();
        dto.setId(entity.getId());
        
        NombreCompletoDto nom = new NombreCompletoDto();
        nom.setNombre(entity.getNombre());
        nom.setApellido1(entity.getApellido1());
        nom.setApellido2(entity.getApellido2());
        dto.setNombreCompleto(nom);
        
        dto.setDni(entity.getDni());
        dto.setTelefono(entity.getTelefono());
        dto.setEmail(entity.getEmail());
        
        dto.setMateriasMatriculadas(materias);
        dto.setIdSede(entity.getIdSede());
        dto.setInstituto(instituto);
        dto.setNoEliminar(entity.isNoEliminar());
        
        return dto;
    }
}
