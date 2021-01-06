package de.mirkosertic.flightrecorderstarter.configuration;

import de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController;
import org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration;
import org.springframework.context.annotation.Bean;

@ManagementContextConfiguration
public class ManagementConfiguration {

    @Bean
    FlightRecorderStaticController flightRecorderStaticController() {
        return new FlightRecorderStaticController();
    }
}
