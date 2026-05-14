package es.uma.informatica.daw.miau.pau_pevau.controllers;

import es.uma.informatica.daw.miau.pau_pevau.models.InstitutoDto;
import es.uma.informatica.daw.miau.pau_pevau.models.InstitutoNuevoDto;
import es.uma.informatica.daw.miau.pau_pevau.services.InstitutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/institutos")
@Tag(name = "Institutos", description = "Gestión de institutos")
public class InstitutoController {

    private final InstitutoService institutoService;

    public InstitutoController(InstitutoService institutoService) {
        this.institutoService = institutoService;
    }

    @GetMapping
    @Operation(operationId = "consultarInstitutos")
    public ResponseEntity<List<InstitutoDto>> getInstitutos() {
        return ResponseEntity.ok(institutoService.obtenerTodos());
    }

    @GetMapping("/{idInstituto}")
    @Operation(operationId = "consultarInstituto")
    public ResponseEntity<InstitutoDto> getInstituto(@PathVariable Long idInstituto) {
        try {
            return ResponseEntity.ok(institutoService.obtenerPorId(idInstituto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @PostMapping
    @Operation(operationId = "crearInstituto")
    public ResponseEntity<InstitutoDto> createInstituto(@RequestBody InstitutoNuevoDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(institutoService.crear(dto));
    }

    @PutMapping("/{idInstituto}")
    @Operation(operationId = "actualizarInstituto")
    public ResponseEntity<InstitutoDto> updateInstituto(@PathVariable Long idInstituto, @RequestBody InstitutoNuevoDto dto) {
        try {
            return ResponseEntity.ok(institutoService.actualizar(idInstituto, dto));
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @DeleteMapping("/{idInstituto}")
    @Operation(operationId = "eliminarInstituto")
    public ResponseEntity<Void> deleteInstituto(@PathVariable Long idInstituto) {
        try {
            institutoService.eliminar(idInstituto);
            return ResponseEntity.ok().build();
        } catch (RuntimeException e) {
            if ("NOT_FOUND".equals(e.getMessage())) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
            }
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        }
    }
}