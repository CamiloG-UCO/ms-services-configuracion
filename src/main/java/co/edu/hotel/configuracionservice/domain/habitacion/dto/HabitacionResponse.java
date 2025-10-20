package co.edu.hotel.configuracionservice.domain.habitacion.dto;

import co.edu.hotel.configuracionservice.domain.habitacion.EstadoHabitacion;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class HabitacionResponse {


    private UUID id;

    private String habitacionId;

    private String nombre;

    private String nombreHotel;

    private String tipo;

    private Integer capacidad;

    private EstadoHabitacion estado;

    private String estadoDescripcion;

    private String motivoDesactivacion;

    private LocalDateTime fechaCambioEstado;

    private String usuarioCambio;

    private boolean permiteReservas;

    private String mensaje;

    public HabitacionResponse(UUID id, String habitacionId, String nombre, String nombreHotel, 
                             EstadoHabitacion estado, String mensaje) {
        this.id = id;
        this.habitacionId = habitacionId;
        this.nombre = nombre;
        this.nombreHotel = nombreHotel;
        this.estado = estado;
        this.estadoDescripcion = estado.getDescripcion();
        this.permiteReservas = estado.permiteReservas();
        this.mensaje = mensaje;
    }
}