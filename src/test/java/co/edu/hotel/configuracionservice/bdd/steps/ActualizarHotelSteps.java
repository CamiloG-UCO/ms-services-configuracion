package co.edu.hotel.configuracionservice.bdd.steps;

import co.edu.hotel.configuracionservice.MsServicesConfiguracionApplication;
import co.edu.hotel.configuracionservice.domain.hotel.Hotel;
import co.edu.hotel.configuracionservice.repository.habitacion.IHabitacionRepository;
import co.edu.hotel.configuracionservice.repository.hotel.IHotelRepository;
import co.edu.hotel.configuracionservice.services.hotel.HotelService;
import com.fasterxml.jackson.databind.ObjectMapper; // Necesitarás esto para clonar
import io.cucumber.java.Before;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.cucumber.spring.CucumberContextConfiguration;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;


import java.util.UUID;

// --- 1. CONFIGURACIÓN DE PRUEBA DE H2 (COPIADA DE TU COMPAÑERO) ---
 // ¡Limpia la base de datos después de cada escenario!
public class ActualizarHotelSteps {

    // --- 2. INYECCIONES REALES (NO MOCKS) ---
    // Ya no usamos MockMvc ni @MockBean
    private final HotelService hotelService;
    private final IHotelRepository hotelRepository;
    private final ObjectMapper objectMapper;
    private final IHabitacionRepository habitacionRepository;

    // --- 3. ESTADO DEL ESCENARIO ---
    private Hotel hotelExistente;
    private Hotel hotelRequestData; // Los datos que enviaremos al servicio
    private UUID hotelId;
    private Exception exception; // Para capturar errores de validación

    @Autowired
    public ActualizarHotelSteps(HotelService hotelService, IHotelRepository hotelRepository, ObjectMapper objectMapper, IHabitacionRepository habitacionRepository) {
        this.hotelService = hotelService;
        this.hotelRepository = hotelRepository;
        this.objectMapper = objectMapper;
        this.habitacionRepository = habitacionRepository;
    }

    @Before
    public void limpiarContexto() {

        habitacionRepository.deleteAll();

        hotelRepository.deleteAll();

        hotelExistente = null;
        hotelRequestData = null;
        hotelId = null;
        exception = null;
    }


    @Given("there is a hotel named {string}, address {string}, city {string}, phone {string}, and description {string}")
    public void hotelExists(String name, String address, String city, String phone, String description) {
        // 1. Crear y GUARDAR el hotel en la H2 real
        hotelExistente = new Hotel();
        hotelExistente.setNombre(name);
        hotelExistente.setDireccion(address);
        hotelExistente.setCiudad(city);
        hotelExistente.setTelefono(phone);
        hotelExistente.setDescripcion(description);
        hotelExistente.setHotelCodigo("HTL-001"); // Rellenar campos necesarios

        // Guardamos y obtenemos la entidad persistida (con su ID)
        hotelExistente = hotelRepository.save(hotelExistente);
        hotelId = hotelExistente.getId();

        // 2. Preparar el objeto "request" que usaremos en el 'When'
        hotelRequestData = objectMapper.convertValue(hotelExistente, Hotel.class);
    }

    @Given("there is no hotel named {string}")
    public void hotelDoesNotExist(String name) {
        // 1. No guardamos nada en la H2. Está vacía.
        // 2. Asignamos un ID falso
        hotelId = UUID.randomUUID();

        // 3. Preparar el objeto "request"
        hotelRequestData = new Hotel();
        hotelRequestData.setNombre(name);
        hotelRequestData.setTelefono("3010001111"); // Teléfono válido para pasar la validación
        hotelRequestData.setDescripcion("Updated description");
    }


    @When("the admin updates the phone to {string} and the description to {string}")
    public void updatePhoneAndDescription(String phone, String description) {
        hotelRequestData.setTelefono(phone);
        hotelRequestData.setDescripcion(description);
        performServiceCall();
    }

    @When("the admin updates the address to {string}")
    public void updateAddress(String address) {
        hotelRequestData.setDireccion(address);
        performServiceCall();
    }

    @When("the admin updates the name to {string}")
    public void updateName(String name) {
        hotelRequestData.setNombre(name);
        performServiceCall();
    }

    @When("the admin updates the name to {string}, address to {string}, city to {string}, phone to {string}, and description to {string}")
    public void updateAllFields(String name, String address, String city, String phone, String description) {
        hotelRequestData.setNombre(name);
        hotelRequestData.setDireccion(address);
        hotelRequestData.setCiudad(city);
        hotelRequestData.setTelefono(phone);
        hotelRequestData.setDescripcion(description);
        performServiceCall();
    }

    @When("the admin updates the phone to {string} and the description to null")
    public void updatePhoneAndNullDescription(String phone) {
        hotelRequestData.setTelefono(phone);
        hotelRequestData.setDescripcion(null);
        performServiceCall();
    }


    private void performServiceCall() {
        try {
            hotelService.actualizarHotel(hotelId, hotelRequestData);
        } catch (Exception e) {
            this.exception = e;
        }
    }


    @Then("the system should save the changes")
    public void verifyChangesSaved() {
        // 1. Verificar que no hubo ninguna excepción
        Assertions.assertNull(exception, "Se lanzó una excepción inesperada: " + (exception != null ? exception.getMessage() : ""));

        // 2. Consultar la base de datos H2 para verificar el cambio
        Hotel hotelPersistido = hotelRepository.findById(hotelId)
                .orElseThrow(() -> new AssertionError("El hotel no se guardó en la H2."));

        // 3. Comparar que datos guardados son los que enviamos
        Assertions.assertEquals(hotelRequestData.getTelefono(), hotelPersistido.getTelefono());
        Assertions.assertEquals(hotelRequestData.getDescripcion(), hotelPersistido.getDescripcion());
        Assertions.assertEquals(hotelRequestData.getNombre(), hotelPersistido.getNombre());
    }

    @And("display the message {string}")
    public void verifySuccessMessage(String expected) {
        Assertions.assertNull(exception, "La operación falló, no se pudo mostrar el mensaje de éxito.");
    }

    @Then("the system should display the error message {string}")
    public void verifyErrorMessage(String expected) {
        Assertions.assertNotNull(exception, "Se esperaba un error, pero no se lanzó ninguna excepción.");

        Assertions.assertTrue(
                exception.getMessage().contains(expected),
                "El mensaje de error fue: '" + exception.getMessage() + "', pero se esperaba que contuviera: '" + expected + "'"
        );
    }
}