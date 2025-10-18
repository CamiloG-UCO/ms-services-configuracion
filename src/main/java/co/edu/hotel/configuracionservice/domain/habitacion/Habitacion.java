package co.edu.hotel.configuracionservice.domain.habitacion;

import co.edu.hotel.configuracionservice.domain.hotel.Hotel;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.UUID;


@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "habitaciones")
public class Habitacion {


    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id")
    private UUID id;

    @Column(name = "habitacion_id", unique = true, nullable = false, length = 20)
    private String habitacionId;

    @Column(name = "nombre", nullable = false, length = 150)
    private String nombre;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hotel_id", nullable = false)
    private Hotel hotel;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false)
    private TipoHabitacion tipo;

    @Column(name = "capacidad", nullable = false)
    private Integer capacidad;

    @Enumerated(EnumType.STRING)
    @Column(name = "estado", nullable = false)
    private EstadoHabitacion estado;

    @Column(name = "motivo_desactivacion", length = 200)
    private String motivoDesactivacion;


    @Column(name = "fecha_cambio_estado")
    private LocalDateTime fechaCambioEstado;


    @Column(name = "usuario_cambio", length = 100)
    private String usuarioCambio;

    public Habitacion(String habitacionId, String nombre, Hotel hotel, TipoHabitacion tipo, Integer capacidad) {
        this.habitacionId = habitacionId;
        this.nombre = nombre;
        this.hotel = hotel;
        this.tipo = tipo;
        this.capacidad = capacidad;
        this.estado = EstadoHabitacion.ACTIVO;
        this.fechaCambioEstado = LocalDateTime.now();
    }

    public void desactivarPorMantenimiento(String motivo, String usuarioAdmin) {
        if (this.estado == EstadoHabitacion.INACTIVO) {
            throw new IllegalStateException(
                "La habitación " + this.habitacionId + " ya se encuentra inactiva"
            );
        }
        
        this.estado = EstadoHabitacion.INACTIVO;
        this.motivoDesactivacion = motivo;
        this.usuarioCambio = usuarioAdmin;
        this.fechaCambioEstado = LocalDateTime.now();
    }

    public void reactivar(String usuarioAdmin) {
        if (this.estado == EstadoHabitacion.ACTIVO) {
            throw new IllegalStateException(
                "La habitación " + this.habitacionId + " ya se encuentra activa"
            );
        }
        
        this.estado = EstadoHabitacion.ACTIVO;
        this.motivoDesactivacion = null;
        this.usuarioCambio = usuarioAdmin;
        this.fechaCambioEstado = LocalDateTime.now();
    }

    public boolean permiteReservas() {
        return this.estado.permiteReservas();
    }
}