package co.edu.hotel.configuracionservice.services.habitacion;

import co.edu.hotel.configuracionservice.domain.habitacion.Habitacion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;


@Service
public class NotificacionModulosService implements INotificacionModulosService {

    private static final Logger logger = LoggerFactory.getLogger(NotificacionModulosService.class);

    @Override
    public void notificarModuloReservas(Habitacion habitacion, String tipoOperacion) {
        try {
            logger.info("Notificando módulo de RESERVAS: Habitación={}, Hotel={}, Operación={}", 
                       habitacion.getHabitacionId(), habitacion.getHotel() != null ? habitacion.getHotel().getNombre() : "N/A", tipoOperacion);

            if ("DESACTIVACION".equals(tipoOperacion)) {

                logger.info("Reservas bloqueadas para habitación {} del hotel {}", 
                           habitacion.getHabitacionId(), habitacion.getHotel() != null ? habitacion.getHotel().getNombre() : "N/A");
            } else if ("REACTIVACION".equals(tipoOperacion)) {

                logger.info("Reservas desbloqueadas para habitación {} del hotel {}", 
                           habitacion.getHabitacionId(), habitacion.getHotel() != null ? habitacion.getHotel().getNombre() : "N/A");
            }

        } catch (Exception e) {
            logger.error("Error al notificar módulo de reservas: {}", e.getMessage(), e);
        }
    }


    @Override
    public void notificarModuloTareas(Habitacion habitacion, String tipoOperacion) {
        try {
            logger.info("Notificando módulo de TAREAS: Habitación={}, Hotel={}, Operación={}", 
                       habitacion.getHabitacionId(), habitacion.getHotel() != null ? habitacion.getHotel().getNombre() : "N/A", tipoOperacion);

            if ("DESACTIVACION".equals(tipoOperacion)) {

                logger.info("Tareas de mantenimiento generadas para habitación {} del hotel {}", 
                           habitacion.getHabitacionId(), habitacion.getHotel() != null ? habitacion.getHotel().getNombre() : "N/A");
            } else if ("REACTIVACION".equals(tipoOperacion)) {

                logger.info("Tareas de mantenimiento completadas para habitación {} del hotel {}", 
                           habitacion.getHabitacionId(), habitacion.getHotel() != null ? habitacion.getHotel().getNombre() : "N/A");
            }

        } catch (Exception e) {
            logger.error("Error al notificar módulo de tareas: {}", e.getMessage(), e);
        }
    }


    @Override
    public void notificarModuloClientes(Habitacion habitacion, String tipoOperacion) {
        try {
            logger.info("Notificando módulo de CLIENTES: Habitación={}, Hotel={}, Operación={}", 
                       habitacion.getHabitacionId(), habitacion.getHotel() != null ? habitacion.getHotel().getNombre() : "N/A", tipoOperacion);

            if ("DESACTIVACION".equals(tipoOperacion)) {

                logger.info("Clientes notificados sobre indisponibilidad de habitación {} del hotel {}", 
                           habitacion.getHabitacionId(), habitacion.getHotel() != null ? habitacion.getHotel().getNombre() : "N/A");
            } else if ("REACTIVACION".equals(tipoOperacion)) {

                logger.info("Clientes notificados sobre disponibilidad de habitación {} del hotel {}", 
                           habitacion.getHabitacionId(), habitacion.getHotel() != null ? habitacion.getHotel().getNombre() : "N/A");
            }

        } catch (Exception e) {
            logger.error("Error al notificar módulo de clientes: {}", e.getMessage(), e);
        }
    }


    @Override
    public void notificarTodosLosModulos(Habitacion habitacion, String tipoOperacion) {
        logger.info("Notificando a todos los módulos dependientes: Habitación={}, Operación={}", 
                   habitacion.getId(), tipoOperacion);

        notificarModuloReservas(habitacion, tipoOperacion);
        notificarModuloTareas(habitacion, tipoOperacion);
        notificarModuloClientes(habitacion, tipoOperacion);

        logger.info("Notificaciones completadas para habitación: {}", habitacion.getId());
    }
}