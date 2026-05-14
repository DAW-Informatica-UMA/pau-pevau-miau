package es.uma.informatica.daw.miau.pau_pevau.services;

import es.uma.informatica.daw.miau.pau_pevau.clients.CatalogoClient;
import es.uma.informatica.daw.miau.pau_pevau.entities.Estudiante;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.CatalogoException;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.DniDuplicadoException;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.EstudianteBloqueadoException;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.EstudianteNoEncontradoException;
import es.uma.informatica.daw.miau.pau_pevau.mappers.EstudianteMapper;
import es.uma.informatica.daw.miau.pau_pevau.models.*;
import es.uma.informatica.daw.miau.pau_pevau.repositories.EstudianteRepository;
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

    @Value("${pau_pevau.convocatoria.vigente:1}")
    private Long CONVOCATORIA_VIGENTE;

    public EstudianteDto consultarEstudiante(Long idEstudiante) {
        Estudiante actual = buscarEstudianteBD(idEstudiante);
        return rellenarDatosExternos(actual);
    }
    
    @Transactional(readOnly = true)
    private Estudiante buscarEstudianteBD(Long idEstudiante) {
        return estudianteRepo.findById(idEstudiante)
                .orElseThrow(() -> new EstudianteNoEncontradoException("Estudiante no encontrado"));
    }

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
    
    @Transactional(readOnly = true)
    private List<Estudiante> buscarEstudiantesBD(Long idSede, Long idConvocatoria) {
        if (idSede != null) {
            return estudianteRepo.findByIdSedeAndIdConvocatoria(idSede, idConvocatoria);
        } else {
            return estudianteRepo.findByIdConvocatoria(idConvocatoria);
        }
    }

    public EstudianteDto crearEstudiante(EstudianteNuevoDto estudianteNuevo) {
        validarRelaciones(estudianteNuevo);
        
        Estudiante guardado = guardarEstudianteNuevoBD(estudianteNuevo);
        
        return rellenarDatosExternos(guardado);
    }
    
    @Transactional
    private Estudiante guardarEstudianteNuevoBD(EstudianteNuevoDto estudianteNuevo) {
        if (estudianteRepo.existsByDniAndIdConvocatoria(estudianteNuevo.getDni(), CONVOCATORIA_VIGENTE)) {
            throw new DniDuplicadoException("El DNI ya existe en esta convocatoria");
        }

        Estudiante entidad = mapper.aEntidad(estudianteNuevo, CONVOCATORIA_VIGENTE);
        return estudianteRepo.save(entidad);
    }

    public EstudianteDto actualizarEstudiante(Long idEstudiante, EstudianteNuevoDto modificado) {
        validarRelaciones(modificado);
        
        Estudiante guardado = actualizarEstudianteBD(idEstudiante, modificado);
        
        return rellenarDatosExternos(guardado);
    }
    
    @Transactional
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

        Estudiante entidadModificada = mapper.aEntidad(modificado, CONVOCATORIA_VIGENTE);
        entidadModificada.setId(actual.getId());
        entidadModificada.setCodigoPegatina(actual.getCodigoPegatina());
        
        return estudianteRepo.save(entidadModificada);
    }

    @Transactional
    public void eliminarEstudiante(Long idEstudiante) {
        Estudiante actual = buscarEstudianteBD(idEstudiante);

        if (actual.isNoEliminar()) {
            throw new EstudianteBloqueadoException("Este estudiante está bloqueado y no se puede borrar");
        }
        
        estudianteRepo.delete(actual);
    }

    public ImportacionEstudiantes importarEstudiantes(List<String> lineasCsv) {
        ImportacionEstudiantes wrapper = new ImportacionEstudiantes();
        wrapper.setImportados(new ArrayList<>());
        wrapper.setNoImportados(new ArrayList<>());

        lineasCsv.parallelStream().forEach(linea -> {
            String[] columnas = linea.split(";");
            
            if (columnas.length < 5 || columnas[0].trim().isEmpty() || columnas[1].trim().isEmpty() || 
                columnas[4].trim().isEmpty()) {
                ProblemaImportacion problemaRegistro = new ProblemaImportacion();
                EstudianteNuevoDto estudianteFaltante = new EstudianteNuevoDto();
                estudianteFaltante.setDni(columnas.length > 4 ? columnas[4].trim() : "DESCONOCIDO");
                problemaRegistro.setEstudiante(estudianteFaltante);
                problemaRegistro.setProblemaImportacion("Faltan campos obligatorios");
                
                synchronized (wrapper.getNoImportados()) {
                    wrapper.getNoImportados().add(problemaRegistro);
                }
                return;
            }
            
            EstudianteNuevoDto dtoNuevo = new EstudianteNuevoDto();
            dtoNuevo.setDni(columnas[4].trim());
            
            NombreCompletoDto nb = new NombreCompletoDto();
            nb.setNombre(columnas[1].trim());
            nb.setApellido1(columnas[2].trim());
            if (columnas.length > 3) {
                nb.setApellido2(columnas[3].trim());
            }
            dtoNuevo.setNombreCompleto(nb);
            
            try {
                String nombreInstituto = columnas[0].trim();
                InstitutoDto institutoEncontrado = catalogoClient.buscarInstitutoPorNombre(nombreInstituto);
                
                if (institutoEncontrado != null) {
                    dtoNuevo.setIdInstituto(institutoEncontrado.getId()); 
                } else {
                    throw new CatalogoException("Instituto no encontrado en el catálogo: " + nombreInstituto);
                } 
                
                Set<Long> setMaterias = new HashSet<>();
                if (columnas.length >= 6 && !columnas[5].trim().isEmpty()) {
                    String[] nombresMaterias = columnas[5].split(",");
                    for (String nomMateria : nombresMaterias) {
                        MateriaDto MAT = catalogoClient.buscarMateriaPorNombre(nomMateria.trim());
                        if (MAT != null) {
                            setMaterias.add(MAT.getId());
                        } else {
                            throw new CatalogoException("Materia no encontrada en el catálogo: " + nomMateria.trim());
                        }
                    }
                }
                dtoNuevo.setMateriasMatriculadas(setMaterias);

                EstudianteDto guardadoClase = null;
                synchronized (this) {
                    guardadoClase = crearEstudiante(dtoNuevo);
                }
                
                synchronized (wrapper.getImportados()) {
                    wrapper.getImportados().add(guardadoClase);
                }
                
            } catch (Exception e) {
                ProblemaImportacion problema = new ProblemaImportacion();
                problema.setEstudiante(dtoNuevo);
                problema.setProblemaImportacion(e.getMessage());
                synchronized (wrapper.getNoImportados()) {
                    wrapper.getNoImportados().add(problema);
                }
            }
        });
        
        return wrapper;
    }

    private EstudianteDto rellenarDatosExternos(Estudiante estudiante) {
        InstitutoDto insti = catalogoClient.getInstituto(estudiante.getIdInstituto());
        
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
            if (catalogoClient.getInstituto(dto.getIdInstituto()) == null) {
                throw new EstudianteNoEncontradoException("Instituto no encontrado");
            }
        }
        if (dto.getMateriasMatriculadas() != null) {
            for (Long idMat : dto.getMateriasMatriculadas()) {
                if (catalogoClient.getMateria(idMat) == null) {
                    throw new EstudianteNoEncontradoException("Materia no encontrada");
                }
            }
        }
    }
}
