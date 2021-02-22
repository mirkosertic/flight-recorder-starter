package de.mirkosertic.flightrecorderstarter.configuration;

import de.mirkosertic.flightrecorderstarter.actuator.FlightRecorderEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class WebMvcFlightRecorderConfigurationTest {

    private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FlightRecorderAutoConfiguration.class));

    @Test
    void givenServletAutoconfiguration_whenContextStarts_thenAutoconfigurationAndBeansAreLoaded() {
        this.webContextRunner
                .run((context) -> assertThat(context)
                        .hasSingleBean(WebMvcFlightRecorderConfiguration.class)
                        .hasSingleBean(FlightRecorderEndpoint.class));
    }

}
