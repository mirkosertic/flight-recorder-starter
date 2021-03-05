package de.mirkosertic.flightrecorderstarter.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController;
import de.mirkosertic.flightrecorderstarter.core.FlightRecorder;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;

@ManagementContextConfiguration
@AutoConfigureAfter(FlightRecorderAutoConfiguration.class)
public class ManagementConfiguration {

    @Bean
    FlightRecorderStaticController flightRecorderStaticController(final ApplicationContext applicationContext, final FlightRecorder flightRecorder, final ObjectMapper mapper) {
        return new FlightRecorderStaticController(applicationContext, flightRecorder, mapper);
    }
}
