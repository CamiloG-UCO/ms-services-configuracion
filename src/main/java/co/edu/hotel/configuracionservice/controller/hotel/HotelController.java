package co.edu.hotel.configuracionservice.controller.hotel;

import co.edu.hotel.configuracionservice.domain.hotel.Hotel;
import co.edu.hotel.configuracionservice.repository.hotel.IHotelRepository;
import co.edu.hotel.configuracionservice.services.hotel.HotelService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/hotel")
public class HotelController {

    private final HotelService hotelService;
    private final IHotelRepository hotelRepository;


    public HotelController(HotelService hotelService, IHotelRepository hotelRepository) {
        this.hotelService = hotelService;
        this.hotelRepository = hotelRepository;
    }

    @PostMapping()
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> addHotel(@RequestBody Hotel hotel) {
        try{
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(hotelService.registrarHotel(hotel));
        }catch(Exception e){
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<Hotel>> findAll(){
        try{
            return new ResponseEntity<>(hotelService.findAll(), HttpStatus.OK);

        }catch(Exception ex){
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }


    @PutMapping("/{hotelId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String>  updateHotel(@PathVariable UUID hotelId, @RequestBody Hotel hotel) {

        try {
            hotelService.actualizarHotel(hotelId, hotel);
            return ResponseEntity.ok("Información del hotel actualizada exitosamente");

        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Error: " + e.getMessage());

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error interno del sistema");
        }
    }
}
