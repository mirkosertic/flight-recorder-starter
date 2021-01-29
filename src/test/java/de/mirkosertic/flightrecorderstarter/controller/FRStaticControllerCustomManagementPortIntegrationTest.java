package de.mirkosertic.flightrecorderstarter.controller;

import de.mirkosertic.flightrecorderstarter.FlightRecorderStarterApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.autoconfigure.web.server.LocalManagementPort;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController.D3_V4_MIN_JS;
import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(classes = FlightRecorderStarterApplication.class, properties = {
        "management.endpoints.web.base-path=/customActuator",
        "management.server.port=0"     //Set port=0 to force random port. See ManagementServerProperties.setPort()
}, webEnvironment = WebEnvironment.RANDOM_PORT)
class FRStaticControllerCustomManagementPortIntegrationTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @LocalManagementPort
    private Long localManagementPort;

    @LocalServerPort
    private Long localServerPort;


    @Test
    void givenCustomConfiguration_whenD3V4MinJSIsRequired_thenFileIsReturned() throws Exception {
        //given base url

        final String baseUrl = "http://localhost:" + this.localManagementPort;

        //when and then
        final ResponseEntity<Resource> response = this.testRestTemplate
                .getForEntity(baseUrl + "/customActuator/flightrecorder/ui" + D3_V4_MIN_JS, Resource.class);

        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    }


}