package co.edu.hotel.configuracionservice.bdd.actualizarhotel;

import co.edu.hotel.configuracionservice.MsServicesConfiguracionApplication;
import co.edu.hotel.configuracionservice.domain.hotel.Hotel;
import co.edu.hotel.configuracionservice.repository.hotel.IHotelRepository;
import io.cucumber.java.en.*;
import org.junit.jupiter.api.Assertions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = MsServicesConfiguracionApplication.class)
public class ActualizarHotelStepDefinitions {

    @Autowired
    private IHotelRepository hotelRepository;

    private Hotel hotel;
    private String message;

    @Given("there is a hotel named {string}, address {string}, city {string}, phone {string}, and description {string}")
    public void hotelExists(String name, String address, String city, String phone, String description) {
        hotel = new Hotel();
        hotel.setNombre(name);
        hotel.setDireccion(address);
        hotel.setCiudad(city);
        hotel.setTelefono(phone);
        hotel.setDescripcion(description);
        hotelRepository.save(hotel);
    }

    @Given("there is no hotel named {string}")
    public void hotelDoesNotExist(String name) {
        hotelRepository.findByNombre(name).ifPresent(h -> hotelRepository.delete(h));
        hotel = null;
    }

    @When("the admin updates the phone to {string} and the description to {string}")
    public void updatePhoneAndDescription(String phone, String description) {
        if (hotel == null) {
            message = "Hotel not found";
            return;
        }
        if (phone == null || phone.isBlank()) {
            message = "Phone cannot be empty";
            return;
        }
        if (description == null || "null".equals(description)) {
            message = "Description cannot be null";
            return;
        }
        if (!phone.matches("\\d{7,15}")) {
            message = "Invalid phone format";
            return;
        }
        hotel.setTelefono(phone);
        hotel.setDescripcion(description);
        hotelRepository.save(hotel);
        message = "Hotel information updated successfully";
    }

    @When("the admin updates the address to {string}")
    public void updateAddress(String address) {
        if (hotel == null) {
            message = "Hotel not found";
            return;
        }
        hotel.setDireccion(address);
        hotelRepository.save(hotel);
        message = "Hotel information updated successfully";
    }

    @When("the admin updates the name to {string}")
    public void updateName(String name) {
        if (hotel == null) {
            message = "Hotel not found";
            return;
        }
        hotel.setNombre(name);
        hotelRepository.save(hotel);
        message = "Hotel information updated successfully";
    }

    @When("the admin updates the name to {string}, address to {string}, city to {string}, phone to {string}, and description to {string}")
    public void updateAllFields(String name, String address, String city, String phone, String description) {
        if (hotel == null) {
            message = "Hotel not found";
            return;
        }
        if (phone == null || phone.isBlank()) {
            message = "Phone cannot be empty";
            return;
        }
        if (!phone.matches("\\d{7,15}")) {
            message = "Invalid phone format";
            return;
        }
        if (description == null || "null".equals(description)) {
            message = "Description cannot be null";
            return;
        }
        hotel.setNombre(name);
        hotel.setDireccion(address);
        hotel.setCiudad(city);
        hotel.setTelefono(phone);
        hotel.setDescripcion(description);
        hotelRepository.save(hotel);
        message = "Hotel information updated successfully";
    }

    @Then("the system should save the changes")
    public void verifyChangesSaved() {
        Assertions.assertNotNull(hotel);
        Assertions.assertTrue(hotelRepository.existsById(hotel.getId()));
    }

    @And("display the message {string}")
    public void verifySuccessMessage(String expected) {
        Assertions.assertEquals(expected, message);
    }

    @Then("the system should display the error message {string}")
    public void verifyErrorMessage(String expected) {
        Assertions.assertEquals(expected, message);
    }
}
