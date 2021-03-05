package de.mirkosertic.flightrecorderstarter.configuration;

import de.mirkosertic.flightrecorderstarter.actuator.FlightRecorderEndpoint;
import de.mirkosertic.flightrecorderstarter.core.FlightRecorder;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication.Type;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


@Configuration
@ConditionalOnWebApplication(type = Type.SERVLET)
public class WebMvcFlightRecorderConfiguration {

    @Bean
    public FlightRecorderEndpoint flightRecorderEndpoint(final FlightRecorder flightRecorder) {
        return new FlightRecorderEndpoint(flightRecorder);
    }

}
