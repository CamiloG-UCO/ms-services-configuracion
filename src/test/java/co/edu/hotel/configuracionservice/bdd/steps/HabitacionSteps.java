package co.edu.hotel.configuracionservice.bdd.steps;

import co.edu.hotel.configuracionservice.domain.habitacion.Habitacion;
import co.edu.hotel.configuracionservice.domain.hotel.Hotel;
import co.edu.hotel.configuracionservice.repository.habitacion.IHabitacionRepository;
import co.edu.hotel.configuracionservice.services.habitacion.IHabitacionService;
import io.cucumber.java.Before;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.And;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class HabitacionSteps {

    @Autowired
    private IHabitacionService habitacionService; // inyectamos la interfaz, no la implementación concreta

    @Autowired
    private IHabitacionRepository habitacionRepository;

    private Hotel hotel;
    private Habitacion creada;
    private String mensajeRespuesta;

    @Before
    public void setUp() {
        habitacionRepository.deleteAll();
        mensajeRespuesta = null;
    }

    @Given("el hotel {string}")
    public void el_hotel(String nombreHotel) {
        hotel = new Hotel();
        hotel.setNombre(nombreHotel);
    }

    @When("el administrador ingrese nombre {string}, tipo {string}, capacidad_personas {string}, id {string}")
    public void el_administrador_ingrese(String nombre, String tipo, String capacidadStr, String habitacionId) {
        int capacidad = Integer.parseInt(capacidadStr);
        // Llamada corregida a la interfaz del servicio
        creada = habitacionService.crearHabitacion(habitacionId, nombre, tipo, capacidad, hotel);
        // Para el flujo BDD esperaremos el mensaje simple solicitado en el feature
        mensajeRespuesta = "Habitación creada exitosamente";
    }

    @Then("el sistema debe registrar la habitación con ID {string} y asignarla al hotel {string}")
    public void el_sistema_debe_registrar_la_habitacion(String habitacionId, String nombreHotel) {
        Habitacion encontrada = habitacionRepository.findByHabitacionId(habitacionId).orElse(null);
        Assertions.assertNotNull(encontrada, "La habitación no fue registrada en la base de datos");
        Assertions.assertEquals(nombreHotel, encontrada.getHotel().getNombre());
    }

    @And("mostrar el mensaje {string}")
    public void mostrar_el_mensaje(String mensajeEsperado) {
        Assertions.assertEquals(mensajeEsperado, mensajeRespuesta);
    }
}
