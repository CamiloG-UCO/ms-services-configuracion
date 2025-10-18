package co.edu.hotel.configuracionservice.config;

import co.edu.hotel.configuracionservice.domain.habitacion.EstadoHabitacion;
import co.edu.hotel.configuracionservice.domain.habitacion.Habitacion;
import co.edu.hotel.configuracionservice.domain.habitacion.TipoHabitacion;
import co.edu.hotel.configuracionservice.domain.hotel.Hotel;
import co.edu.hotel.configuracionservice.domain.user.Role;
import co.edu.hotel.configuracionservice.repository.habitacion.IHabitacionRepository;
import co.edu.hotel.configuracionservice.repository.hotel.IHotelRepository;
import co.edu.hotel.configuracionservice.repository.user.IRoleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;


@Component
@RequiredArgsConstructor
@Slf4j
public class DataInitializer implements CommandLineRunner {

    private final IRoleRepository roleRepository;
    private final IHotelRepository hotelRepository;
    private final IHabitacionRepository habitacionRepository;


    @Override
    public void run(String... args) {
        createRolesIfNotExist();
        createHotelesIfNotExist();
        createHabitacionesIfNotExist();
    }


    private void createRolesIfNotExist() {
        createRoleIfNotExist("ADMIN", "Administrador del sistema con acceso completo");
        createRoleIfNotExist("CUSTOMER", "Cliente con acceso a consultas básicas");
    }


    private void createRoleIfNotExist(String roleName, String description) {
        if (!roleRepository.existsByName(roleName)) {
            Role role = Role.builder()
                    .name(roleName)
                    .description(description)
                    .active(true)
                    .build();
            
            roleRepository.save(role);
            log.info("Rol creado: {}", roleName);
        } else {
            log.debug("Rol ya existe: {}", roleName);
        }
    }


    private void createHotelesIfNotExist() {
        createHotelIfNotExist("Santa Marta Resort", "Carrera 1 # 2-3, Santa Marta", "3001234567", "info@santamartaresort.com");
        createHotelIfNotExist("Hotel Caribe", "Avenida Santander # 5-10, Cartagena", "3009876543", "reservas@hotelcaribe.com");
    }


    private void createHotelIfNotExist(String nombre, String direccion, String telefono, String email) {
        if (!hotelRepository.existsByNombre(nombre)) {
            Hotel hotel = Hotel.builder()
                    .nombre(nombre)
                    .direccion(direccion)
                    .telefono(telefono)
                    .email(email)
                    .activo(true)
                    .build();
            
            hotelRepository.save(hotel);
            log.info("Hotel creado: {}", nombre);
        } else {
            log.debug("Hotel ya existe: {}", nombre);
        }
    }


    private void createHabitacionesIfNotExist() {

        Hotel santaMarta = hotelRepository.findByNombre("Santa Marta Resort").orElse(null);
        Hotel hotelCaribe = hotelRepository.findByNombre("Hotel Caribe").orElse(null);

        if (santaMarta != null) {
            createHabitacionesForHotel(santaMarta);
        }

        if (hotelCaribe != null) {
            createHabitacionesForHotel(hotelCaribe);
        }
    }


    private void createHabitacionesForHotel(Hotel hotel) {

        createHabitacionIfNotExist(hotel, "H-101", "Habitación Económica 101", TipoHabitacion.ECONOMICA, 2, EstadoHabitacion.ACTIVO);
        createHabitacionIfNotExist(hotel, "H-102", "Habitación Standard 102", TipoHabitacion.STANDARD, 2, EstadoHabitacion.ACTIVO);
        createHabitacionIfNotExist(hotel, "H-103", "Habitación Standard 103", TipoHabitacion.STANDARD, 3, EstadoHabitacion.INACTIVO, "Mantenimiento programado");


        createHabitacionIfNotExist(hotel, "H-201", "Habitación Premium 201", TipoHabitacion.PREMIUM, 3, EstadoHabitacion.ACTIVO);
        createHabitacionIfNotExist(hotel, "H-202", "Habitación Premium 202", TipoHabitacion.PREMIUM, 4, EstadoHabitacion.ACTIVO);
        createHabitacionIfNotExist(hotel, "H-203", "Habitación Ejecutiva 203", TipoHabitacion.EJECUTIVA, 2, EstadoHabitacion.ACTIVO);


        createHabitacionIfNotExist(hotel, "H-301", "Suite Junior 301", TipoHabitacion.SUITE, 4, EstadoHabitacion.ACTIVO);
        createHabitacionIfNotExist(hotel, "H-302", "Suite Presidencial 302", TipoHabitacion.SUITE, 6, EstadoHabitacion.ACTIVO);
        createHabitacionIfNotExist(hotel, "H-303", "Suite VIP 303", TipoHabitacion.SUITE, 4, EstadoHabitacion.INACTIVO, "Desactivar por mantenimiento");

        log.info("Habitaciones creadas para hotel: {}", hotel.getNombre());
    }


    private void createHabitacionIfNotExist(Hotel hotel, String habitacionId, String nombre, 
                                          TipoHabitacion tipo, Integer capacidad, EstadoHabitacion estado) {
        createHabitacionIfNotExist(hotel, habitacionId, nombre, tipo, capacidad, estado, null);
    }


    private void createHabitacionIfNotExist(Hotel hotel, String habitacionId, String nombre, 
                                          TipoHabitacion tipo, Integer capacidad, EstadoHabitacion estado, String motivoDesactivacion) {
        if (!habitacionRepository.existsByHabitacionIdAndHotel(habitacionId, hotel)) {
            Habitacion habitacion = Habitacion.builder()
                    .habitacionId(habitacionId)
                    .nombre(nombre)
                    .hotel(hotel)
                    .tipo(tipo)
                    .capacidad(capacidad)
                    .estado(estado)
                    .motivoDesactivacion(motivoDesactivacion)
                    .fechaCambioEstado(LocalDateTime.now())
                    .usuarioCambio("system-init")
                    .build();
            
            habitacionRepository.save(habitacion);
            log.info("Habitación creada: {} - {} ({})", habitacionId, nombre, estado);
        } else {
            log.debug("Habitación ya existe: {} para hotel {}", habitacionId, hotel.getNombre());
        }
    }
}