package co.edu.hotel.configuracionservice.repository.habitacion;

import co.edu.hotel.configuracionservice.domain.habitacion.Habitacion;
import co.edu.hotel.configuracionservice.domain.habitacion.EstadoHabitacion;
import co.edu.hotel.configuracionservice.domain.habitacion.TipoHabitacion;
import co.edu.hotel.configuracionservice.domain.hotel.Hotel;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface IHabitacionRepository extends JpaRepository<Habitacion, UUID> {


    @Query("SELECT h FROM Habitacion h WHERE h.hotel.nombre = :nombreHotel AND h.habitacionId = :habitacionId")
    Optional<Habitacion> findByHotelNombreAndHabitacionId(@Param("nombreHotel") String nombreHotel, @Param("habitacionId") String habitacionId);

    Optional<Habitacion> findByHabitacionId(String habitacionId);

    boolean existsByHabitacionId(String habitacionId);


    @Query("SELECT h FROM Habitacion h WHERE h.hotel.nombre = :nombreHotel AND h.estado = :estado")
    List<Habitacion> findHabitacionesParaNotificacion(
        @Param("nombreHotel") String nombreHotel, 
        @Param("estado") EstadoHabitacion estado
    );


    @Query("SELECT COUNT(h) > 0 FROM Habitacion h WHERE h.hotel.nombre = :nombreHotel AND h.habitacionId = :habitacionId")
    boolean existsByHotelNombreAndHabitacionId(@Param("nombreHotel") String nombreHotel, @Param("habitacionId") String habitacionId);

    boolean existsByHabitacionIdAndHotel(String habitacionId, Hotel hotel);

    @Query("SELECT COUNT(h) FROM Habitacion h WHERE h.hotel.nombre = :nombreHotel AND h.estado = 'ACTIVO'")
    long countHabitacionesActivasByHotel(@Param("nombreHotel") String nombreHotel);


    @Query("SELECT COUNT(h) FROM Habitacion h WHERE h.hotel.nombre = :nombreHotel AND h.estado = 'INACTIVO'")
    long countHabitacionesInactivasByHotel(@Param("nombreHotel") String nombreHotel);

    Optional<Habitacion> findById(UUID id);

    @Query("SELECT h FROM Habitacion h JOIN FETCH h.hotel WHERE h.habitacionId = :habitacionId")
    Optional<Habitacion> findWithHotelByHabitacionId(@Param("habitacionId") String habitacionId);

    Page<Habitacion> findAll(Pageable pageable);
    List<Habitacion> findByEstado(EstadoHabitacion estado);
    List<Habitacion> findByHotel(Hotel hotel);
    List<Habitacion> findByTipo(TipoHabitacion tipo);
}
