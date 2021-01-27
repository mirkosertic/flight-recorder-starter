package de.mirkosertic.flightrecorderstarter.actuator;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mirkosertic.flightrecorderstarter.core.FlightRecorder;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.servlet.ServletManagementContextAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrlPattern;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(FlightRecorderEndpoint.class)
class FlightRecorderEndpointTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private FlightRecorder mockFlightRecorder;

    @SpringBootConfiguration
    @ImportAutoConfiguration({JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class,
            EndpointAutoConfiguration.class, WebEndpointAutoConfiguration.class,
            ServletManagementContextAutoConfiguration.class,
            PropertyPlaceholderAutoConfiguration.class, WebMvcAutoConfiguration.class,
            ManagementContextAutoConfiguration.class, DispatcherServletAutoConfiguration.class})
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
        final MvcResult result = this.mockMvc.perform(post("/actuator/flightrecorder")
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
    void givenCorrectParamsAndFailureAtCore_whenTryToCreateRecording_then400ErrorCodeIsReturned() throws Exception {
        //Given
        given(this.mockFlightRecorder.startRecordingFor(any())).willThrow(IllegalArgumentException.class);

        //When
        final MvcResult result = this.mockMvc.perform(post("/actuator/flightrecorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"duration\": \"5\",\"timeUnit\":\"SECONDS\"}"))
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
        final MvcResult result = this.mockMvc.perform(post("/actuator/flightrecorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"duration\": \"5\",\"timeUnit\":\"SECONDS\"}"))
                .andExpect(status().isCreated())
                .andExpect(redirectedUrlPattern("**/actuator/flightrecorder/1"))
                .andReturn();

        //Then
        then(this.mockFlightRecorder).should().startRecordingFor(any());

    }

    //TODO include tests for all operations
}