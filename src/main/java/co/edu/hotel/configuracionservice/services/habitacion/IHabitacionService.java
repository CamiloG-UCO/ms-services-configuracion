package co.edu.hotel.configuracionservice.services.habitacion;

import co.edu.hotel.configuracionservice.domain.habitacion.dto.DesactivarHabitacionRequest;
import co.edu.hotel.configuracionservice.domain.habitacion.dto.HabitacionResponse;

import java.util.List;


public interface IHabitacionService {


    HabitacionResponse desactivarPorMantenimiento(DesactivarHabitacionRequest request, String usuarioAdmin);
}