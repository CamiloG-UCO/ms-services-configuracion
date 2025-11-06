package co.edu.hotel.configuracionservice.services.habitacion;

import co.edu.hotel.configuracionservice.domain.habitacion.Habitacion;
import co.edu.hotel.configuracionservice.domain.habitacion.dto.DesactivarHabitacionRequest;
import co.edu.hotel.configuracionservice.domain.habitacion.dto.HabitacionResponse;
import co.edu.hotel.configuracionservice.domain.hotel.Hotel;


public interface IHabitacionService {

    Habitacion crearHabitacion(String habitacionId, String nombre, String tipo, int capacidad, Hotel hotel);

    HabitacionResponse desactivarPorMantenimiento(DesactivarHabitacionRequest request, String usuarioAdmin);

    HabitacionResponse reactivarHabitacion(String nombreHotel, String numeroHabitacion, String usuarioAdmin);
}