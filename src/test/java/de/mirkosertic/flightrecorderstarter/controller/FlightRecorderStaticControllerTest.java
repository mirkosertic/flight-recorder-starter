package de.mirkosertic.flightrecorderstarter.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.test.web.servlet.MockMvc;

import org.junit.jupiter.api.Test;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController.CACHE_CONTROL_KEY;
import static de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController.CACHE_CONTROL_VALUE;
import static de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController.D3_FLAMEGRAPH_COLOR_MAPPER_MIN_JS;
import static de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController.D3_FLAMEGRAPH_CSS;
import static de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController.D3_FLAMEGRAPH_MIN_JS;
import static de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController.D3_FLAMEGRAPH_TOOLTIP_MIN_JS;
import static de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController.D3_V4_MIN_JS;
import static de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController.EXPIRES_KEY;
import static de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController.EXPIRES_VALUE;
import static de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController.PRAGMA_KEY;
import static de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController.PRAGMA_VALUE;
import static de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController.TEXT_CSS;
import static de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController.TEXT_JAVASCRIPT;

@WebMvcTest(value = FlightRecorderStaticController.class, properties = {
    "flightrecorder.calculated-static-controller-base-path=/testStaticUrl"})
class FlightRecorderStaticControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @SpringBootConfiguration
    static class ControllerConfiguration {

        @Bean
        FlightRecorderStaticController flightRecorderStaticController() {
            return new FlightRecorderStaticController();
        }
    }

    @Test
    void givenStaticFiles_whenD3V4MinJSIsRequired_thenFileIsReturned() throws Exception {
        //given empty

        //when and then
        this.mockMvc.perform(get("/testStaticUrl" + D3_V4_MIN_JS))
            .andExpect(status().isOk())
            .andExpect(header().string(CACHE_CONTROL_KEY, CACHE_CONTROL_VALUE))
            .andExpect(header().string(PRAGMA_KEY, PRAGMA_VALUE))
            .andExpect(header().string(EXPIRES_KEY, EXPIRES_VALUE))
            .andExpect(content().contentType(TEXT_JAVASCRIPT));
    }

    @Test
    void givenStaticFiles_whenD3FlamegraphMinJSIsRequired_thenFileIsReturned() throws Exception {
        //given empty

        //when and then
        this.mockMvc.perform(get("/testStaticUrl" + D3_FLAMEGRAPH_MIN_JS))
            .andExpect(status().isOk())
            .andExpect(header().string(CACHE_CONTROL_KEY, CACHE_CONTROL_VALUE))
            .andExpect(header().string(PRAGMA_KEY, PRAGMA_VALUE))
            .andExpect(header().string(EXPIRES_KEY, EXPIRES_VALUE))
            .andExpect(content().contentType(TEXT_JAVASCRIPT));
    }

    @Test
    void givenStaticFiles_whenD3FlamegraphColorJSIsRequired_thenFileIsReturned() throws Exception {
        //given empty

        //when and then
        this.mockMvc.perform(get("/testStaticUrl" + D3_FLAMEGRAPH_COLOR_MAPPER_MIN_JS))
            .andExpect(status().isOk())
            .andExpect(header().string(CACHE_CONTROL_KEY, CACHE_CONTROL_VALUE))
            .andExpect(header().string(PRAGMA_KEY, PRAGMA_VALUE))
            .andExpect(header().string(EXPIRES_KEY, EXPIRES_VALUE))
            .andExpect(content().contentType(TEXT_JAVASCRIPT));
    }

    @Test
    void givenStaticFiles_whenD3TooltipJSIsRequired_thenFileIsReturned() throws Exception {
        //given empty

        //when and then
        this.mockMvc.perform(get("/testStaticUrl" + D3_FLAMEGRAPH_TOOLTIP_MIN_JS))
            .andExpect(status().isOk())
            .andExpect(header().string(CACHE_CONTROL_KEY, CACHE_CONTROL_VALUE))
            .andExpect(header().string(PRAGMA_KEY, PRAGMA_VALUE))
            .andExpect(header().string(EXPIRES_KEY, EXPIRES_VALUE))
            .andExpect(content().contentType(TEXT_JAVASCRIPT));
    }

    @Test
    void givenStaticFiles_whenD3FlamegraphCssJSIsRequired_thenFileIsReturned() throws Exception {
        //given empty

        //when and then
        this.mockMvc.perform(get("/testStaticUrl" + D3_FLAMEGRAPH_CSS))
            .andExpect(status().isOk())
            .andExpect(header().string(CACHE_CONTROL_KEY, CACHE_CONTROL_VALUE))
            .andExpect(header().string(PRAGMA_KEY, PRAGMA_VALUE))
            .andExpect(header().string(EXPIRES_KEY, EXPIRES_VALUE))
            .andExpect(content().contentType(TEXT_CSS));
    }


}