package es.uma.informatica.daw.miau.pau_pevau.services;

import es.uma.informatica.daw.miau.pau_pevau.clients.CatalogoClient;
import es.uma.informatica.daw.miau.pau_pevau.entities.Estudiante;
import es.uma.informatica.daw.miau.pau_pevau.mappers.EstudianteMapper;
import es.uma.informatica.daw.miau.pau_pevau.models.*;
import es.uma.informatica.daw.miau.pau_pevau.repositories.EstudianteRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;

@Service
@RequiredArgsConstructor
@Transactional
public class EstudianteService {

    private final EstudianteRepository estudianteRepo;
    private final EstudianteMapper mapper;
    private final CatalogoClient catalogoClient;

    // Para no tener que recompilar todo cuando cambie la convocatoria vigente
    @Value("${pau_pevau.convocatoria.vigente:1}")
    private Long CONVOCATORIA_VIGENTE;

    @Transactional(readOnly = true)
    public EstudianteDto consultarEstudiante(Long idEstudiante) {
        Estudiante actual = estudianteRepo.findById(idEstudiante)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado"));
        return rellenarDatosExternos(actual);
    }

    @Transactional(readOnly = true)
    public List<EstudianteDto> consultarEstudiantes(Long idSede, Long idConvocatoria) {
        if (idConvocatoria == null) {
            idConvocatoria = CONVOCATORIA_VIGENTE;
        }

        List<Estudiante> lista;
        if (idSede != null) {
            lista = estudianteRepo.findByIdSedeAndIdConvocatoria(idSede, idConvocatoria);
        } else {
            lista = estudianteRepo.findByIdConvocatoria(idConvocatoria);
        }

        List<EstudianteDto> resultado = new ArrayList<>();
        for (Estudiante e : lista) {
            resultado.add(rellenarDatosExternos(e));
        }
        return resultado;
    }

    public EstudianteDto crearEstudiante(EstudianteNuevoDto estudianteNuevo) {
        validarRelaciones(estudianteNuevo);
        if (estudianteRepo.existsByDniAndIdConvocatoria(estudianteNuevo.getDni(), CONVOCATORIA_VIGENTE)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "El DNI ya existe en esta convocatoria");
        }

        Estudiante entidad = mapper.aEntidad(estudianteNuevo, CONVOCATORIA_VIGENTE);
        Estudiante guardado = estudianteRepo.save(entidad);
        
        return rellenarDatosExternos(guardado);
    }

    public EstudianteDto actualizarEstudiante(Long idEstudiante, EstudianteNuevoDto modificado) {
        Estudiante actual = estudianteRepo.findById(idEstudiante)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado"));
                
        validarRelaciones(modificado);

        if (!actual.getDni().equals(modificado.getDni())) {
            if (estudianteRepo.existsByDniAndIdConvocatoria(modificado.getDni(), CONVOCATORIA_VIGENTE)) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "Este DNI ya lo tiene otro alumno");
            }
        }

        // Esto es para evitar que un alumno bloqueado se desbloquee por accidente al hacer una modificación.
        if (actual.isNoEliminar()) {
            if (modificado.getNoEliminar() != null && !modificado.getNoEliminar()) {
                throw new ResponseStatusException(HttpStatus.CONFLICT, "El estudiante está marcado como no eliminar, no se puede revocar ese estado.");
            }
            modificado.setNoEliminar(true);
        }

        Estudiante entidadModificada = mapper.aEntidad(modificado, CONVOCATORIA_VIGENTE);
        entidadModificada.setId(actual.getId());
        entidadModificada.setCodigoPegatina(actual.getCodigoPegatina());
        
        Estudiante guardado = estudianteRepo.save(entidadModificada);
        return rellenarDatosExternos(guardado);
    }

    public void eliminarEstudiante(Long idEstudiante) {
        Estudiante actual = estudianteRepo.findById(idEstudiante)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Estudiante no encontrado"));

        if (actual.isNoEliminar()) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Este estudiante está bloqueado y no se puede borrar");
        }
        
        estudianteRepo.delete(actual);
    }

    public ImportacionEstudiantes importarEstudiantes(MultipartFile fichero) {
        ImportacionEstudiantes wrapper = new ImportacionEstudiantes();
        wrapper.setImportados(new ArrayList<>());
        wrapper.setNoImportados(new ArrayList<>());

        try {
            // Lo hemos hecho usando un bufferedreader como en otras asignaturas, saltamos la primera linea que es el header
            BufferedReader br = new BufferedReader(new InputStreamReader(fichero.getInputStream()));
            String linea = br.readLine();
            
            while ((linea = br.readLine()) != null) {
                String[] columnas = linea.split(";");
                
                if (columnas.length < 5 || columnas[0].trim().isEmpty() || columnas[1].trim().isEmpty() || 
                    columnas[4].trim().isEmpty()) {
                    ProblemaImportacion problemaRegistro = new ProblemaImportacion();
                    EstudianteNuevoDto estudianteFaltante = new EstudianteNuevoDto();
                    estudianteFaltante.setDni(columnas.length > 4 ? columnas[4].trim() : "DESCONOCIDO");
                    problemaRegistro.setEstudiante(estudianteFaltante);
                    problemaRegistro.setProblemaImportacion("Faltan campos obligatorios");
                    wrapper.getNoImportados().add(problemaRegistro);
                    continue;
                }
                
                EstudianteNuevoDto dtoNuevo = new EstudianteNuevoDto();
                // CENTRO;Nombre;Apellido1;Apellido2;DNI/NIF;DETALLE_MATERIAS
                dtoNuevo.setDni(columnas[4].trim());
                
                NombreCompletoDto nb = new NombreCompletoDto();
                nb.setNombre(columnas[1].trim());
                nb.setApellido1(columnas[2].trim());
                if (columnas.length > 3) {
                    nb.setApellido2(columnas[3].trim());
                }
                dtoNuevo.setNombreCompleto(nb);
                
                try {
                    // Buscamos el instituto usando el endpint correcto
                    String nombreInstituto = columnas[0].trim();
                    InstitutoDto institutoEncontrado = catalogoClient.buscarInstitutoPorNombre(nombreInstituto);
                    
                    if (institutoEncontrado != null) {
                        dtoNuevo.setIdInstituto(institutoEncontrado.getId()); 
                    } else {
                        // Si el microservicio no lo encuentra, da fallo para que se registre en la lista de NO importados.
                        throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Instituto no encontrado en el catálogo: " + nombreInstituto);
                    } 
                    
                    Set<Long> setMaterias = new HashSet<>();
                    if (columnas.length >= 6 && !columnas[5].trim().isEmpty()) {
                        String[] nombresMaterias = columnas[5].split(",");
                        for (String nomMateria : nombresMaterias) {
                            MateriaDto MAT = catalogoClient.buscarMateriaPorNombre(nomMateria.trim());
                            if (MAT != null) {
                                setMaterias.add(MAT.getId());
                            } else {
                                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Materia no encontrada en el catálogo: " + nomMateria.trim());
                            }
                        }
                    }
                    dtoNuevo.setMateriasMatriculadas(setMaterias);

                    EstudianteDto guardadoClase = crearEstudiante(dtoNuevo);
                    wrapper.getImportados().add(guardadoClase);
                } catch (ResponseStatusException e) {
                    ProblemaImportacion problema = new ProblemaImportacion();
                    problema.setEstudiante(dtoNuevo);
                    problema.setProblemaImportacion(e.getReason());
                    wrapper.getNoImportados().add(problema);
                }
            }
            br.close();
            
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Ha fallado la lectura del CSV");
        }
        
        return wrapper;
    }

    // Funcion para cargar datos externos (instituto y materias) y rellenar el DTO completo a partir de la entidad.
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
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Instituto no encontrado");
            }
        }
        if (dto.getMateriasMatriculadas() != null) {
            for (Long idMat : dto.getMateriasMatriculadas()) {
                if (catalogoClient.getMateria(idMat) == null) {
                    throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Materia no encontrada");
                }
            }
        }
    }
}
