package co.edu.hotel.configuracionservice.services.habitacion;

import co.edu.hotel.configuracionservice.domain.habitacion.EstadoHabitacion;
import co.edu.hotel.configuracionservice.domain.habitacion.Habitacion;
import co.edu.hotel.configuracionservice.domain.habitacion.TipoHabitacion;
import co.edu.hotel.configuracionservice.domain.habitacion.dto.DesactivarHabitacionRequest;
import co.edu.hotel.configuracionservice.domain.habitacion.dto.HabitacionResponse;
import co.edu.hotel.configuracionservice.domain.hotel.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface IHabitacionService {

    Habitacion crearHabitacion(String habitacionId, String nombre, String tipo, int capacidad, Hotel hotel);

    HabitacionResponse desactivarPorMantenimiento(DesactivarHabitacionRequest request, String usuarioAdmin);


    Page<HabitacionResponse> listarHabitacionesPaginadas(Pageable pageable);
    List<HabitacionResponse> listarPorEstado(EstadoHabitacion estado);
    List<HabitacionResponse> listarPorHotel(Hotel hotel);
    List<HabitacionResponse> listarPorTipo(TipoHabitacion tipo);
}