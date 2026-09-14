package co.edu.eci.blueprints.controllers;

import co.edu.eci.blueprints.dto.ApiResponse;
import co.edu.eci.blueprints.model.Blueprint;
import co.edu.eci.blueprints.model.Point;
import co.edu.eci.blueprints.persistence.BlueprintNotFoundException;
import co.edu.eci.blueprints.persistence.BlueprintPersistenceException;
import co.edu.eci.blueprints.services.BlueprintsServices;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/blueprints")
public class BlueprintsAPIController {
    private final BlueprintsServices services;

    public BlueprintsAPIController(BlueprintsServices services) {
        this.services = services;
    }

    @Operation(summary = "Obtener todos los blueprints", security = @io.swagger.v3.oas.annotations.security.SecurityRequirement(name = "bearer-jwt"))
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Consulta exitosa"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Token válido pero sin el scope blueprints.read")
    })
    @GetMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    public ResponseEntity<ApiResponse<Set<Blueprint>>> getAll() {
        return ResponseEntity.ok(ApiResponse.ok(services.getAllBlueprints()));
    }

    @Operation(summary = "Obtener blueprints por autor")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Consulta exitosa"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Token válido pero sin el scope blueprints.read"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Autor sin blueprints registrados")
    })
    @GetMapping("/{author}")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    public ResponseEntity<ApiResponse<Set<Blueprint>>> byAuthor(@PathVariable String author)
            throws BlueprintNotFoundException {
        return ResponseEntity.ok(ApiResponse.ok(services.getBlueprintsByAuthor(author)));
    }

    @Operation(summary = "Obtener un blueprint por autor y nombre")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "Consulta exitosa"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Token válido pero sin el scope blueprints.read"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Blueprint no encontrado")
    })
    @GetMapping("/{author}/{name}")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.read')")
    public ResponseEntity<ApiResponse<Blueprint>> byAuthorAndName(
            @PathVariable String author, @PathVariable String name) throws BlueprintNotFoundException {
        return ResponseEntity.ok(ApiResponse.ok(services.getBlueprint(author, name)));
    }

    @Operation(summary = "Crear un nuevo blueprint")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "201", description = "Blueprint creado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "Datos inválidos o blueprint duplicado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Token válido pero sin el scope blueprints.write")
    })
    @PostMapping
    @PreAuthorize("hasAuthority('SCOPE_blueprints.write')")
    public ResponseEntity<ApiResponse<Blueprint>> add(@Valid @RequestBody NewBlueprintRequest request)
            throws BlueprintPersistenceException {
        Blueprint blueprint = new Blueprint(request.author(), request.name(), request.points());
        services.addNewBlueprint(blueprint);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.created(blueprint));
    }

    @Operation(summary = "Agregar un punto a un blueprint existente")
    @ApiResponses({
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "202", description = "Actualización aceptada"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "Token ausente, inválido o expirado"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "Token válido pero sin el scope blueprints.write"),
            @io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "404", description = "Blueprint no encontrado")
    })
    @PutMapping("/{author}/{name}/points")
    @PreAuthorize("hasAuthority('SCOPE_blueprints.write')")
    public ResponseEntity<ApiResponse<Void>> addPoint(
            @PathVariable String author, @PathVariable String name, @RequestBody Point point)
            throws BlueprintNotFoundException {
        services.addPoint(author, name, point.x(), point.y());
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(ApiResponse.accepted(null));
    }

    public record NewBlueprintRequest(
            @NotBlank String author,
            @NotBlank String name,
            @Valid List<Point> points) {
    }
}