package co.edu.hotel.configuracionservice.bdd.steps;

import co.edu.hotel.configuracionservice.domain.hotel.Hotel;
import co.edu.hotel.configuracionservice.repository.hotel.IHotelRepository;
import co.edu.hotel.configuracionservice.services.hotel.HotelService;
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@CucumberContextConfiguration
@SpringBootTest
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@Transactional
public class HotelStepDefinitions {

    @Autowired
    private IHotelRepository hotelRepository;

    @Autowired
    private HotelService hotelService;

    private Hotel hotel;
    private String mensajeResultado;
    private String codigoGenerado;

    @Before
    public void setUp(){
        hotelRepository.deleteAll();
    }

    @Given("los datos del nuevo hotel {string} con dirección {string}, ciudad {string} del departamento {string}, pais {string} con el numero de contacto {string} con descripcion {string}")
    public void losDatosDelNuevoHotel(String nombre, String direccion, String ciudad,
                                      String departamento, String pais, String numeroContacto,
                                      String descripcion){
        hotel = new Hotel(nombre, direccion, ciudad, departamento, pais, numeroContacto, descripcion);
    }

    @When("el administrador presione {string}")
    public void elAdministradorPresione(String accion){
        if ("Guardar".equals(accion)){
            mensajeResultado= hotelService.registrarHotel(hotel);
            codigoGenerado = hotel.getHotelCodigo();
        }
    }

    @Then("el sistema debe registrar el hotel con el código {string}")
    public void elSistemaDebeRegistrarElHotelConCodigo(String codigoEsperado){
        assertNotNull(codigoGenerado, "El código del hotel no debe ser nulo");
        assertEquals(codigoEsperado, codigoGenerado, "El código generado debe ser " + codigoEsperado);
        Hotel hotelGuardado = hotelService.obtenerHotelPorCodigo(codigoGenerado);
        assertNotNull(hotelGuardado, "El hotel debe estar guardado en el sistema");
        assertEquals("Hotel Cartagena Real", hotelGuardado.getNombre());
    }

    @And("mostrar el mensaje {string}")
    public void mostrarMensaje(String mensajeEsperado){
        assertEquals(mensajeEsperado, mensajeResultado, "El mensaje debe ser: " + mensajeEsperado);
    }
}
