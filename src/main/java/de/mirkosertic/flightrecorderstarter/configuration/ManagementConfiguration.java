package de.mirkosertic.flightrecorderstarter.configuration;

import org.springframework.boot.actuate.autoconfigure.web.ManagementContextConfiguration;
import org.springframework.context.annotation.Bean;

import de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController;

@ManagementContextConfiguration
public class ManagementConfiguration {

    @Bean
    FlightRecorderStaticController flightRecorderStaticController(){
        return new FlightRecorderStaticController();
    }
}
