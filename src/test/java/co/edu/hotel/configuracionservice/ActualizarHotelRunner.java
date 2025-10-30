package co.edu.hotel.configuracionservice;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.*;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/actualizarhotel/ActualizarHotel.feature")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "co.edu.hotel.configuracionservice.bdd.actualizarhotel")
@ConfigurationParameter(key = PLUGIN_PROPERTY_NAME, value = "pretty, summary, html:target/cucumber-reports/actualizar-hotel-report.html, json:target/cucumber-reports/actualizar-hotel-report.json")
@ConfigurationParameter(key = FILTER_TAGS_PROPERTY_NAME, value = "@ActualizarHotel")
public class ActualizarHotelRunner {
}
