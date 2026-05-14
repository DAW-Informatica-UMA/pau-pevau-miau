package es.uma.informatica.daw.miau.pau_pevau.services;

import es.uma.informatica.daw.miau.pau_pevau.clients.CalendarioClient;
import es.uma.informatica.daw.miau.pau_pevau.clients.SedeClient;
import es.uma.informatica.daw.miau.pau_pevau.entities.Estudiante;
import es.uma.informatica.daw.miau.pau_pevau.entities.Instituto;
import es.uma.informatica.daw.miau.pau_pevau.entities.Materia;
import es.uma.informatica.daw.miau.pau_pevau.mappers.EstudianteMapper;
import es.uma.informatica.daw.miau.pau_pevau.models.*;
import es.uma.informatica.daw.miau.pau_pevau.repositories.EstudianteRepository;
import es.uma.informatica.daw.miau.pau_pevau.repositories.InstitutoRepository;
import es.uma.informatica.daw.miau.pau_pevau.repositories.MateriaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EstudianteService {

    private final EstudianteRepository estudianteRepository;
    private final InstitutoRepository institutoRepository;
    private final MateriaRepository materiaRepository;
    private final SedeClient sedeClient;
    private final CalendarioClient calendarioClient;
    private final EstudianteMapper mapper;

    public EstudianteDto consultarEstudiante(Long id) {
        Estudiante est = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));
        return buildDto(est);
    }

    public List<EstudianteDto> consultarEstudiantes(Long idSede, Long idConvocatoria) {
        Long conv = (idConvocatoria != null) ? idConvocatoria : calendarioClient.obtenerConvocatoriaActualId();
        List<Estudiante> estudiantes;

        if (idSede != null) {
            estudiantes = estudianteRepository.findByIdSedeAndIdConvocatoria(idSede, conv);
        } else {
            estudiantes = estudianteRepository.findByIdConvocatoria(conv);
        }
        return estudiantes.stream().map(this::buildDto).collect(Collectors.toList());
    }

    @Transactional
    public EstudianteDto crearEstudiante(EstudianteNuevoDto dto) {
        Long convActual = calendarioClient.obtenerConvocatoriaActualId();

        if (dto.getIdSede() != null && !sedeClient.existeSede(dto.getIdSede())) {
            throw new RuntimeException("NOT_FOUND"); // Sede no existe en el otro microservicio
        }
        if (estudianteRepository.existsByDniAndIdConvocatoria(dto.getDni(), convActual)) {
            throw new RuntimeException("CONFLICT"); // DNI duplicado
        }
        if (!institutoRepository.existsById(dto.getIdInstituto())) {
            throw new RuntimeException("NOT_FOUND"); // Instituto no existe localmente
        }

        Estudiante est = mapper.toEntity(dto);

        // Lógica de negocio asignada explícitamente en el servicio
        est.setIdConvocatoria(convActual);
        est.setCodigoPegatina("PEG-" + dto.getDni()); // Nuestro sistema original, mucho más claro
        est.setNecesidadEspecial(false);

        return buildDto(estudianteRepository.save(est));
    }

    @Transactional
    public EstudianteDto actualizarEstudiante(Long id, EstudianteNuevoDto dto) {
        Estudiante est = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        if (!institutoRepository.existsById(dto.getIdInstituto())) {
            throw new RuntimeException("NOT_FOUND");
        }
        if (estudianteRepository.existsByDniAndIdConvocatoriaAndIdNot(dto.getDni(), est.getIdConvocatoria(), id)) {
            throw new RuntimeException("CONFLICT");
        }

        est.setNombre(dto.getNombreCompleto().getNombre());
        est.setApellido1(dto.getNombreCompleto().getApellido1());
        est.setApellido2(dto.getNombreCompleto().getApellido2());
        est.setDni(dto.getDni());
        est.setTelefono(dto.getTelefono());
        est.setEmail(dto.getEmail());
        est.setIdInstituto(dto.getIdInstituto());
        est.setIdSede(dto.getIdSede());

        if (dto.getMateriasMatriculadas() != null) {
            est.setMateriasMatriculadas(new ArrayList<>(dto.getMateriasMatriculadas()));
        }

        // Regla: noEliminar solo pasa de false a true
        if (!est.isNoEliminar() && Boolean.TRUE.equals(dto.getNoEliminar())) {
            est.setNoEliminar(true);
        }

        return buildDto(estudianteRepository.save(est));
    }

    @Transactional
    public void eliminarEstudiante(Long id) {
        Estudiante est = estudianteRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("NOT_FOUND"));

        if (est.isNoEliminar()) {
            throw new RuntimeException("CONFLICT");
        }
        estudianteRepository.delete(est);
    }

    public ImportacionEstudiantes importarEstudiantes(MultipartFile file) {
        Long convActual = calendarioClient.obtenerConvocatoriaActualId();
        Set<Long> sedesValidas = sedeClient.obtenerTodosLosIdsSedes();

        Map<String, Long> cacheMaterias = materiaRepository.findAll().stream()
                .collect(Collectors.toMap(Materia::getNombre, Materia::getId, (id1, id2) -> id1));

        Map<String, Long> cacheInstitutos = institutoRepository.findAll().stream()
                .collect(Collectors.toMap(Instituto::getNombre, Instituto::getId, (id1, id2) -> id1));

        ImportacionEstudiantes resultado = new ImportacionEstudiantes();
        resultado.setImportados(new ArrayList<>());
        resultado.setNoImportados(new ArrayList<>());

        try (BufferedReader br = new BufferedReader(new InputStreamReader(file.getInputStream(), StandardCharsets.UTF_8))) {
            String line;
            boolean isFirstLine = true;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false;
                    continue;
                }
                if (line.trim().isEmpty()) continue;

                String[] campos = line.split(";", -1);
                if (campos.length < 6) continue;

                EstudianteNuevoDto dto = new EstudianteNuevoDto();
                NombreCompletoDto nc = new NombreCompletoDto();
                nc.setNombre(campos[1].trim());
                nc.setApellido1(campos[2].trim());
                nc.setApellido2(campos[3].trim());

                dto.setNombreCompleto(nc);
                dto.setDni(campos[4].trim());
                dto.setNoEliminar(false);
                dto.setIdSede(1L);

                try {
                    // --- 2. VALIDAR SEDE (Contra la caché en RAM) ---
                    if (!sedesValidas.contains(dto.getIdSede())) {
                        throw new RuntimeException("Sede no válida en el sistema central");
                    }

                    // --- 3. RESOLVER INSTITUTO (Caché + Fallback) ---
                    String centro = campos[0].trim();
                    Long idInst = cacheInstitutos.get(centro);
                    if (idInst == null) {
                        Instituto nuevoInst = institutoRepository.save(Instituto.builder().nombre(centro).build());
                        idInst = nuevoInst.getId();
                        cacheInstitutos.put(centro, idInst);
                    }
                    dto.setIdInstituto(idInst);

                    // --- 4. RESOLVER MATERIAS (Caché + Fallback) ---
                    Set<Long> materiasIds = new HashSet<>();
                    String[] materiasArray = campos[5].split(","); // ¡Corregido! Ahora lee el CSV
                    for (String matNombre : materiasArray) {
                        String cleanName = matNombre.trim();
                        if (cleanName.isEmpty()) continue;

                        Long idMat = cacheMaterias.get(cleanName);
                        if (idMat == null) {
                            Materia nueva = materiaRepository.save(Materia.builder().nombre(cleanName).eliminada(false).build());
                            idMat = nueva.getId();
                            cacheMaterias.put(cleanName, idMat);
                        }
                        materiasIds.add(idMat);
                    }
                    dto.setMateriasMatriculadas(materiasIds);

                    if (estudianteRepository.existsByDniAndIdConvocatoria(dto.getDni(), convActual)) {
                        throw new RuntimeException("CONFLICT");
                    }

                    Estudiante est = mapper.toEntity(dto);
                    est.setIdConvocatoria(convActual);
                    est.setCodigoPegatina("PEG-" + dto.getDni());
                    est.setNecesidadEspecial(false);

                    Estudiante guardado = estudianteRepository.save(est);
                    resultado.getImportados().add(buildDto(guardado));

                } catch (Exception e) {
                    ProblemaImportacion problema = new ProblemaImportacion();
                    problema.setEstudiante(dto);
                    problema.setProblemaImportacion(e.getMessage());
                    resultado.getNoImportados().add(problema);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al leer el archivo CSV: " + e.getMessage());
        }

        return resultado;
    }

    private EstudianteDto buildDto(Estudiante entity) {
        InstitutoDto instDto = institutoRepository.findById(entity.getIdInstituto())
                .map(i -> {
                    InstitutoDto d = new InstitutoDto();
                    d.setId(i.getId());
                    d.setNombre(i.getNombre());
                    d.setDireccion1(i.getDireccion1());
                    d.setDireccion2(i.getDireccion2());
                    d.setLocalidad(i.getLocalidad());
                    d.setCodigoPostal(i.getCodigoPostal());
                    d.setPais(i.getPais());
                    return d;
                }).orElse(null);

        Set<MateriaDto> materiasDtos = entity.getMateriasMatriculadas().stream()
                .map(id -> materiaRepository.findById(id).orElse(null))
                .filter(Objects::nonNull)
                .map(m -> {
                    MateriaDto d = new MateriaDto();
                    d.setId(m.getId());
                    d.setNombre(m.getNombre());
                    d.setEliminada(m.getEliminada());
                    return d;
                }).collect(Collectors.toSet());

        return mapper.toDto(entity, instDto, materiasDtos);
    }
}