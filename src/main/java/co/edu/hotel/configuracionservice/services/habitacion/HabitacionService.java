package co.edu.hotel.configuracionservice.services.habitacion;

import co.edu.hotel.configuracionservice.domain.habitacion.Habitacion;
import co.edu.hotel.configuracionservice.domain.habitacion.EstadoHabitacion;
import co.edu.hotel.configuracionservice.domain.habitacion.TipoHabitacion;
import co.edu.hotel.configuracionservice.domain.habitacion.dto.DesactivarHabitacionRequest;
import co.edu.hotel.configuracionservice.domain.habitacion.dto.HabitacionResponse;
import co.edu.hotel.configuracionservice.domain.hotel.Hotel;
import co.edu.hotel.configuracionservice.repository.habitacion.IHabitacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;


@Service
@Transactional
public class HabitacionService implements IHabitacionService {

    private static final Logger logger = LoggerFactory.getLogger(HabitacionService.class);

    private final IHabitacionRepository habitacionRepository;

    private final INotificacionModulosService notificacionService;

    private final IAutorizacionService autorizacionService;

    @Autowired
    public HabitacionService(IHabitacionRepository habitacionRepository,
                            INotificacionModulosService notificacionService,
                             IAutorizacionService autorizacionService) {
        this.habitacionRepository = habitacionRepository;
        this.notificacionService = notificacionService;
        this.autorizacionService = autorizacionService;
        logger.info("HabitacionService inicializado");
    }

    //Crea una nueva habitación validando que no exista por habitacionId.
    @Override
    public Habitacion crearHabitacion(String habitacionId, String nombre, String tipo, int capacidad, Hotel hotel) {
        logger.info("Creando habitación - ID: {}, Nombre: {}, Tipo: {}, Capacidad: {}, Hotel: {}",
                habitacionId, nombre, tipo, capacidad, hotel != null ? hotel.getNombre() : null);

        if (hotel == null) {
            throw new IllegalArgumentException("Hotel no encontrado");
        }

        if (habitacionRepository.existsByHabitacionId(habitacionId)) {
            throw new IllegalArgumentException("Ya existe una habitación con el ID: " + habitacionId);
        }

        TipoHabitacion tipoEnum;
        try {
            tipoEnum = TipoHabitacion.valueOf(tipo.toUpperCase());
        } catch (Exception e) {
            logger.warn("Tipo de habitación inválido: {}", tipo);
            throw new IllegalArgumentException("Tipo de habitación inválido: " + tipo);
        }

        Habitacion habitacion = Habitacion.builder()
                .habitacionId(habitacionId)
                .nombre(nombre)
                .hotel(hotel)
                .tipo(tipoEnum)
                .capacidad(capacidad)
                .estado(EstadoHabitacion.ACTIVO)
                .fechaCambioEstado(LocalDateTime.now())
                .build();

        Habitacion saved = habitacionRepository.save(habitacion);
        logger.info("Habitación creada con ID: {}", saved.getHabitacionId());

        // notificar si corresponde (se mantiene la inyección en caso de que sea necesario)
        try {
            notificacionService.notificarTodosLosModulos(saved, "CREACION");
        } catch (Exception ex) {
            logger.debug("No se pudo notificar a los módulos: {}", ex.getMessage());
        }

        return saved;
    }

    @Override
    public HabitacionResponse desactivarPorMantenimiento(DesactivarHabitacionRequest request, String usuarioAdmin) {

        if (!autorizacionService.tieneRol(usuarioAdmin, "ADMIN")) {
            throw new AccessDeniedException("Usuario no autorizado para desactivar habitaciones");
        }

        logger.info("Iniciando desactivación - Hotel: {}, Habitación: {}, Usuario: {}",
                request.getNombreHotel(), request.getNumeroHabitacion(), usuarioAdmin);

        try {

            Optional<Habitacion> habitacionOpt = habitacionRepository
                    .findByHotelNombreAndHabitacionId(request.getNombreHotel(), request.getNumeroHabitacion());

            if (habitacionOpt.isEmpty()) {
                logger.warn("Habitación no encontrada - Hotel: {}, Habitación: {}",
                        request.getNombreHotel(), request.getNumeroHabitacion());
                throw new IllegalArgumentException("Habitación no encontrada");
            }

            Habitacion habitacion = habitacionOpt.get();


            if (habitacion.getEstado() == EstadoHabitacion.INACTIVO) {
                logger.warn("Habitación ya está inactiva - ID: {}", habitacion.getHabitacionId());
                throw new IllegalStateException("La habitación ya está desactivada");
            }


            habitacion.setEstado(EstadoHabitacion.INACTIVO);
            habitacion.setMotivoDesactivacion(request.getMotivoDesactivacion());
            habitacion.setFechaCambioEstado(LocalDateTime.now());
            habitacion.setUsuarioCambio(usuarioAdmin);


            habitacion = habitacionRepository.save(habitacion);
            logger.info("Habitación desactivada exitosamente - ID: {}", habitacion.getHabitacionId());


            try {
                notificacionService.notificarTodosLosModulos(habitacion, "DESACTIVACION");
                logger.info("Notificación enviada exitosamente");
            } catch (Exception e) {
                logger.warn("Error al enviar notificación: {}", e.getMessage());

            }

            HabitacionResponse resp = new HabitacionResponse(
                    habitacion.getId(),
                    habitacion.getHabitacionId(),
                    habitacion.getNombre(),
                    habitacion.getHotel().getNombre(),
                    habitacion.getEstado(),
                    "Habitación desactivada exitosamente"
            );

            resp.setMotivoDesactivacion(habitacion.getMotivoDesactivacion());
            resp.setFechaCambioEstado(habitacion.getFechaCambioEstado());
            resp.setUsuarioCambio(habitacion.getUsuarioCambio());

            return resp;

        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Error de validación: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error interno al desactivar habitación: {}", e.getMessage(), e);
            throw new RuntimeException("Error interno del servidor", e);
        }
    }

    @Override
    @Transactional
    public HabitacionResponse reactivarHabitacion(String nombreHotel, String numeroHabitacion, String usuarioAdmin) {
        var habitacion = habitacionRepository
                .findByHotelNombreAndHabitacionId(nombreHotel, numeroHabitacion)
                .orElseThrow(() -> new IllegalArgumentException("Habitación no encontrada"));

        if (habitacion.getEstado() == EstadoHabitacion.ACTIVO) {
            throw new IllegalStateException("La habitación ya está activa");
        }

        habitacion.reactivar(usuarioAdmin); // o setEstado + fecha/usuario
        habitacion = habitacionRepository.save(habitacion);

        HabitacionResponse resp = new HabitacionResponse(
                habitacion.getId(),
                habitacion.getHabitacionId(),
                habitacion.getNombre(),
                habitacion.getHotel().getNombre(),
                habitacion.getEstado(),
                "Habitación reactivada exitosamente"
        );
        resp.setMotivoDesactivacion(habitacion.getMotivoDesactivacion()); // normalmente null al activar
        resp.setFechaCambioEstado(habitacion.getFechaCambioEstado());
        resp.setUsuarioCambio(habitacion.getUsuarioCambio());
        return resp;
    }

    @Override
    public Page<HabitacionResponse> listarHabitacionesPaginadas(Pageable pageable) {
        Page<Habitacion> pageHabitaciones = habitacionRepository.findAll(pageable);
        return pageHabitaciones.map(this::mapearAResponse);
    }


    private HabitacionResponse crearRespuestaExitosa(Habitacion habitacion, String mensaje) {
        HabitacionResponse response = mapearAResponse(habitacion);
        response.setMensaje(mensaje);
        return response;
    }


    private HabitacionResponse mapearAResponse(Habitacion habitacion) {
        HabitacionResponse response = new HabitacionResponse();
        response.setId(habitacion.getId());
        response.setHabitacionId(habitacion.getHabitacionId());
        response.setNombre(habitacion.getNombre());
        response.setNombreHotel(habitacion.getHotel() != null ? habitacion.getHotel().getNombre() : null);
        response.setTipo(habitacion.getTipo() != null ? habitacion.getTipo().getDescripcion() : null);
        response.setCapacidad(habitacion.getCapacidad());
        response.setEstado(habitacion.getEstado());
        response.setEstadoDescripcion(habitacion.getEstado().getDescripcion());
        response.setMotivoDesactivacion(habitacion.getMotivoDesactivacion());
        response.setFechaCambioEstado(habitacion.getFechaCambioEstado());
        response.setUsuarioCambio(habitacion.getUsuarioCambio());
        response.setPermiteReservas(habitacion.permiteReservas());
        return response;
    }

    @Override
    public List<HabitacionResponse> listarPorEstado(EstadoHabitacion estado) {
        List<Habitacion> habitaciones = habitacionRepository.findByEstado(estado);
        return habitaciones.stream().map(this::mapearAResponse).toList();
    }

    @Override
    public List<HabitacionResponse> listarPorHotel(Hotel hotel) {
        List<Habitacion> habitaciones = habitacionRepository.findByHotel(hotel);
        return habitaciones.stream().map(this::mapearAResponse).toList();
    }

    @Override
    public List<HabitacionResponse> listarPorTipo(TipoHabitacion tipo) {
        List<Habitacion> habitaciones = habitacionRepository.findByTipo(tipo);
        return habitaciones.stream().map(this::mapearAResponse).toList();
    }
}

