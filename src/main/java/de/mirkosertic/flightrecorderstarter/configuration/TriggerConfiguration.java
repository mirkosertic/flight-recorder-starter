package de.mirkosertic.flightrecorderstarter.configuration;

import de.mirkosertic.flightrecorderstarter.FlightRecorder;
import de.mirkosertic.flightrecorderstarter.trigger.MicrometerAdapter;
import de.mirkosertic.flightrecorderstarter.trigger.TriggerChecker;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.actuate.autoconfigure.metrics.CompositeMeterRegistryAutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(value = "flightrecorder.trigger-enabled", matchIfMissing = true)
@ConditionalOnBean(MeterRegistry.class)
@AutoConfigureAfter(value = {FlightRecorderAutoConfiguration.class, CompositeMeterRegistryAutoConfiguration.class})
public class TriggerConfiguration {
    @Bean
    public MicrometerAdapter micrometerAdapter(final MeterRegistry meterRegistry) {
        return new MicrometerAdapter(meterRegistry);
    }

    @Bean
    public TriggerChecker triggerChecker(final BeanFactory beanFactory,
                                         final FlightRecorderDynamicConfiguration dynamicConfiguration,
                                         final FlightRecorder flightRecorder,
                                         final MicrometerAdapter micrometerAdapter) {
        return new TriggerChecker(beanFactory, dynamicConfiguration, flightRecorder, micrometerAdapter);
    }
}
