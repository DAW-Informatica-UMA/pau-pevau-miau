package es.uma.informatica.daw.miau.pau_pevau.services;

import es.uma.informatica.daw.miau.pau_pevau.models.EstudianteDto;
import es.uma.informatica.daw.miau.pau_pevau.models.EstudianteNuevoDto;
import es.uma.informatica.daw.miau.pau_pevau.models.ImportacionEstudiantes;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EstudianteService {
    EstudianteDto consultarEstudiante(Long idEstudiante);
    EstudianteDto actualizarEstudiante(Long idEstudiante, EstudianteNuevoDto estudianteNuevo);
    void eliminarEstudiante(Long idEstudiante);
    List<EstudianteDto> consultarEstudiantes(Long idSede, Long idConvocatoria);
    EstudianteDto crearEstudiante(EstudianteNuevoDto estudianteNuevo);
    ImportacionEstudiantes importarEstudiantes(MultipartFile ficheroEstudiantes);
}
