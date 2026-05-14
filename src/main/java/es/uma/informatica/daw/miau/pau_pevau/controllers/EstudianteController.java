package es.uma.informatica.daw.miau.pau_pevau.controllers;

import es.uma.informatica.daw.miau.pau_pevau.models.EstudianteDto;
import es.uma.informatica.daw.miau.pau_pevau.models.EstudianteNuevoDto;
import es.uma.informatica.daw.miau.pau_pevau.models.ImportacionEstudiantes;
import es.uma.informatica.daw.miau.pau_pevau.services.EstudianteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/estudiantes")
@Tag(name = "Estudiantes", description = "Gestión de estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;

    public EstudianteController(EstudianteService estudianteService) {
        this.estudianteService = estudianteService;
    }

    @GetMapping
    @Operation(operationId = "consultarEstudiantes")
    public ResponseEntity<List<EstudianteDto>> getEstudiantes(
            @RequestParam(required = false) Long idSede,
            @RequestParam(required = false) Long idConvocatoria) {
        return ResponseEntity.ok(estudianteService.consultarEstudiantes(idSede, idConvocatoria));
    }

    @GetMapping("/{idEstudiante}")
    @Operation(operationId = "consultarEstudiante")
    public ResponseEntity<EstudianteDto> getEstudiante(@PathVariable Long idEstudiante) {
        try {
            return ResponseEntity.ok(estudianteService.consultarEstudiante(idEstudiante));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    @Operation(operationId = "crearEstudiante")
    public ResponseEntity<EstudianteDto> createEstudiante(@RequestBody EstudianteNuevoDto dto) {
        try {
            EstudianteDto creado = estudianteService.crearEstudiante(dto);
            URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                    .path("/{id}")
                    .buildAndExpand(creado.getId())
                    .toUri();
            return ResponseEntity.created(location).body(creado);
        } catch (RuntimeException e) {
            if ("NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PutMapping("/{idEstudiante}")
    @Operation(operationId = "actualizarEstudiante")
    public ResponseEntity<EstudianteDto> updateEstudiante(@PathVariable Long idEstudiante, @RequestBody EstudianteNuevoDto dto) {
        try {
            return ResponseEntity.ok(estudianteService.actualizarEstudiante(idEstudiante, dto));
        } catch (RuntimeException e) {
            if ("NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @DeleteMapping("/{idEstudiante}")
    @Operation(operationId = "eliminarEstudiante")
    public ResponseEntity<Void> deleteEstudiante(@PathVariable Long idEstudiante) {
        try {
            estudianteService.eliminarEstudiante(idEstudiante);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if ("NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }

    @PostMapping(path = "/upload", consumes = "multipart/form-data")
    @Operation(operationId = "importarEstudiantes")
    public ResponseEntity<ImportacionEstudiantes> uploadEstudiantes(@RequestParam("ficheroEstudiantes") MultipartFile file) {
        ImportacionEstudiantes resultado = estudianteService.importarEstudiantes(file);
        return ResponseEntity.ok(resultado);
    }
}