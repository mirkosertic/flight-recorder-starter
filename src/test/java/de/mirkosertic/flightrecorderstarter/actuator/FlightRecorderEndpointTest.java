package de.mirkosertic.flightrecorderstarter.actuator;

import de.mirkosertic.flightrecorderstarter.actuator.model.FlightRecorderPublicSession;
import de.mirkosertic.flightrecorderstarter.core.FlightRecorder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.http.converter.autoconfigure.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.jackson.autoconfigure.JacksonAutoConfiguration;
import org.springframework.boot.servlet.autoconfigure.actuate.web.ServletManagementContextAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.DispatcherServletAutoConfiguration;
import org.springframework.boot.webmvc.autoconfigure.WebMvcAutoConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import tools.jackson.databind.ObjectMapper;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(FlightRecorderEndpoint.class)
class FlightRecorderEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private FlightRecorder mockFlightRecorder;

    @SpringBootConfiguration
    @ImportAutoConfiguration({JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class,
            EndpointAutoConfiguration.class, WebEndpointAutoConfiguration.class,
            ServletManagementContextAutoConfiguration.class,
            PropertyPlaceholderAutoConfiguration.class, WebMvcAutoConfiguration.class,
            ManagementContextAutoConfiguration.class, DispatcherServletAutoConfiguration.class})
    @PropertySource("classpath:flight-recorder.properties")
    static class TestConfiguration {


        @Bean
        Object objectMapper() {
            return new ObjectMapper();
        }

        @Bean
        FlightRecorderEndpoint flightRecorderEndpoint(final FlightRecorder flightRecorder) {
            return new FlightRecorderEndpoint(flightRecorder);
        }
    }


    @Test
    void givenDurationIncorrectParam_whenTryToCreateRecording_then400ErrorCodeIsReturned() throws Exception {
        //Given
        given(this.mockFlightRecorder.startRecordingFor(any())).willReturn(1L);

        //When
        this.mockMvc.perform(post("/actuator/flightrecorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"timeUnit\": \"SECONDS\"}"))
                .andExpect(status().isBadRequest())
                .andReturn();

        //Then
        then(this.mockFlightRecorder).should(never()).startRecordingFor(any());
    }

    @Test
    void givenTimeUnitIncorrectParam_whenTryToCreateRecording_then400ErrorCodeIsReturned() throws Exception {
        //Given
        given(this.mockFlightRecorder.startRecordingFor(any())).willReturn(1L);

        //When
        this.mockMvc.perform(post("/actuator/flightrecorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"duration\": \"5\"}"))
                .andExpect(status().isBadRequest())
                .andReturn();

        //Then
        then(this.mockFlightRecorder).should(never()).startRecordingFor(any());
    }

    @Test
    void givenCorrectParamsAndFailureAtCore_whenTryToCreateRecording_then500ErrorCodeIsReturned() throws Exception {
        //Given
        given(this.mockFlightRecorder.startRecordingFor(any())).willThrow(IllegalArgumentException.class);

        //When
        this.mockMvc.perform(post("/actuator/flightrecorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"duration\": \"5\",\"timeUnit\":\"Seconds\"}"))
                .andExpect(status().isInternalServerError())
                .andReturn();

        //Then
        then(this.mockFlightRecorder).should().startRecordingFor(any());
    }

    @Test
    void givenCorrectParams_whenTryToCreateRecording_thenRecordingIdIsReturned() throws Exception {
        //Given
        given(this.mockFlightRecorder.startRecordingFor(any())).willReturn(1L);

        //When
        this.mockMvc.perform(post("/actuator/flightrecorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"duration\": \"5\",\"timeUnit\":\"Seconds\"}"))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("**/actuator/flightrecorder/1"))
                .andReturn();

        //Then
        then(this.mockFlightRecorder).should().startRecordingFor(any());

    }

    @Test
    void givenCorrectRequest_whenTryToRetrieveAllRecordings_thenAllRecordingsAreReturned() throws Exception {
        //given two flightRecorderPublicSession
        final FlightRecorderPublicSession flightRecorderPublicSession1 = new FlightRecorderPublicSession();
        flightRecorderPublicSession1.setId(1L);
        flightRecorderPublicSession1.setStartedAt(LocalDateTime.now());
        flightRecorderPublicSession1.setStatus("status");
        flightRecorderPublicSession1.setFinishedAt(LocalDateTime.now());
        flightRecorderPublicSession1.setDescription("description");

        final FlightRecorderPublicSession flightRecorderPublicSession2 = new FlightRecorderPublicSession();
        flightRecorderPublicSession2.setId(2L);
        flightRecorderPublicSession2.setStartedAt(LocalDateTime.now());
        flightRecorderPublicSession2.setStatus("status");
        flightRecorderPublicSession2.setFinishedAt(LocalDateTime.now());
        flightRecorderPublicSession2.setDescription("description");

        final List<FlightRecorderPublicSession> flightRecorderPublicSessions = Arrays.asList(flightRecorderPublicSession1, flightRecorderPublicSession2);

        given(this.mockFlightRecorder.sessions()).willReturn(flightRecorderPublicSessions);

        //when
        this.mockMvc.perform(get("/actuator/flightrecorder"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().string(containsString("\"id\":1")))
                .andExpect(content().string(containsString("\"id\":2")))
                .andReturn();

        //then
        then(this.mockFlightRecorder).should().sessions();
    }

    @Test
    void givenRequest_whenTryToRetrieveAllRecordings_thenInternalServerErrorReturned() throws Exception {
        //given
        given(this.mockFlightRecorder.sessions()).willThrow(IllegalArgumentException.class);

        //when
        this.mockMvc.perform(get("/actuator/flightrecorder"))
                .andExpect(status().isInternalServerError())
                .andReturn();

        //then
        then(this.mockFlightRecorder).should().sessions();
    }

    @Test
    void givenCorrectParams_whenTryToStopRecording_thenRecordingIdIsReturned() throws Exception {
        //given
        given(this.mockFlightRecorder.stopRecording(anyLong())).willReturn(new File(getClass().getResource("/recording.jfr").toURI()));

        //when
        this.mockMvc.perform(put("/actuator/flightrecorder/1"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andReturn();

        //then
        then(this.mockFlightRecorder).should().stopRecording(anyLong());
    }

    @Test
    void givenNoExistingRecordingId_whenTryToStopRecording_thenNotFoundIsReturned() throws Exception {
        //given
        given(this.mockFlightRecorder.stopRecording(anyLong())).willReturn(null);

        //when
        this.mockMvc.perform(put("/actuator/flightrecorder/1"))
                .andExpect(status().isNotFound())
                .andReturn();

        //then
        then(this.mockFlightRecorder).should().stopRecording(anyLong());
    }

    @Test
    void givenCorrectParams_whenTryToDeleteARecording_thenNoContentIsReturned() throws Exception {
        //given
        given(this.mockFlightRecorder.stopRecording(anyLong())).willReturn(new File(getClass().getResource("/recording.jfr").toURI()));

        //when
        this.mockMvc.perform(delete("/actuator/flightrecorder/1"))
                .andExpect(status().isNoContent())
                .andReturn();

        //then
        then(this.mockFlightRecorder).should().stopRecording(anyLong());
        then(this.mockFlightRecorder).should().deleteRecording(anyLong());
    }

    @Test
    void givenNoExistingRecordId_whenTryToDeleteARecording_thenNotFoundIsReturned() throws Exception {
        //given
        given(this.mockFlightRecorder.stopRecording(anyLong())).willReturn(null);

        //when
        this.mockMvc.perform(delete("/actuator/flightrecorder/1"))
                .andExpect(status().isNotFound())
                .andReturn();

        //then
        then(this.mockFlightRecorder).should().stopRecording(anyLong());
        then(this.mockFlightRecorder).should(never()).deleteRecording(anyLong());

    }

    @Test
    void givenCorrectParams_whenTryToDeleteARecording_thenInternalServerErrorIsReturned() throws Exception {
        //given
        given(this.mockFlightRecorder.stopRecording(anyLong())).willThrow(IllegalArgumentException.class);

        //when
        this.mockMvc.perform(delete("/actuator/flightrecorder/1"))
                .andExpect(status().isInternalServerError())
                .andReturn();

        //then
        then(this.mockFlightRecorder).should().stopRecording(anyLong());
        //then(this.mockFlightRecorder).should(never()).deleteRecording(anyLong());
    }

    @Test
    void givenCorrectParams_whenTryToDownloadRecording_thenRecordingIsReturned() throws Exception {
        //given
        given(this.mockFlightRecorder.stopRecording(anyLong())).willReturn(new File(getClass().getResource("/recording.jfr").toURI()));

        //when
        this.mockMvc.perform(get("/actuator/flightrecorder/1"))
                .andExpect(header().string("Cache-Control", "no-cache, no-store, must-revalidate"))
                .andExpect(header().string("Pragma", "no-cache"))
                .andExpect(header().string("Expires", "0"))
                .andExpect(content().contentType(MediaType.APPLICATION_OCTET_STREAM))
                .andExpect(status().isOk())
                .andReturn();

        //then
        then(this.mockFlightRecorder).should().stopRecording(anyLong());
    }

    @Test
    void givenNoExistingRecordId_whenTryToDownloadRecording_thenNotFoundIsReturned() throws Exception {
        //given
        given(this.mockFlightRecorder.stopRecording(anyLong())).willReturn(null);

        //when
        this.mockMvc.perform(get("/actuator/flightrecorder/1"))
                .andExpect(status().isNotFound())
                .andReturn();

        //then
        then(this.mockFlightRecorder).should().stopRecording(anyLong());
    }


}