package co.edu.hotel.configuracionservice.controller.habitacion;

import co.edu.hotel.configuracionservice.domain.habitacion.EstadoHabitacion;
import co.edu.hotel.configuracionservice.domain.habitacion.TipoHabitacion;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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

    @GetMapping
    public ResponseEntity<?> listarHabitacionesPaginadas(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        try {
            Page<HabitacionResponse> habitaciones = habitacionService.listarHabitacionesPaginadas(PageRequest.of(page, size));
            return ResponseEntity.ok(habitaciones);
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al listar habitaciones paginadas: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al listar habitaciones paginadas: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }

    @GetMapping("/por-estado")
    public ResponseEntity<?> listarPorEstado(@RequestParam EstadoHabitacion estado) {
        try {
            List<HabitacionResponse> habitaciones = habitacionService.listarPorEstado(estado);
            return ResponseEntity.ok(habitaciones);
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al consultar por estado: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al consultar por estado: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }

    @GetMapping("/por-hotel")
    public ResponseEntity<?> listarPorHotel(@RequestParam String hotelCodigo) {
        try {
            Hotel hotel = hotelRepository.findByHotelCodigo(hotelCodigo)
                    .orElseThrow(() -> new IllegalArgumentException("Hotel no encontrado con código: " + hotelCodigo));
            List<HabitacionResponse> habitaciones = habitacionService.listarPorHotel(hotel);
            return ResponseEntity.ok(habitaciones);
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al consultar por hotel: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al consultar por hotel: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }

    @GetMapping("/por-tipo")
    public ResponseEntity<?> listarPorTipo(@RequestParam TipoHabitacion tipo) {
        try {
            List<HabitacionResponse> habitaciones = habitacionService.listarPorTipo(tipo);
            return ResponseEntity.ok(habitaciones);
        } catch (IllegalArgumentException e) {
            logger.warn("Error de validación al consultar por tipo: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            logger.error("Error interno al consultar por tipo: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del servidor");
        }
    }
}