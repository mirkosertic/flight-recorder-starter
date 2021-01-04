package de.mirkosertic.flightrecorderstarter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import de.mirkosertic.flightrecorderstarter.fixtures.FlightRecorderStarterApplication;
import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController.D3_V4_MIN_JS;

@SpringBootTest(classes = FlightRecorderStarterApplication.class, properties = {
    "management.endpoints.web.base-path=/customActuator"})
@AutoConfigureMockMvc
class FRStaticControllerCustomManagementContextPathIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenCustomConfiguration_whenD3V4MinJSIsRequired_thenFileIsReturned() throws Exception {
        //given empty

        //when and then
        this.mockMvc.perform(get("/customActuator/flightrecorder/static" + D3_V4_MIN_JS))
            .andExpect(status().isOk());
    }


}