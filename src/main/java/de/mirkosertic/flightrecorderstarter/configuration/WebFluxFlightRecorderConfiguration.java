package de.mirkosertic.flightrecorderstarter.configuration;

import de.mirkosertic.flightrecorderstarter.actuator.ReactiveFlightRecorderEndpoint;
import de.mirkosertic.flightrecorderstarter.core.FlightRecorder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnWebApplication(type = Type.REACTIVE)
public class WebFluxFlightRecorderConfiguration {

    @Bean
    public ReactiveFlightRecorderEndpoint flightRecorderEndpoint(final FlightRecorder flightRecorder) {
        return new ReactiveFlightRecorderEndpoint(flightRecorder);
    }

}
