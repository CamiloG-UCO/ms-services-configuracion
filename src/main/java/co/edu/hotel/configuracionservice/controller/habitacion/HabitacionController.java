package co.edu.hotel.configuracionservice.controller.habitacion;

import co.edu.hotel.configuracionservice.domain.habitacion.dto.DesactivarHabitacionRequest;
import co.edu.hotel.configuracionservice.domain.habitacion.dto.HabitacionResponse;
import co.edu.hotel.configuracionservice.domain.habitacion.Habitacion;
import co.edu.hotel.configuracionservice.domain.hotel.Hotel;
import co.edu.hotel.configuracionservice.repository.hotel.IHotelRepository;
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
    private final IHotelRepository hotelRepository;


    @Autowired
    public HabitacionController(IHabitacionService habitacionService, IHotelRepository hotelRepository) {
        this.habitacionService = habitacionService;
        this.hotelRepository = hotelRepository;
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

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> crearHabitacion(
            @RequestParam String habitacionId,
            @RequestParam String nombre,
            @RequestParam String tipo,
            @RequestParam int capacidad,
            @RequestParam String hotelCodigo
    ) {
        try {
            logger.info("Solicitud de creación de habitación: habitacionId={}, nombre={}, tipo={}, capacidad={}, hotel={}",
                    habitacionId, nombre, tipo, capacidad, hotelCodigo);


            Hotel hotel = hotelRepository.findByHotelCodigo(hotelCodigo)
                    .orElseThrow(() -> new IllegalArgumentException("Hotel no encontrado con código: " + hotelCodigo));
            Habitacion creada = habitacionService.crearHabitacion(habitacionId, nombre, tipo, capacidad, hotel);

            String mensaje = "Habitación creada exitosamente con ID " + creada.getHabitacionId();
            logger.info("Habitación creada: {}", creada.getHabitacionId());
            return ResponseEntity.ok(mensaje);

        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al crear habitación: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al crear habitación: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }
}