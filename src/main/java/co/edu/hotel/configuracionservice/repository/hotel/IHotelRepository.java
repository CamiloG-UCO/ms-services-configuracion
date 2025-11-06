package co.edu.hotel.configuracionservice.repository.hotel;

import co.edu.hotel.configuracionservice.domain.hotel.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


@Repository
public interface IHotelRepository extends JpaRepository<Hotel, UUID> {

    Optional<Hotel> findByNombre(String nombre);

    Optional<Hotel> findByHotelCodigo(String hotelCodigo);

    boolean existsByNombre(String nombre);
    boolean existsByTelefono(String telefono);

    @Query("SELECT COALESCE(MAX(CAST(SUBSTRING(h.hotelCodigo, 5) AS int)), 0) FROM Hotel h WHERE h.hotelCodigo LIKE 'HTL-%'")
    Integer findMaxCodigoNumero();
}