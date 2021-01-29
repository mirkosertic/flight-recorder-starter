package de.mirkosertic.flightrecorderstarter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mirkosertic.flightrecorderstarter.FlightRecorderStarterApplication;
import de.mirkosertic.flightrecorderstarter.core.FlightRecorder;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.GenericApplicationContext;
import org.springframework.test.web.servlet.MockMvc;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static de.mirkosertic.flightrecorderstarter.controller.FlightRecorderStaticController.*;
import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
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

    @Autowired
    private FlightRecorder flightRecorder;

    @Autowired
    @Qualifier("mockAppContext")
    private ApplicationContext mockAppContext;

    @SpringBootConfiguration
    static class ControllerConfiguration {

        @MockBean
        private FlightRecorder mockFlightRecorder;

//        @MockBean
//        @Qualifier("mockAppContext")
//        private ApplicationContext mockAppContext;


        @Bean
        FlightRecorderStaticController flightRecorderStaticController() {
            return new FlightRecorderStaticController(mockAppContext(), this.mockFlightRecorder, objectMapper());
        }

        @Bean
        ObjectMapper objectMapper() {
            return new ObjectMapper();
        }

        @Bean
        ApplicationContext mockAppContext() {
            return new GenericApplicationContext();
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
        this.mockMvc.perform(get("/testStaticUrl" + FLAMEGRAPH_HTML))
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
        this.mockMvc.perform(get("/testStaticUrl" + RAM_FLAMEGRAPH_HTML))
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
        given(this.flightRecorder.stopRecording(any())).willReturn(null);

        //when and then
        this.mockMvc.perform(get("/testStaticUrl" + "/1" + DATA_JSON))
                .andExpect(status().isNotFound());

    }

    @Test
    void givenInternalErrorInService_whenTryToDownloadDataJson_thenInternalServerErrorIsReturned() throws Exception {
        //given empty
        given(this.flightRecorder.stopRecording(any())).willThrow(IllegalArgumentException.class);

        //when and then
        this.mockMvc.perform(get("/testStaticUrl" + "/1" + DATA_JSON))
                .andExpect(status().isInternalServerError());
    }


    @Test
    void givenExistingRecording_whenTryToDownloadDataJson_thenJSONInfoIsReturned() throws Exception {
        //given empty
        final Map<String, Object> mockMap = new HashMap();
        mockMap.put("beanName", new FlightRecorderStarterApplication());

        given(this.mockAppContext.getBeansWithAnnotation(SpringBootApplication.class)).willReturn(mockMap);
        given(this.flightRecorder.stopRecording(any())).willReturn(new File(getClass().getResource("/recording.jfr").toURI()));

        //TODO pendiente modificar contexto de spring para inyectar beans
        
        //when and then
        this.mockMvc.perform(get("/testStaticUrl" + "/1" + DATA_JSON))
                .andExpect(status().isOk()).andDo(r -> System.out.println(r.getResponse().getContentAsString()));
    }


    //TODO include test for findBootClass method


}