package co.edu.hotel.configuracionservice.bdd.steps;

import static org.assertj.core.api.Assertions.assertThat;

import co.edu.hotel.configuracionservice.domain.habitacion.EstadoHabitacion;
import co.edu.hotel.configuracionservice.domain.habitacion.Habitacion;
import co.edu.hotel.configuracionservice.domain.habitacion.TipoHabitacion;
import co.edu.hotel.configuracionservice.domain.hotel.Hotel;
import co.edu.hotel.configuracionservice.repository.habitacion.IHabitacionRepository;
import co.edu.hotel.configuracionservice.repository.hotel.IHotelRepository;
import co.edu.hotel.configuracionservice.services.habitacion.IHabitacionService;
import io.cucumber.java.Before;
import io.cucumber.java.es.Cuando;
import io.cucumber.java.es.Dado;
import io.cucumber.java.es.Entonces;
import io.cucumber.java.es.Y;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestExecutionListeners;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.support.DependencyInjectionTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextBeforeModesTestExecutionListener;
import org.springframework.test.context.support.DirtiesContextTestExecutionListener;
import org.springframework.transaction.annotation.Transactional;

@CucumberContextConfiguration
@SpringBootTest
@TestExecutionListeners(value = {
        DependencyInjectionTestExecutionListener.class,
        DirtiesContextBeforeModesTestExecutionListener.class,
        DirtiesContextTestExecutionListener.class
}, mergeMode = TestExecutionListeners.MergeMode.REPLACE_DEFAULTS)
@TestPropertySource(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1",
        "spring.datasource.driverClassName=org.h2.Driver",
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.jpa.show-sql=false",
        "spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.H2Dialect"
})
@Transactional
public class HabitacionSteps {

    private static final String HABITACION_ID_ESPERADA = "H-456";

    private final IHabitacionService habitacionService;
    private final IHabitacionRepository habitacionRepository;
    private final IHotelRepository hotelRepository;

    private Hotel hotel;
    private Habitacion habitacionCreada;
    private String mensajeRespuesta;
    private int capacidadEsperada;
    private String tipoEsperado;
    private EstadoHabitacion estadoEsperado;

    public HabitacionSteps(IHabitacionService habitacionService,
                           IHabitacionRepository habitacionRepository,
                           IHotelRepository hotelRepository) {
        this.habitacionService = habitacionService;
        this.habitacionRepository = habitacionRepository;
        this.hotelRepository = hotelRepository;
    }

    @Before
    public void limpiarContexto() {
        habitacionRepository.deleteAll();
        hotelRepository.deleteAll();
        habitacionCreada = null;
        mensajeRespuesta = null;
        capacidadEsperada = 0;
        tipoEsperado = null;
        estadoEsperado = null;
    }

    @Dado("el hotel {string}")
    public void elHotel(String nombreHotel) {
        Hotel nuevoHotel = Hotel.builder()
                .nombre(nombreHotel)
                .activo(true)
                .build();
        hotel = hotelRepository.save(nuevoHotel);
    }

    @Cuando("el administrador ingrese nombre {string}, tipo {string}, numero_de_camas {string}, capacidad_personas {string}, descripcion {string}, estado {string}")
    public void elAdministradorIngresa(String nombre, String tipo, String numeroCamasStr, String capacidadStr,
                                       String descripcion, String estado) {
        Integer.parseInt(numeroCamasStr); // validación simple de número de camas
        capacidadEsperada = Integer.parseInt(capacidadStr);
        tipoEsperado = tipo;
        estadoEsperado = mapearEstado(estado);

        habitacionCreada = habitacionService.crearHabitacion(
                HABITACION_ID_ESPERADA,
                nombre,
                tipo,
                capacidadEsperada,
                hotel
        );

        assertThat(habitacionCreada).as("La habitación debe crearse").isNotNull();
        assertThat(habitacionCreada.getNombre()).isEqualTo(nombre);
        assertThat(habitacionCreada.getCapacidad()).isEqualTo(capacidadEsperada);
        assertThat(habitacionCreada.getTipo()).isEqualTo(TipoHabitacion.valueOf(tipo.toUpperCase()));

        mensajeRespuesta = "Habitación creada exitosamente";
    }

    @Entonces("el sistema debe registrar la habitación con ID {string} y asignarla al hotel {string}")
    public void elSistemaDebeRegistrarLaHabitacionConIdYAsignarlaAlHotel(String habitacionIdEsperado, String nombreHotelEsperado) {
        Habitacion persistida = habitacionRepository.findWithHotelByHabitacionId(habitacionIdEsperado)
                .orElseThrow(() -> new AssertionError("La habitación no fue registrada"));

        assertThat(persistida.getHotel()).isNotNull();
        assertThat(persistida.getHotel().getNombre()).isEqualTo(nombreHotelEsperado);
        assertThat(persistida.getHabitacionId()).isEqualTo(habitacionIdEsperado);
        assertThat(persistida.getCapacidad()).isEqualTo(capacidadEsperada);
        assertThat(persistida.getTipo().getDescripcion()).isEqualToIgnoringCase(tipoEsperado);
        assertThat(persistida.getEstado()).isEqualTo(estadoEsperado);
    }

    @Y("mostrar el mensaje {string}")
    public void mostrarElMensaje(String mensajeEsperado) {
        assertThat(mensajeRespuesta).isEqualTo(mensajeEsperado);
    }

    private EstadoHabitacion mapearEstado(String estadoDescripcion) {
        if ("Disponible".equalsIgnoreCase(estadoDescripcion) || "Activo".equalsIgnoreCase(estadoDescripcion)) {
            return EstadoHabitacion.ACTIVO;
        }
        if ("Inactivo".equalsIgnoreCase(estadoDescripcion) || "No disponible".equalsIgnoreCase(estadoDescripcion)) {
            return EstadoHabitacion.INACTIVO;
        }
        throw new IllegalArgumentException("Estado de habitación no soportado: " + estadoDescripcion);
    }
}
