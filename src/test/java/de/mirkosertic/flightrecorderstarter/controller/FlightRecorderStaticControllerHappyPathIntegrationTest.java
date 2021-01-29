package de.mirkosertic.flightrecorderstarter.controller;

import de.mirkosertic.flightrecorderstarter.FlightRecorderStarterApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController.D3_V4_MIN_JS;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FlightRecorderStarterApplication.class)
@AutoConfigureMockMvc
class FlightRecorderStaticControllerHappyPathIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void givenDefaultConfiguration_whenD3V4MinJSIsRequired_thenFileIsReturned() throws Exception {
        //given empty

        //when and then
        this.mockMvc.perform(get("/actuator/flightrecorder/ui" + D3_V4_MIN_JS))
                .andExpect(status().isOk());
    }


}