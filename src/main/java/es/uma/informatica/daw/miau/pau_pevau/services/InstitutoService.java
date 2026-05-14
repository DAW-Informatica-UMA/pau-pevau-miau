package es.uma.informatica.daw.miau.pau_pevau.services;

import es.uma.informatica.daw.miau.pau_pevau.entities.Instituto;
import es.uma.informatica.daw.miau.pau_pevau.models.InstitutoDto;
import es.uma.informatica.daw.miau.pau_pevau.models.InstitutoNuevoDto;
import es.uma.informatica.daw.miau.pau_pevau.repositories.EstudianteRepository;
import es.uma.informatica.daw.miau.pau_pevau.repositories.InstitutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class InstitutoService {

    private final InstitutoRepository institutoRepo;
    private final EstudianteRepository estudianteRepo;

    @Transactional(readOnly = true)
    public List<InstitutoDto> consultarInstitutos() {
        List<Instituto> entities = institutoRepo.findAll();
        List<InstitutoDto> result = new ArrayList<>();
        for (Instituto entity : entities) {
            result.add(entityToDto(entity));
        }
        return result;
    }

    @Transactional(readOnly = true)
    public InstitutoDto consultarInstituto(Long idInstituto) {
        Instituto current = institutoRepo.findById(idInstituto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instituto no encontrado"));
        return entityToDto(current);
    }

    public InstitutoDto crearInstituto(InstitutoNuevoDto dto) {
        Instituto entity = dtoToEntity(dto);
        Instituto saved = institutoRepo.save(entity);
        return entityToDto(saved);
    }

    public InstitutoDto actualizarInstituto(Long idInstituto, InstitutoNuevoDto modified) {
        Instituto current = institutoRepo.findById(idInstituto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instituto no encontrado"));

        current.setNombre(modified.getNombre());
        current.setDireccion1(modified.getDireccion1());
        current.setDireccion2(modified.getDireccion2());
        current.setLocalidad(modified.getLocalidad());
        current.setCodigoPostal(modified.getCodigoPostal());
        current.setPais(modified.getPais());

        Instituto saved = institutoRepo.save(current);
        return entityToDto(saved);
    }

    public void eliminarInstituto(Long idInstituto) {
        Instituto current = institutoRepo.findById(idInstituto)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Instituto no encontrado"));

        if (estudianteRepo.existsByIdInstituto(idInstituto)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El instituto no se puede eliminar porque tiene estudiantes asignados");
        }
        
        institutoRepo.delete(current);
    }

    private InstitutoDto entityToDto(Instituto entity) {
        InstitutoDto dto = new InstitutoDto();
        dto.setId(entity.getId());
        dto.setNombre(entity.getNombre());
        dto.setDireccion1(entity.getDireccion1());
        dto.setDireccion2(entity.getDireccion2());
        dto.setLocalidad(entity.getLocalidad());
        dto.setCodigoPostal(entity.getCodigoPostal());
        dto.setPais(entity.getPais());
        return dto;
    }

    private Instituto dtoToEntity(InstitutoNuevoDto dto) {
        return Instituto.builder()
                .nombre(dto.getNombre())
                .direccion1(dto.getDireccion1())
                .direccion2(dto.getDireccion2())
                .localidad(dto.getLocalidad())
                .codigoPostal(dto.getCodigoPostal())
                .pais(dto.getPais())
                .build();
    }
}
