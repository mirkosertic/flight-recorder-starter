package de.mirkosertic.flightrecorderstarter.configuration;

import de.mirkosertic.flightrecorderstarter.core.FlightRecorder;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.boot.test.context.runner.ReactiveWebApplicationContextRunner;
import org.springframework.boot.test.context.runner.WebApplicationContextRunner;

import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;


class FlightRecorderAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FlightRecorderAutoConfiguration.class));

    private final WebApplicationContextRunner webContextRunner = new WebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FlightRecorderAutoConfiguration.class));

    private final ReactiveWebApplicationContextRunner reactiveContextRunner = new ReactiveWebApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(FlightRecorderAutoConfiguration.class));


    @Test
    void givenActivationPropertyFalse_whenNoWebContextStarts_thenMainAutoconfigurationIsNotLoaded() {
        this.contextRunner
                .withPropertyValues("flightrecorder.enabled=false")
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(FlightRecorderAutoConfiguration.class));
    }

    @Test
    void givenActivationPropertyNotInformed_whenNoWebContextStarts_thenMainAutoconfigurationIsNotLoaded() {
        this.contextRunner
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(FlightRecorderAutoConfiguration.class)
                        .doesNotHaveBean(FlightRecorderDynamicConfiguration.class));
    }

    @Test
    void givenActivationPropertyTrue_whenNoWebContextStarts_thenMainAutoconfigurationIsNotLoaded() {
        this.contextRunner
                .withPropertyValues("flightrecorder.enabled=true")
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(FlightRecorderAutoConfiguration.class)
                        .doesNotHaveBean(FlightRecorderDynamicConfiguration.class));
    }

    @Test
    void givenActivationPropertyFalse_whenWebContextStarts_thenMainAutoconfigurationIsNotLoaded() {
        this.webContextRunner
                .withPropertyValues("flightrecorder.enabled=false")
                .run((context) -> assertThat(context)
                        .doesNotHaveBean(FlightRecorderAutoConfiguration.class));
    }

    @Test
    void givenActivationPropertyNotInformed_whenWebContextStarts_thenMainAutoconfigurationIsLoaded() {
        this.webContextRunner
                .run((context) -> assertThat(context)
                        .hasSingleBean(FlightRecorderAutoConfiguration.class)
                        .hasSingleBean(FlightRecorderDynamicConfiguration.class)
                        .hasSingleBean(FlightRecorder.class));
    }

    @Test
    void givenActivationPropertyTrue_whenWebContextStarts_thenMainAutoconfigurationIsLoaded() {
        this.webContextRunner
                .withPropertyValues("flightrecorder.enabled=true")
                .run((context) -> assertThat(context)
                        .hasSingleBean(FlightRecorderAutoConfiguration.class)
                        .hasSingleBean(FlightRecorder.class)
                        .hasSingleBean(FlightRecorderDynamicConfiguration.class));
    }

    @Test
    void givenConfigurationProperties_whenWebContextStarts_thenPropertiesArePopulated() {
        this.webContextRunner
                .withPropertyValues("flightrecorder.enabled=true",
                        "flightrecorder.old-recordings-TTL=40")
                .run((context) -> {
                    assertThat(context)
                            .hasSingleBean(FlightRecorderAutoConfiguration.class)
                            .hasSingleBean(FlightRecorder.class)
                            .hasSingleBean(FlightRecorderDynamicConfiguration.class);
                    // Custom value
                    assertThat(context
                            .getBean(FlightRecorderDynamicConfiguration.class)
                            .getOldRecordingsTTL()).isEqualTo(40L);
                    // Default value
                    assertThat(context
                            .getBean(FlightRecorderDynamicConfiguration.class)
                            .getOldRecordingsTTLTimeUnit()).isEqualTo(ChronoUnit.HOURS);
                });
    }

    @Test
    void givenServletWebApplication_whenWebContextStarts_thenWebMvcConfigurationIsRegistered() {
        this.webContextRunner
                .run((context) -> {
                    assertThat(context)
                            .hasSingleBean(FlightRecorderAutoConfiguration.class)
                            .hasSingleBean(FlightRecorder.class)
                            .hasSingleBean(WebMvcFlightRecorderConfiguration.class);
                });
    }

    @Test
    void givenWebFluxWebApplication_whenWebContextStarts_thenWebFluxConfigurationIsRegistered() {
        this.reactiveContextRunner
                .run((context) -> {
                    assertThat(context)
                            .hasSingleBean(FlightRecorderAutoConfiguration.class)
                            .hasSingleBean(FlightRecorder.class)
                            .hasSingleBean(WebFluxFlightRecorderConfiguration.class);
                });
    }

}
