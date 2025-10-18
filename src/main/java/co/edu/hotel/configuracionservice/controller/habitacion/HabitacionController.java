package co.edu.hotel.configuracionservice.controller.habitacion;

import co.edu.hotel.configuracionservice.domain.habitacion.dto.DesactivarHabitacionRequest;
import co.edu.hotel.configuracionservice.domain.habitacion.dto.HabitacionResponse;
import co.edu.hotel.configuracionservice.services.habitacion.IHabitacionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@RequestMapping("/api/v1/habitaciones")
@CrossOrigin(origins = "*")
public class HabitacionController {

    private static final Logger logger = LoggerFactory.getLogger(HabitacionController.class);

    private final IHabitacionService habitacionService;


    @Autowired
    public HabitacionController(IHabitacionService habitacionService) {
        this.habitacionService = habitacionService;
        logger.info("HabitacionController inicializado");
    }

    @PostMapping("/desactivar")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<HabitacionResponse> desactivarPorMantenimiento(
            @Valid @RequestBody DesactivarHabitacionRequest request,
            Authentication authentication) {

        try {

            String usuarioAdmin = authentication.getName();

            logger.info("Solicitud de desactivación recibida: Hotel={}, Habitación={}, Usuario={}",
                    request.getNombreHotel(), request.getNumeroHabitacion(), usuarioAdmin);


            HabitacionResponse response = habitacionService.desactivarPorMantenimiento(request, usuarioAdmin);

            logger.info("Habitación desactivada exitosamente: {}", response.getId());

            return ResponseEntity.ok(response);

        } catch (IllegalArgumentException e) {

            logger.warn("Error de validación: {}", e.getMessage());
            return ResponseEntity.badRequest()
                    .body(new HabitacionResponse(null, null, null, request.getNombreHotel(),
                            null, e.getMessage()));

        } catch (IllegalStateException e) {

            logger.warn("Error de estado: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.CONFLICT)
                    .body(new HabitacionResponse(null, null, null, request.getNombreHotel(),
                            null, e.getMessage()));

        } catch (Exception e) {

            logger.error("Error interno al desactivar habitación: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new HabitacionResponse(null, null, null, request.getNombreHotel(),
                            null, "Error interno del servidor"));
        }
    }


}