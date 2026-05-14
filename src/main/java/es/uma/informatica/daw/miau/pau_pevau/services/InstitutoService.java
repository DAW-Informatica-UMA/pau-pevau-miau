package es.uma.informatica.daw.miau.pau_pevau.services;

import es.uma.informatica.daw.miau.pau_pevau.entities.Instituto;
import es.uma.informatica.daw.miau.pau_pevau.models.InstitutoDto;
import es.uma.informatica.daw.miau.pau_pevau.models.InstitutoNuevoDto;
import es.uma.informatica.daw.miau.pau_pevau.repositories.InstitutoRepository;
import es.uma.informatica.daw.miau.pau_pevau.repositories.EstudianteRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class InstitutoService {
    private final InstitutoRepository repository;
    private final EstudianteRepository estudianteRepository;

    @Autowired
    public InstitutoService(InstitutoRepository repository, EstudianteRepository estudianteRepository) {
        this.repository = repository;
        this.estudianteRepository = estudianteRepository;
    }

    public List<InstitutoDto> obtenerTodos() {
        return repository.findAll().stream()
                .map(this::entityToDto)
                .collect(Collectors.toList());
    }

    public InstitutoDto obtenerPorId(Long id) {
        return repository.findById(id)
                .map(this::entityToDto)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));
    }

    @Transactional
    public InstitutoDto crear(InstitutoNuevoDto dto) {
        Instituto instituto = dtoToEntity(dto);
        return entityToDto(repository.save(instituto));
    }

    @Transactional
    public InstitutoDto actualizar(Long id, InstitutoNuevoDto dto) {
        Instituto instituto = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        instituto.setNombre(dto.getNombre());
        instituto.setDireccion1(dto.getDireccion1());
        instituto.setDireccion2(dto.getDireccion2());
        instituto.setLocalidad(dto.getLocalidad());
        instituto.setCodigoPostal(dto.getCodigoPostal());
        instituto.setPais(dto.getPais());

        return entityToDto(repository.save(instituto));
    }

    @Transactional
    public void eliminar(Long id) {
        if (!repository.existsById(id)) {
            throw new RuntimeException("NOT_FOUND");
        }
        if (estudianteRepository.existsByIdInstituto(id)) {
            throw new RuntimeException("CONFLICT");
        }
        repository.deleteById(id);
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