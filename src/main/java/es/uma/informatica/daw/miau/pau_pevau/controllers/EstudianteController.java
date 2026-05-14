package es.uma.informatica.daw.miau.pau_pevau.controllers;

import es.uma.informatica.daw.miau.pau_pevau.models.EstudianteDto;
import es.uma.informatica.daw.miau.pau_pevau.models.EstudianteNuevoDto;
import es.uma.informatica.daw.miau.pau_pevau.models.ImportacionEstudiantes;
import es.uma.informatica.daw.miau.pau_pevau.services.EstudianteService;
import es.uma.informatica.daw.miau.pau_pevau.exceptions.CsvLecturaException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/estudiantes")
@RequiredArgsConstructor
@Tag(name = "Estudiantes", description = "Endpoints para la gestión de estudiantes")
public class EstudianteController {

    private final EstudianteService estudianteService;

    @Operation(summary = "Consultar un estudiante", description = "Obtiene los detalles de un estudiante por su ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estudiante encontrado"),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado")
    })
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VICERRECTORADO')")
    @GetMapping("/{idEstudiante}")
    public ResponseEntity<EstudianteDto> consultarEstudiante(@PathVariable Long idEstudiante) {
        return ResponseEntity.ok(estudianteService.consultarEstudiante(idEstudiante));
    }

    @Operation(summary = "Actualizar un estudiante", description = "Actualiza los datos de un estudiante existente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estudiante actualizado correctamente"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado"),
            @ApiResponse(responseCode = "409", description = "DNI duplicado o conflicto de estado")
    })
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PutMapping("/{idEstudiante}")
    public ResponseEntity<EstudianteDto> actualizarEstudiante(
            @PathVariable Long idEstudiante,
            @Valid @RequestBody EstudianteNuevoDto estudianteNuevo) {
        return ResponseEntity.ok(estudianteService.actualizarEstudiante(idEstudiante, estudianteNuevo));
    }

    @Operation(summary = "Eliminar estudiante", description = "Elimina un estudiante si no está bloqueado")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Estudiante eliminado"),
            @ApiResponse(responseCode = "404", description = "Estudiante no encontrado"),
            @ApiResponse(responseCode = "409", description = "El estudiante está bloqueado")
    })
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @DeleteMapping("/{idEstudiante}")
    public ResponseEntity<Void> eliminarEstudiante(@PathVariable Long idEstudiante) {
        estudianteService.eliminarEstudiante(idEstudiante);
        return ResponseEntity.noContent().build();
    }

    @Operation(summary = "Consultar estudiantes", description = "Devuelve una lista filtrada de estudiantes")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VICERRECTORADO')")
    @GetMapping
    public ResponseEntity<List<EstudianteDto>> consultarEstudiantes(
            @RequestParam(required = false) Long idSede,
            @RequestParam(required = false) Long idConvocatoria) {
        return ResponseEntity.ok(estudianteService.consultarEstudiantes(idSede, idConvocatoria));
    }

    @Operation(summary = "Crear estudiante", description = "Crea un nuevo estudiante")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Estudiante creado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos"),
            @ApiResponse(responseCode = "409", description = "El DNI ya existe")
    })
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping
    public ResponseEntity<EstudianteDto> crearEstudiante(@Valid @RequestBody EstudianteNuevoDto estudianteNuevo) {
        EstudianteDto creado = estudianteService.crearEstudiante(estudianteNuevo);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @Operation(summary = "Importar estudiantes CSV", description = "Sube un archivo CSV para importar múltiples estudiantes")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    @PostMapping(value = "/upload", consumes = org.springframework.http.MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ImportacionEstudiantes> importarEstudiantes(
            @RequestParam("ficheroEstudiantes") MultipartFile ficheroEstudiantes) {
        
        if (ficheroEstudiantes.isEmpty()) {
            throw new CsvLecturaException("El archivo CSV proporcionado está vacío");
        }
        
        List<String> lineas = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new InputStreamReader(ficheroEstudiantes.getInputStream()))) {
            String linea = br.readLine(); // Saltar header
            if (linea == null) {
                throw new CsvLecturaException("El archivo CSV no tiene el formato esperado");
            }
            while ((linea = br.readLine()) != null) {
                lineas.add(linea);
            }
        } catch (Exception e) {
            throw new CsvLecturaException("Ha fallado la lectura del CSV: " + e.getMessage());
        }
        
        return ResponseEntity.ok(estudianteService.importarEstudiantes(lineas));
    }
}
