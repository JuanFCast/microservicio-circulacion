package co.analisys.biblioteca.controller;

import co.analisys.biblioteca.model.LibroId;
import co.analisys.biblioteca.model.Prestamo;
import co.analisys.biblioteca.model.PrestamoId;
import co.analisys.biblioteca.model.UsuarioId;
import co.analisys.biblioteca.service.CirculacionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/circulacion")
public class CirculacionController {

    @Autowired
    private CirculacionService circulacionService;

    @Operation(
            summary = "Prestar un libro",
            description = "Permite a un bibliotecario prestar un libro a un usuario registrado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Libro prestado exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol de bibliotecario"),
            @ApiResponse(responseCode = "400", description = "Parámetros inválidos")
    })
    @PostMapping("/prestar")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public void prestarLibro(
            @Parameter(description = "ID del usuario que solicita el préstamo") @RequestParam String usuarioId,
            @Parameter(description = "ID del libro a prestar") @RequestParam String libroId) {
        circulacionService.prestarLibro(new UsuarioId(usuarioId), new LibroId(libroId));
    }

    @Operation(
            summary = "Devolver un libro",
            description = "Permite a un bibliotecario registrar la devolución de un libro prestado."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Libro devuelto exitosamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere rol de bibliotecario"),
            @ApiResponse(responseCode = "404", description = "Préstamo no encontrado")
    })
    @PostMapping("/devolver")
    @PreAuthorize("hasRole('ROLE_LIBRARIAN')")
    public void devolverLibro(
            @Parameter(description = "ID del préstamo a devolver") @RequestParam String prestamoId) {
        circulacionService.devolverLibro(new PrestamoId(prestamoId));
    }

    @Operation(
            summary = "Consultar todos los préstamos",
            description = "Obtiene una lista de todos los préstamos registrados en el sistema."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Lista de préstamos obtenida correctamente"),
            @ApiResponse(responseCode = "403", description = "Acceso denegado - Se requiere autenticación")
    })
    @GetMapping("/prestamos")
    @PreAuthorize("hasAnyRole('ROLE_LIBRARIAN', 'ROLE_USER')")
    public List<Prestamo> obtenerTodosPrestamos() {
        return circulacionService.obtenerTodosPrestamos();
    }

    @Operation(
            summary = "Verificar estado del servicio",
            description = "Endpoint público para verificar que el servicio de circulación está activo."
    )
    @ApiResponse(responseCode = "200", description = "El servicio está funcionando correctamente")
    @GetMapping("/public/status")
    public String getPublicStatus() {
        return "El servicio de circulación está funcionando correctamente";
    }
}
