package de.mirkosertic.flightrecorderstarter.configuration;


import de.mirkosertic.flightrecorderstarter.trigger.MicrometerAdapter;
import de.mirkosertic.flightrecorderstarter.trigger.TriggerChecker;
import org.junit.jupiter.api.Test;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.metrics.MetricsAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class TriggerConfigurationTest {

    @Test
    void givenMissingPropertyAndMeterRegistryPresent_whenContextLoads_thenTriggerConfigurationBeanIsLoaded() {
        //Given
        final AutoConfigurations autoConfigurations = AutoConfigurations.of(MetricsAutoConfiguration.class,
                CompositeMeterRegistryAutoConfiguration.class, FlightRecorderAutoConfiguration.class);

        //When
        new ApplicationContextRunner().withConfiguration(autoConfigurations).run(context -> {
            //Then
            assertThat(context).hasSingleBean(TriggerConfiguration.class);
            assertThat(context).hasSingleBean(MicrometerAdapter.class);
            assertThat(context).hasSingleBean(TriggerChecker.class);
        });

    }

    @Test
    void givenTruePropertyAndMeterRegistryPresent_whenContextLoads_thenTriggerConfigurationBeanIsLoaded() {
        //Given
        final AutoConfigurations autoConfigurations = AutoConfigurations.of(MetricsAutoConfiguration.class,
                CompositeMeterRegistryAutoConfiguration.class, FlightRecorderAutoConfiguration.class);
        final String[] properties = {"flightrecorder.trigger-enabled=true"};

        //When
        new ApplicationContextRunner().withConfiguration(autoConfigurations)
                .withPropertyValues(properties).run(context -> {
            //Then
            assertThat(context).hasSingleBean(TriggerConfiguration.class);
            assertThat(context).hasSingleBean(MicrometerAdapter.class);
            assertThat(context).hasSingleBean(TriggerChecker.class);
        });

    }

    @Test
    void givenPropertyValueFalseAndMeterRegistryPresent_whenContextLoads_thenTriggerConfigurationBeanIsNotLoaded() {
        //Given
        final AutoConfigurations autoConfigurations = AutoConfigurations.of(MetricsAutoConfiguration.class,
                CompositeMeterRegistryAutoConfiguration.class, FlightRecorderAutoConfiguration.class);
        final String[] properties = {"flightrecorder.trigger-enabled=false"};

        //When
        new ApplicationContextRunner().withConfiguration(autoConfigurations).withPropertyValues(properties).run(context -> {
            //Then
            assertThat(context).doesNotHaveBean(TriggerConfiguration.class);
            assertThat(context).doesNotHaveBean(MicrometerAdapter.class);
            assertThat(context).doesNotHaveBean(TriggerChecker.class);
        });

    }


    @Test
    void givenTruePropertyAndMeterRegistryNotPresent_whenContextLoads_thenTriggerConfigurationBeanIsLoaded() {
        //Given
        final AutoConfigurations autoConfigurations = AutoConfigurations.of(FlightRecorderAutoConfiguration.class);
        final String[] properties = {"flightrecorder.trigger-enabled=true"};

        //When
        new ApplicationContextRunner().withConfiguration(autoConfigurations)
                .withPropertyValues(properties).run(context -> {
            //Then
            assertThat(context).doesNotHaveBean(TriggerConfiguration.class);
            assertThat(context).doesNotHaveBean(MicrometerAdapter.class);
            assertThat(context).doesNotHaveBean(TriggerChecker.class);
        });

    }
}