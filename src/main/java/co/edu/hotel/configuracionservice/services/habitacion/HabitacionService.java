package co.edu.hotel.configuracionservice.services.habitacion;

import co.edu.hotel.configuracionservice.domain.habitacion.Habitacion;
import co.edu.hotel.configuracionservice.domain.habitacion.EstadoHabitacion;
import co.edu.hotel.configuracionservice.domain.habitacion.dto.DesactivarHabitacionRequest;
import co.edu.hotel.configuracionservice.domain.habitacion.dto.HabitacionResponse;
import co.edu.hotel.configuracionservice.repository.habitacion.IHabitacionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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


    @Autowired
    public HabitacionService(IHabitacionRepository habitacionRepository,
                            INotificacionModulosService notificacionService) {
        this.habitacionRepository = habitacionRepository;
        this.notificacionService = notificacionService;
        logger.info("HabitacionService inicializado");
    }


    @Override
    public HabitacionResponse desactivarPorMantenimiento(DesactivarHabitacionRequest request, 
                                                        String usuarioAdmin) {
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


            return new HabitacionResponse(
                habitacion.getId(),
                habitacion.getHabitacionId(),
                habitacion.getNombre(),
                habitacion.getHotel().getNombre(),
                habitacion.getEstado(),
                "Habitación desactivada exitosamente"
            );

        } catch (IllegalArgumentException | IllegalStateException e) {
            logger.warn("Error de validación: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            logger.error("Error interno al desactivar habitación: {}", e.getMessage(), e);
            throw new RuntimeException("Error interno del servidor", e);
        }
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
}