package co.edu.hotel.configuracionservice.domain.habitacion.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@NoArgsConstructor
@AllArgsConstructor
public class DesactivarHabitacionRequest {


    @NotBlank(message = "El nombre del hotel es obligatorio")
    @Size(max = 100, message = "El nombre del hotel no puede exceder 100 caracteres")
    private String nombreHotel;


    @NotBlank(message = "El número de habitación es obligatorio")
    @Size(max = 10, message = "El número de habitación no puede exceder 10 caracteres")
    private String numeroHabitacion;


    @NotBlank(message = "El motivo de desactivación es obligatorio")
    @Size(max = 200, message = "El motivo no puede exceder 200 caracteres")
    private String motivoDesactivacion;


    @Size(max = 100, message = "El usuario no puede exceder 100 caracteres")
    private String usuarioSolicitante;
}