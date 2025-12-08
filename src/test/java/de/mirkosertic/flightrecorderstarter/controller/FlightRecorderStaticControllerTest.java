package de.mirkosertic.flightrecorderstarter.controller;

import de.mirkosertic.flightrecorderstarter.core.FlightRecorder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.io.File;

import static de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController.*;
import static org.hamcrest.Matchers.containsString;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.TEXT_HTML;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(value = FlightRecorderStaticController.class, properties = {
        "flightrecorder.calculated-static-controller-base-path=/testStaticUrl"})
@ExtendWith(MockitoExtension.class)
class FlightRecorderStaticControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FlightRecorder flightRecorder;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private FlightRecorderStaticController flightRecorderStaticController;

    @SpringBootConfiguration
    static class ControllerConfiguration {

        @Bean
        FlightRecorderStaticController flightRecorderStaticControllerflightRecorderStaticController(final FlightRecorder mockFlightRecorder, final ApplicationContext applicationContext, final ObjectMapper objectMapper) {
            return new FlightRecorderStaticController(applicationContext, mockFlightRecorder, objectMapper);
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

    @Test
    void givenFlamegraphHtml_whenFlamegraphHtmlIsRequired_thenFileIsReturned() throws Exception {
        //given empty

        //when and then
        this.mockMvc.perform(get("/testStaticUrl" + "/1" + FLAMEGRAPH_HTML))
                .andExpect(status().isOk())
                .andExpect(header().string(CACHE_CONTROL_KEY, CACHE_CONTROL_VALUE))
                .andExpect(header().string(PRAGMA_KEY, PRAGMA_VALUE))
                .andExpect(header().string(EXPIRES_KEY, EXPIRES_VALUE))
                .andExpect(content().contentType(TEXT_HTML))
                .andExpect(content().string(containsString("data.json")));
    }

    @Test
    void givenRawFlamegraphHtml_whenRawFlamegraphHtmlIsRequired_thenFileIsReturned() throws Exception {
        //given empty

        //when and then
        this.mockMvc.perform(get("/testStaticUrl" + "/1" + RAM_FLAMEGRAPH_HTML))
                .andExpect(status().isOk())
                .andExpect(header().string(CACHE_CONTROL_KEY, CACHE_CONTROL_VALUE))
                .andExpect(header().string(PRAGMA_KEY, PRAGMA_VALUE))
                .andExpect(header().string(EXPIRES_KEY, EXPIRES_VALUE))
                .andExpect(content().contentType(TEXT_HTML))
                .andExpect(content().string(containsString("rawdata.json")));
    }

    @Test
    void givenNoExistingRecording_whenTryToDownloadDataJson_thenNotFoundIsReturned() throws Exception {
        //given empty
        given(this.flightRecorder.stopRecording(anyLong())).willReturn(null);

        //when and then
        this.mockMvc.perform(get("/testStaticUrl" + "/1" + DATA_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void givenInternalErrorInService_whenTryToDownloadDataJson_thenInternalServerErrorIsReturned() throws Exception {
        //given empty
        given(this.flightRecorder.stopRecording(anyLong())).willThrow(IllegalArgumentException.class);

        //when and then
        this.mockMvc.perform(get("/testStaticUrl" + "/1" + DATA_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void givenExistingRecording_whenTryToDownloadDataJson_thenJSONInfoIsReturned() throws Exception {
        //given
        given(this.flightRecorder.stopRecording(anyLong())).willReturn(new File(getClass().getResource("/recording.jfr").toURI()));

        //when and then

        this.mockMvc.perform(get("/testStaticUrl" + "/1" + DATA_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("de.mirkosertic.flightrecorderstarter.FlightRecorderEndpoint.startRecording")));
    }

    @Test
    void givenExistingRecordingAndNotAnnotatedSpringBootApplication_whenTryToDownloadDataJson_thenJSONInfoIsReturned() throws Exception {

        given(this.flightRecorder.stopRecording(anyLong())).willReturn(new File(getClass().getResource("/recording.jfr").toURI()));

        //when and then
        this.mockMvc.perform(get("/testStaticUrl" + "/1" + DATA_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("org.apache.tomcat.util.net.NioBlockingSelector$BlockPoller.run")));
    }

    @Test
    void givenNonExistingRecording_whenTryToDownloadDataJson_thenNotFoundResponseIsReturned() throws Exception {
        //given
        given(this.flightRecorder.stopRecording(anyLong())).willReturn(null);

        //when and then
        this.mockMvc.perform(get("/testStaticUrl" + "/1" + DATA_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenExistingRecording_whenTryToDownloadRawJson_thenJSONInfoIsReturned() throws Exception {
        //given
        given(this.flightRecorder.stopRecording(anyLong())).willReturn(new File(getClass().getResource("/recording.jfr").toURI()));

        //when and then

        this.mockMvc.perform(get("/testStaticUrl" + "/1" + RAWDATA_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("org.apache.tomcat.util.net.NioBlockingSelector$BlockPoller.run")));
    }

    @Test
    void givenNonExistingRecording_whenTryToDownloadRawJson_thenNotFoundIsReturned() throws Exception {
        //given
        given(this.flightRecorder.stopRecording(anyLong())).willReturn(null);

        //when and then

        this.mockMvc.perform(get("/testStaticUrl" + "/1" + RAWDATA_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void givenInternalErrorInService_whenTryToDownloadRawJson_thenInternalServerErrorIsReturned() throws Exception {
        //given empty
        given(this.flightRecorder.stopRecording(anyLong())).willThrow(IllegalArgumentException.class);

        //when and then
        this.mockMvc.perform(get("/testStaticUrl" + "/1" + RAWDATA_JSON))
                .andExpect(status().isInternalServerError());
    }

    @Test
    void givenApplicationContextWithoutSpringBootApplicationBean_whenTryToFindBootClass_thenNullIsReturned() {
        assertNull(this.flightRecorderStaticController.findBootClass(this.applicationContext));
    }
}