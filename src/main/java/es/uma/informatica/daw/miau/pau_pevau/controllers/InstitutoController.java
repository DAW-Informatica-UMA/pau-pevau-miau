package es.uma.informatica.daw.miau.pau_pevau.controllers;

import es.uma.informatica.daw.miau.pau_pevau.models.InstitutoDto;
import es.uma.informatica.daw.miau.pau_pevau.models.InstitutoNuevoDto;
import es.uma.informatica.daw.miau.pau_pevau.services.InstitutoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping(path = "/institutos")
@Tag(name = "Institutos", description = "Gestión de institutos")
@RequiredArgsConstructor
public class InstitutoController {

    private final InstitutoService institutoService;

    @GetMapping
    @Operation(operationId = "consultarInstitutos")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VICERRECTORADO')")
    public ResponseEntity<List<InstitutoDto>> getInstitutos() {
        return ResponseEntity.ok(institutoService.consultarInstitutos());
    }

    @GetMapping("/{idInstituto}")
    @Operation(operationId = "consultarInstituto")
    @PreAuthorize("hasAnyRole('ADMINISTRADOR', 'VICERRECTORADO')")
    public ResponseEntity<InstitutoDto> getInstituto(@PathVariable Long idInstituto) {
        return ResponseEntity.ok(institutoService.consultarInstituto(idInstituto));
    }

    @PostMapping
    @Operation(operationId = "crearInstituto")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<InstitutoDto> createInstituto(@RequestBody InstitutoNuevoDto dto) {
        InstitutoDto creado = institutoService.crearInstituto(dto);
        URI location = ServletUriComponentsBuilder.fromCurrentRequest()
                .path("/{id}")
                .buildAndExpand(creado.getId())
                .toUri();
        return ResponseEntity.created(location).body(creado);
    }

    @PutMapping("/{idInstituto}")
    @Operation(operationId = "actualizarInstituto")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<InstitutoDto> updateInstituto(@PathVariable Long idInstituto, @RequestBody InstitutoNuevoDto dto) {
        return ResponseEntity.ok(institutoService.actualizarInstituto(idInstituto, dto));
    }

    @DeleteMapping("/{idInstituto}")
    @Operation(operationId = "eliminarInstituto")
    @PreAuthorize("hasRole('ADMINISTRADOR')")
    public ResponseEntity<Void> deleteInstituto(@PathVariable Long idInstituto) {
        institutoService.eliminarInstituto(idInstituto);
        return ResponseEntity.ok().build();
    }
}
