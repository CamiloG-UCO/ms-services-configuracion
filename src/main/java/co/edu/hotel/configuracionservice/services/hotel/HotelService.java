package co.edu.hotel.configuracionservice.services.hotel;

import co.edu.hotel.configuracionservice.domain.hotel.Hotel;
import co.edu.hotel.configuracionservice.repository.hotel.IHotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@Transactional
public class HotelService {

    private final IHotelRepository hotelRepository;

    public HotelService(IHotelRepository hotelRepository) {

        this.hotelRepository = hotelRepository;
    }

    public String registrarHotel(Hotel hotel){
        if (hotel.getNombre() == null || hotel.getNombre().isEmpty()){
            throw new IllegalArgumentException("El nombre del hotel es obligatorio");
        }
        String codigo = generarCodigoHotel();
        hotel.setHotelCodigo(codigo);

        hotelRepository.save(hotel);
        return "Hotel registrado exitosamente";
    }

    public Hotel obtenerHotelPorCodigo(String codigo){
        return hotelRepository.findByHotelCodigo(codigo)
                .orElseThrow(() -> new IllegalArgumentException("Hotel no encontrado"));
    }

    private String generarCodigoHotel() {
        Integer maxNumero = hotelRepository.findMaxCodigoNumero();
        int siguienteNumero = (maxNumero != null ? maxNumero : 0) + 1;
        return String.format("HTL-%03d", siguienteNumero);
    }

    public Hotel actualizarHotel(UUID hotelId, Hotel hotel) {
        return hotelRepository.findById(hotelId)
                .map(existingHotel -> {
                    existingHotel.setNombre(hotel.getNombre());
                    existingHotel.setHotelCodigo(hotel.getHotelCodigo());
                    existingHotel.setCiudad(hotel.getCiudad());
                    existingHotel.setDireccion(hotel.getDireccion());
                    existingHotel.setTelefono(hotel.getTelefono());
                    existingHotel.setDescripcion(hotel.getDescripcion());
                    return hotelRepository.save(existingHotel);
                })
                .orElseThrow(() -> new RuntimeException("Hotel no encontrado"));
    }

    public List<Hotel> findAll(){
        return hotelRepository.findAll();
    }

}
