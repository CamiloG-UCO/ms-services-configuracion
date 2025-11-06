package co.edu.hotel.configuracionservice.services.hotel;

import co.edu.hotel.configuracionservice.domain.hotel.Hotel;
import co.edu.hotel.configuracionservice.repository.hotel.IHotelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.regex.Pattern;

@Service
@Transactional
public class HotelService {

    // Patrón de regex para validar teléfono (7 a 15 dígitos)
    private static final Pattern PHONE_PATTERN = Pattern.compile("\\d{7,15}");
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

    public Hotel actualizarHotel(UUID hotelId, Hotel hotelRequest) {

        // Validaciones del request
        if (hotelRequest.getTelefono() == null || hotelRequest.getTelefono().isBlank()) {
            throw new IllegalArgumentException("Phone cannot be empty");
        }
        if (!PHONE_PATTERN.matcher(hotelRequest.getTelefono()).matches()) {
            throw new IllegalArgumentException("Invalid phone format");
        }
        if (hotelRequest.getDescripcion() == null) {
            // Permitimos "" (vacío) pero no null
            throw new IllegalArgumentException("Description cannot be null");
        }

        // Lógica para buscar y actualizar
        return hotelRepository.findById(hotelId)
                .map(existingHotel -> {
                    existingHotel.setNombre(hotelRequest.getNombre());
                    existingHotel.setDepartamento(hotelRequest.getDepartamento());
                    existingHotel.setCiudad(hotelRequest.getCiudad());
                    existingHotel.setPais(hotelRequest.getPais());
                    existingHotel.setDireccion(hotelRequest.getDireccion());
                    existingHotel.setTelefono(hotelRequest.getTelefono());
                    existingHotel.setDescripcion(hotelRequest.getDescripcion());
                    return hotelRepository.save(existingHotel);
                })
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
    }

    public List<Hotel> findAll(){
        return hotelRepository.findAll();
    }

    public Hotel findById(UUID id){
        return hotelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
    }

    public Hotel findByCodigo(String codigo){
        return hotelRepository.findByHotelCodigo(codigo)
                .orElseThrow(() -> new RuntimeException("Hotel not found"));
    }

}
