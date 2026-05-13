package es.uma.informatica.daw.miau.pau_pevau.controllers;

import es.uma.informatica.daw.miau.pau_pevau.models.EstudianteDto;
import es.uma.informatica.daw.miau.pau_pevau.models.EstudianteNuevoDto;
import es.uma.informatica.daw.miau.pau_pevau.models.ImportacionEstudiantes;
import es.uma.informatica.daw.miau.pau_pevau.services.EstudianteService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/estudiantes")
@RequiredArgsConstructor
public class EstudianteController {

    private final EstudianteService estudianteService;

    @GetMapping("/{idEstudiante}")
    public ResponseEntity<EstudianteDto> consultarEstudiante(@PathVariable Long idEstudiante) {
        return ResponseEntity.ok(estudianteService.consultarEstudiante(idEstudiante));
    }

    @PutMapping("/{idEstudiante}")
    public ResponseEntity<EstudianteDto> actualizarEstudiante(
            @PathVariable Long idEstudiante,
            @RequestBody EstudianteNuevoDto estudianteNuevo) {
        return ResponseEntity.ok(estudianteService.actualizarEstudiante(idEstudiante, estudianteNuevo));
    }

    @DeleteMapping("/{idEstudiante}")
    public ResponseEntity<Void> eliminarEstudiante(@PathVariable Long idEstudiante) {
        estudianteService.eliminarEstudiante(idEstudiante);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<EstudianteDto>> consultarEstudiantes(
            @RequestParam(required = false) Long idSede,
            @RequestParam(required = false) Long idConvocatoria) {
        return ResponseEntity.ok(estudianteService.consultarEstudiantes(idSede, idConvocatoria));
    }

    @PostMapping
    public ResponseEntity<EstudianteDto> crearEstudiante(@RequestBody EstudianteNuevoDto estudianteNuevo) {
        EstudianteDto creado = estudianteService.crearEstudiante(estudianteNuevo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PostMapping(value = "/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportacionEstudiantes> importarEstudiantes(
            @RequestParam("ficheroEstudiantes") MultipartFile ficheroEstudiantes) {
        return ResponseEntity.ok(estudianteService.importarEstudiantes(ficheroEstudiantes));
    }
}
