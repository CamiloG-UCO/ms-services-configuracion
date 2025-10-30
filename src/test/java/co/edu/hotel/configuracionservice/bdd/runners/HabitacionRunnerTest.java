package co.edu.hotel.configuracionservice.bdd.runners;

import io.cucumber.junit.platform.engine.Constants;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/habitacion/crear_habitacion.feature")
@ConfigurationParameter(key = Constants.GLUE_PROPERTY_NAME, value = "co.edu.hotel.configuracionservice.bdd.steps")
@ConfigurationParameter(key = Constants.PLUGIN_PROPERTY_NAME, value = "pretty, html:target/cucumber-reports/habitacion/index.html, json:target/cucumber-reports/habitacion.json")
public class HabitacionRunnerTest {
}
