package co.edu.hotel.configuracionservice.domain.habitacion.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HabitacionRequest {

    private String hotelNombre;          // "Santa Marta Resort"
    private String nombre;               // "Suite Mar Caribe"
    private String tipo;                 // "Premium"
    private Integer numeroCamas;         // "2"
    private Integer capacidadPersonas;   // "3"
    private String descripcion;          // "Jacuzzi, balcón con vista al mar"
    private String estado;               // "Disponible"
}