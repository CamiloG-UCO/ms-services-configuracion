package co.edu.hotel.configuracionservice.repository.hotel;

import co.edu.hotel.configuracionservice.domain.hotel.Hotel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;


@Repository
public interface IHotelRepository extends JpaRepository<Hotel, UUID> {


    Optional<Hotel> findByNombre(String nombre);

    boolean existsByNombre(String nombre);
}