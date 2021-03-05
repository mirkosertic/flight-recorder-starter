package de.mirkosertic.flightrecorderstarter.configuration;

import de.mirkosertic.flightrecorderstarter.actuator.ReactiveFlightRecorderEndpoint;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class WebFluxFlightRecorderConfigurationTest {

    private final ReactiveWebApplicationContextRunner webContextRunner = new ReactiveWebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FlightRecorderAutoConfiguration.class));

    @Test
    void givenReactiveAutoconfiguration_whenContextStarts_thenAutoconfigurationAndBeansAreLoaded() {
        this.webContextRunner
                .run((context) -> assertThat(context)
                        .hasSingleBean(WebFluxFlightRecorderConfiguration.class)
                        .hasSingleBean(ReactiveFlightRecorderEndpoint.class));
    }

}
