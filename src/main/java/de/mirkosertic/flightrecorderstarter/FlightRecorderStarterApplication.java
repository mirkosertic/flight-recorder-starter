package de.mirkosertic.flightrecorderstarter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class FlightRecorderStarterApplication {

	public static void main(String[] args) {
		FlightRecorder.recordFor(10, "test.jfr");
		SpringApplication.run(FlightRecorderStarterApplication.class, args);
	}

}
