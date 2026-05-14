package es.uma.informatica.daw.miau.pau_pevau.services;

import es.uma.informatica.daw.miau.pau_pevau.clients.CatalogoClient;
import es.uma.informatica.daw.miau.pau_pevau.entities.Estudiante;
import es.uma.informatica.daw.miau.pau_pevau.entities.Instituto;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.CatalogoException;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.DniDuplicadoException;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.EstudianteBloqueadoException;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.EstudianteNoEncontradoException;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.InstitutoNoEncontradoException;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.MateriaNoEncontradaException;
import es.uma.informatica.daw.miau.pau_pevau.mappers.EstudianteMapper;
import es.uma.informatica.daw.miau.pau_pevau.models.*;
import es.uma.informatica.daw.miau.pau_pevau.repositories.EstudianteRepository;
import es.uma.informatica.daw.miau.pau_pevau.repositories.InstitutoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class EstudianteService {

    private final EstudianteRepository estudianteRepo;
    private final EstudianteMapper mapper;
    private final CatalogoClient catalogoClient;
    private final InstitutoRepository institutoRepo;
    private final CsvEstudianteParser csvParser;

    @Value("${pau_pevau.convocatoria.vigente:1}")
    private Long CONVOCATORIA_VIGENTE;

    // Busca un estudiante por ID y devuelve sus datos con información del catálogo
    @Transactional(readOnly = true)
    public EstudianteDto consultarEstudiante(Long idEstudiante) {
        Estudiante actual = buscarEstudianteBD(idEstudiante);
        return rellenarDatosExternos(actual);
    }

    // Método privado que busca un estudiante en BD por ID, lanza excepción si no existe
    private Estudiante buscarEstudianteBD(Long idEstudiante) {
        return estudianteRepo.findById(idEstudiante)
                .orElseThrow(() -> new EstudianteNoEncontradoException("Estudiante no encontrado"));
    }

    // Devuelve lista de estudiantes filtrados por sede y convocatoria (vigente si no se especifica)
    @Transactional(readOnly = true)
    public List<EstudianteDto> consultarEstudiantes(Long idSede, Long idConvocatoria) {
        if (idConvocatoria == null) {
            idConvocatoria = CONVOCATORIA_VIGENTE;
        }

        List<Estudiante> lista = buscarEstudiantesBD(idSede, idConvocatoria);

        List<EstudianteDto> resultado = new ArrayList<>();
        for (Estudiante e : lista) {
            resultado.add(rellenarDatosExternos(e));
        }
        return resultado;
    }
    
    // Método privado que busca en BD estudiantes por sede y convocatoria
    private List<Estudiante> buscarEstudiantesBD(Long idSede, Long idConvocatoria) {
        if (idSede != null) {
            return estudianteRepo.findByIdSedeAndIdConvocatoria(idSede, idConvocatoria);
        } else {
            return estudianteRepo.findByIdConvocatoria(idConvocatoria);
        }
    }

    // Crea un nuevo estudiante validando relaciones y verifica que el DNI no esté duplicado
    @Transactional
    public EstudianteDto crearEstudiante(EstudianteNuevoDto estudianteNuevo) {
        validarRelaciones(estudianteNuevo);
        
        Estudiante guardado = guardarEstudianteNuevoBD(estudianteNuevo);
        
        return rellenarDatosExternos(guardado);
    }
    
    // Método privado que persiste un nuevo estudiante en BD tras validar duplicidad de DNI
    private Estudiante guardarEstudianteNuevoBD(EstudianteNuevoDto estudianteNuevo) {
        if (estudianteRepo.existsByDniAndIdConvocatoria(estudianteNuevo.getDni(), CONVOCATORIA_VIGENTE)) {
            throw new DniDuplicadoException("El DNI ya existe en esta convocatoria");
        }

        Estudiante entidad = mapper.aEntidad(estudianteNuevo, CONVOCATORIA_VIGENTE);
        return estudianteRepo.save(entidad);
    }

    // Actualiza datos de un estudiante manteniendo DNI único y respetando bloqueo de no eliminable
    @Transactional
    public EstudianteDto actualizarEstudiante(Long idEstudiante, EstudianteNuevoDto modificado) {
        validarRelaciones(modificado);
        
        Estudiante guardado = actualizarEstudianteBD(idEstudiante, modificado);
        
        return rellenarDatosExternos(guardado);
    }
    
    // Método privado que persiste cambios en un estudiante existente en BD
    private Estudiante actualizarEstudianteBD(Long idEstudiante, EstudianteNuevoDto modificado) {
        Estudiante actual = buscarEstudianteBD(idEstudiante);

        if (!actual.getDni().equals(modificado.getDni())) {
            if (estudianteRepo.existsByDniAndIdConvocatoria(modificado.getDni(), CONVOCATORIA_VIGENTE)) {
                throw new DniDuplicadoException("Este DNI ya lo tiene otro alumno");
            }
        }

        if (actual.isNoEliminar()) {
            if (modificado.getNoEliminar() != null && !modificado.getNoEliminar()) {
                throw new EstudianteBloqueadoException("El estudiante está marcado como no eliminar, no se puede revocar ese estado.");
            }
            modificado.setNoEliminar(true);
        }

        Estudiante entidadModificada = mapper.aEntidad(modificado, CONVOCATORIA_VIGENTE, actual.getCodigoPegatina());
        entidadModificada.setId(actual.getId());
        
        return estudianteRepo.save(entidadModificada);
    }

    // Elimina un estudiante de BD si no está bloqueado (marcado como no eliminable)
    @Transactional
    public void eliminarEstudiante(Long idEstudiante) {
        Estudiante actual = buscarEstudianteBD(idEstudiante);

        if (actual.isNoEliminar()) {
            throw new EstudianteBloqueadoException("Este estudiante está bloqueado y no se puede borrar");
        }
        
        estudianteRepo.delete(actual);
    }

    // Importa estudiantes desde líneas CSV en paralelo, validando campos obligatorios y existencia en catálogo
    public ImportacionEstudiantes importarEstudiantes(List<String> lineasCsv) {
        ImportacionEstudiantes wrapper = new ImportacionEstudiantes();
        wrapper.setImportados(new ArrayList<>());
        wrapper.setNoImportados(new ArrayList<>());

        List<CsvEstudianteParser.EstudianteParseado> parseados = csvParser.parsearLineas(lineasCsv);

        // Ya no usamos parallelStream para evitar bloqueos y contención de pestillos (synchronized),
        // delegando transaccionalidad al guardado individual.
        for (CsvEstudianteParser.EstudianteParseado item : parseados) {
            if (item.errorParseo != null) {
                ProblemaImportacion problema = new ProblemaImportacion();
                problema.setEstudiante(item.dto);
                problema.setProblemaImportacion(item.errorParseo);
                wrapper.getNoImportados().add(problema);
                continue;
            }

            try {
                EstudianteNuevoDto dtoNuevo = item.dto;
                
                // Mapeo local de instituto
                String nombreInstituto = item.nombreInstituto != null ? item.nombreInstituto.trim() : null;
                Instituto insti = (nombreInstituto == null || nombreInstituto.isEmpty())
                        ? null
                        : institutoRepo.findByNombreIgnoreCase(nombreInstituto).orElse(null);
                if (insti != null) {
                    dtoNuevo.setIdInstituto(insti.getId());
                } else {
                    throw new CatalogoException("Instituto no encontrado en local: " + item.nombreInstituto);
                }

                Set<Long> setMaterias = new HashSet<>();
                if (item.nombresMaterias != null) {
                    for (String nomMateria : item.nombresMaterias) {
                        MateriaDto MAT = catalogoClient.buscarMateriaPorNombre(nomMateria);
                        if (MAT != null) {
                            setMaterias.add(MAT.getId());
                        } else {
                            throw new CatalogoException("Materia no encontrada en el catálogo: " + nomMateria);
                        }
                    }
                }
                dtoNuevo.setMateriasMatriculadas(setMaterias);

                // Guardado
                EstudianteDto guardado = crearEstudiante(dtoNuevo);
                wrapper.getImportados().add(guardado);

            } catch (Exception e) {
                ProblemaImportacion problema = new ProblemaImportacion();
                problema.setEstudiante(item.dto);
                problema.setProblemaImportacion(e.getMessage());
                wrapper.getNoImportados().add(problema);
            }
        }
        
        return wrapper;
    }

    // Metodos auxiliares privados para externalizar datos y validar relaciones

    private EstudianteDto rellenarDatosExternos(Estudiante estudiante) {
        InstitutoDto insti = obtenerInstitutoLocal(estudiante.getIdInstituto());
        
        Set<MateriaDto> materiasList = new HashSet<>();
        for (Long idMateria : estudiante.getMateriasMatriculadas()) {
            MateriaDto m = catalogoClient.getMateria(idMateria);
            if (m != null) {
                materiasList.add(m);
            }
        }

        return mapper.aDto(estudiante, insti, materiasList);
    }
    
    private void validarRelaciones(EstudianteNuevoDto dto) {
        if (dto.getIdInstituto() != null) {
            if (!institutoRepo.existsById(dto.getIdInstituto())) {
                throw new InstitutoNoEncontradoException("Instituto no encontrado");
            }
        }
        if (dto.getMateriasMatriculadas() != null) {
            for (Long idMat : dto.getMateriasMatriculadas()) {
                if (catalogoClient.getMateria(idMat) == null) {
                    throw new MateriaNoEncontradaException("Materia no encontrada");
                }
            }
        }
    }

    private InstitutoDto obtenerInstitutoLocal(Long idInstituto) {
        if (idInstituto == null) {
            return null;
        }

        return institutoRepo.findById(idInstituto)
                .map(this::mapInstituto)
                .orElse(null);
    }

    private InstitutoDto mapInstituto(Instituto instituto) {
        InstitutoDto dto = new InstitutoDto();
        dto.setId(instituto.getId());
        dto.setNombre(instituto.getNombre());
        dto.setDireccion1(instituto.getDireccion1());
        dto.setDireccion2(instituto.getDireccion2());
        dto.setLocalidad(instituto.getLocalidad());
        dto.setCodigoPostal(instituto.getCodigoPostal());
        dto.setPais(instituto.getPais());
        return dto;
    }
}
