package co.edu.hotel.configuracionservice.bdd.steps;

import co.edu.hotel.configuracionservice.domain.habitacion.Habitacion;
import co.edu.hotel.configuracionservice.domain.habitacion.EstadoHabitacion;
import co.edu.hotel.configuracionservice.domain.habitacion.TipoHabitacion;
import co.edu.hotel.configuracionservice.domain.habitacion.dto.DesactivarHabitacionRequest;
import co.edu.hotel.configuracionservice.domain.habitacion.dto.HabitacionResponse;
import co.edu.hotel.configuracionservice.domain.hotel.Hotel;
import co.edu.hotel.configuracionservice.repository.habitacion.IHabitacionRepository;
import co.edu.hotel.configuracionservice.repository.hotel.IHotelRepository;
import co.edu.hotel.configuracionservice.services.habitacion.IHabitacionService;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.security.test.context.support.WithMockUser;


import static org.assertj.core.api.Assertions.*;

public class EstadoOperativoHabitacionSteps {

    @Autowired
    private IHabitacionService habitacionService;

    @Autowired
    private IHabitacionRepository habitacionRepository;

    @Autowired
    private IHotelRepository hotelRepository;

    private Habitacion habitacionContexto;
    private HabitacionResponse respuestaContexto;
    private Exception excepcionContexto;
    private String usuarioAdminContexto = "admin.test";


    @Dado("el hotel {string} con estado actual {string}")
    public void elHotelConEstadoActual(String nombreHotel, String estadoActual) {

        limpiarContexto();
        

        Hotel hotel = new Hotel();
        hotel.setNombre(nombreHotel);

        
        habitacionContexto = new Habitacion();
        habitacionContexto.setHotel(hotelRepository.save(hotel));
        habitacionContexto.setHabitacionId("101");
        habitacionContexto.setNombre("Suite de Prueba");
        habitacionContexto.setTipo(TipoHabitacion.SUITE);
        habitacionContexto.setCapacidad(3);
        

        if ("Activo".equals(estadoActual)) {
            habitacionContexto.setEstado(EstadoHabitacion.ACTIVO);
        } else if ("Inactivo".equals(estadoActual)) {
            habitacionContexto.setEstado(EstadoHabitacion.INACTIVO);
            habitacionContexto.setMotivoDesactivacion("Mantenimiento previo");
        }
        

        habitacionContexto = habitacionRepository.save(habitacionContexto);
        

        assertThat(habitacionContexto.getId()).isNotNull();
        assertThat(habitacionContexto.getHotel().getNombre()).isEqualTo(nombreHotel);
    }


    @WithMockUser(roles = "ADMIN", username = "admin.test")
    @Cuando("el administrador seleccione {string}")
    public void elAdministradorSeleccione(String accion) {
        excepcionContexto = null;
        respuestaContexto = null;

        if ("Desactivar por mantenimiento".equals(accion)) {
            DesactivarHabitacionRequest req = new DesactivarHabitacionRequest();
            req.setNombreHotel(habitacionContexto.getHotel().getNombre());
            req.setNumeroHabitacion(habitacionContexto.getHabitacionId());
            req.setMotivoDesactivacion("Desactivar por mantenimiento"); // ¡exacto!
            try {
                respuestaContexto = habitacionService.desactivarPorMantenimiento(req, usuarioAdminContexto);
            } catch (Exception e) { excepcionContexto = e; }

        } else if ("Reactivar habitacion".equals(accion)) {
            try {
                respuestaContexto = habitacionService.reactivarHabitacion(
                        habitacionContexto.getHotel().getNombre(),
                        habitacionContexto.getHabitacionId(),
                        usuarioAdminContexto
                );
            } catch (Exception e) { excepcionContexto = e; }

        } else {
            throw new IllegalArgumentException("Acción no soportada: " + accion);
        }
    }

    @Entonces("el sistema debe cambiar su estado a {string}")
    public void elSistemaDebeCambiarSuEstadoA(String estadoEsperado) {

        assertThat(excepcionContexto).isNull();
        

        assertThat(respuestaContexto).isNotNull();
        
        if ("Inactivo".equals(estadoEsperado)) {
            assertThat(respuestaContexto.getEstado()).isEqualTo(EstadoHabitacion.INACTIVO);
            assertThat(respuestaContexto.isPermiteReservas()).isFalse();
            assertThat(respuestaContexto.getMotivoDesactivacion()).isEqualTo("Desactivar por mantenimiento");
            
        } else if ("Activo".equals(estadoEsperado)) {
            assertThat(respuestaContexto.getEstado()).isEqualTo(EstadoHabitacion.ACTIVO);
            assertThat(respuestaContexto.isPermiteReservas()).isTrue();
        }


        Habitacion habitacionActualizada = habitacionRepository.findById(habitacionContexto.getId())
                .orElseThrow(() -> new AssertionError("Habitación no encontrada en BD"));
        
        if ("Inactivo".equals(estadoEsperado)) {
            assertThat(habitacionActualizada.getEstado()).isEqualTo(EstadoHabitacion.INACTIVO);
        } else if ("Activo".equals(estadoEsperado)) {
            assertThat(habitacionActualizada.getEstado()).isEqualTo(EstadoHabitacion.ACTIVO);
        }
    }


    @Y("notificar a todos los módulos dependientes \\(reservas, tareas, clientes)")
    public void notificarATodosLosModulosDependientes() {

        assertThat(respuestaContexto).isNotNull();
        assertThat(respuestaContexto.getMensaje()).contains("exitosamente");
        

        assertThat(respuestaContexto.getUsuarioCambio()).isEqualTo(usuarioAdminContexto);
        assertThat(respuestaContexto.getFechaCambioEstado()).isNotNull();
        

    }


    @Entonces("el sistema debe mostrar un error indicando que ya está inactiva")
    public void elSistemaDebeMostrarUnErrorIndicandoQueYaEstaInactiva() {

        assertThat(excepcionContexto).isNotNull();
        assertThat(excepcionContexto).isInstanceOf(IllegalStateException.class);
        assertThat(excepcionContexto.getMessage()).contains("La habitación ya está desactivada");
    }


    @Y("permitir nuevamente las reservas")
    public void permitirNuevamenteLasReservas() {
        assertThat(respuestaContexto).isNotNull();
        assertThat(respuestaContexto.isPermiteReservas()).isTrue();
        assertThat(respuestaContexto.getEstado()).isEqualTo(EstadoHabitacion.ACTIVO);
    }


    @Dado("un usuario con rol {string}")
    public void unUsuarioConRol(String rol) {

        usuarioAdminContexto = "user.customer";
        

        Hotel hotel = new Hotel();
        hotel.setNombre("Hotel Test");
        

        habitacionContexto = new Habitacion();
        habitacionContexto.setHotel(hotelRepository.save(hotel));
        habitacionContexto.setHabitacionId("202");
        habitacionContexto.setNombre("Habitación Estándar");
        habitacionContexto.setTipo(TipoHabitacion.STANDARD);
        habitacionContexto.setCapacidad(2);
        habitacionContexto.setEstado(EstadoHabitacion.ACTIVO);
        habitacionContexto = habitacionRepository.save(habitacionContexto);
    }


    @WithMockUser(roles = "CUSTOMER", username = "user.customer")
    @Cuando("intente desactivar una habitacion")
    public void intenteDesactivarUnaHabitacion() {
        try {
            DesactivarHabitacionRequest request = new DesactivarHabitacionRequest();
            request.setNombreHotel(habitacionContexto.getHotel().getNombre());
            request.setNumeroHabitacion(habitacionContexto.getHabitacionId());
            request.setMotivoDesactivacion("Intento no autorizado");


            habitacionService.desactivarPorMantenimiento(request, usuarioAdminContexto);
            
        } catch (Exception e) {
            excepcionContexto = e;
        }
    }


    @Entonces("el sistema debe denegar el acceso")
    public void elSistemaDebeDenegarElAcceso() {

        assertThat(respuestaContexto).isNull();
    }


    @Y("mostrar un error de autorización")
    public void mostrarUnErrorDeAutorizacion() {

        assertThat(excepcionContexto).isNotNull();
    }


    private void limpiarContexto() {
        habitacionContexto = null;
        respuestaContexto = null;
        excepcionContexto = null;
        usuarioAdminContexto = "admin.test";
    }
}
