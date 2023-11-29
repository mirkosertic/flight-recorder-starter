package de.mirkosertic.flightrecorderstarter.actuator;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mirkosertic.flightrecorderstarter.actuator.model.FlightRecorderPublicSession;
import de.mirkosertic.flightrecorderstarter.core.FlightRecorder;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.EndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.endpoint.web.WebEndpointAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.reactive.ReactiveManagementContextAutoConfiguration;
import org.springframework.boot.actuate.autoconfigure.web.server.ManagementContextAutoConfiguration;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.context.PropertyPlaceholderAutoConfiguration;
import org.springframework.boot.autoconfigure.http.HttpMessageConvertersAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.ReactiveWebServerFactoryAutoConfiguration;
import org.springframework.boot.autoconfigure.web.reactive.WebFluxAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.io.File;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.never;

/**
 * The type Reactive flight recorder endpoint test.
 */
@WebFluxTest(ReactiveFlightRecorderEndpoint.class)
class ReactiveFlightRecorderEndpointTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private FlightRecorder mockFlightRecorder;

    /**
     * The type Test configuration.
     */
    @SpringBootConfiguration
    @ImportAutoConfiguration({JacksonAutoConfiguration.class, HttpMessageConvertersAutoConfiguration.class,
            EndpointAutoConfiguration.class, WebEndpointAutoConfiguration.class,
            ReactiveManagementContextAutoConfiguration.class,
            PropertyPlaceholderAutoConfiguration.class, WebFluxAutoConfiguration.class,
            ManagementContextAutoConfiguration.class, ReactiveWebServerFactoryAutoConfiguration.class})
    @PropertySource("classpath:flight-recorder.properties")
    static class TestConfiguration {


        /**
         * Object mapper object.
         *
         * @return the object
         */
        @Bean
        Object objectMapper() {
            return new ObjectMapper();
        }

        /**
         * Reactive flight recorder endpoint reactive flight recorder endpoint.
         *
         * @param flightRecorder the flight recorder
         * @return the reactive flight recorder endpoint
         */
        @Bean
        ReactiveFlightRecorderEndpoint reactiveFlightRecorderEndpoint(final FlightRecorder flightRecorder) {
            return new ReactiveFlightRecorderEndpoint(flightRecorder);
        }

    }

    /**
     * Given duration incorrect param when try to create recording then 400 error code is returned.
     *
     * @throws Exception the exception
     */
    @Test
    void givenDurationIncorrectParam_whenTryToCreateRecording_then400ErrorCodeIsReturned() throws Exception {
        // Given
        given(this.mockFlightRecorder.startRecordingFor(any())).willReturn(1L);

        // When
        this.webTestClient.post()
                .uri("/actuator/flightrecorder")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"timeUnit\": \"SECONDS\"}")
                .exchange()
                .expectStatus()
                .isBadRequest();

        // Then
        then(this.mockFlightRecorder).should(never()).startRecordingFor(any());
    }

    /**
     * Given time unit incorrect param when try to create recording then 400 error code is returned.
     *
     * @throws Exception the exception
     */
    @Test
    void givenTimeUnitIncorrectParam_whenTryToCreateRecording_then400ErrorCodeIsReturned() throws Exception {
        // Given
        given(this.mockFlightRecorder.startRecordingFor(any())).willReturn(1L);

        // When
        this.webTestClient.post()
                .uri("/actuator/flightrecorder")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"duration\": \"5\"}")
                .exchange()
                .expectStatus()
                .isBadRequest();

        // Then
        then(this.mockFlightRecorder).should(never()).startRecordingFor(any());
    }

    /**
     * Given correct params and failure at core when try to create recording then 500 error code is
     * returned.
     *
     * @throws Exception the exception
     */
    @Test
    void givenCorrectParamsAndFailureAtCore_whenTryToCreateRecording_then500ErrorCodeIsReturned()
            throws Exception {
        // Given
        given(this.mockFlightRecorder.startRecordingFor(any())).willThrow(IllegalArgumentException.class);

        // When
        this.webTestClient.post()
                .uri("/actuator/flightrecorder")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"duration\": \"5\",\"timeUnit\":\"SECONDS\"}")
                .exchange()
                .expectStatus()
                .is5xxServerError();

        // Then
        then(this.mockFlightRecorder).should().startRecordingFor(any());
    }

    /**
     * Given correct params when try to create recording then recording id is returned.
     *
     * @throws Exception the exception
     */
    @Test
    void givenCorrectParams_whenTryToCreateRecording_thenRecordingIdIsReturned() throws Exception {
        // Given
        given(this.mockFlightRecorder.startRecordingFor(any())).willReturn(1L);

        // When
        this.webTestClient.post()
                .uri("/actuator/flightrecorder")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue("{\"duration\": \"5\",\"timeUnit\":\"SECONDS\"}")
                .exchange()
                .expectStatus()
                .isCreated()
                .expectHeader()
                .value("Location", Matchers.containsString("/actuator/flightrecorder/1"));

        // Then
        then(this.mockFlightRecorder).should().startRecordingFor(any());

    }

    /**
     * Given correct request when try to retrieve all recordings then all recordings are returned.
     */
    @Test
    void givenCorrectRequest_whenTryToRetrieveAllRecordings_thenAllRecordingsAreReturned() {
        // given two flightRecorderPublicSession
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

        final List<FlightRecorderPublicSession> flightRecorderPublicSessions = Arrays
                .asList(flightRecorderPublicSession1, flightRecorderPublicSession2);

        given(this.mockFlightRecorder.sessions()).willReturn(flightRecorderPublicSessions);

        // when
        this.webTestClient.get()
                .uri("/actuator/flightrecorder")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON)
                .expectBody()
                .jsonPath("id", 1, 2);


        // then
        then(this.mockFlightRecorder).should().sessions();
    }

    /**
     * Given request when try to retrieve all recordings then internal server error returned.
     */
    @Test
    void givenRequest_whenTryToRetrieveAllRecordings_thenInternalServerErrorReturned() {
        // given
        given(this.mockFlightRecorder.sessions()).willThrow(IllegalArgumentException.class);

        // when
        this.webTestClient.get()
                .uri("/actuator/flightrecorder")
                .exchange()
                .expectStatus()
                .is5xxServerError();

        // then
        then(this.mockFlightRecorder).should().sessions();
    }

    /**
     * Given correct params when try to stop recording then recording id is returned.
     *
     * @throws Exception the exception
     */
    @Test
    void givenCorrectParams_whenTryToStopRecording_thenRecordingIdIsReturned() throws Exception {
        // given
        given(this.mockFlightRecorder.stopRecording(anyLong()))
                .willReturn(new File(this.getClass().getResource("/recording.jfr").toURI()));

        // when
        this.webTestClient.put()
                .uri("/actuator/flightrecorder/1")
                .exchange()
                .expectStatus()
                .isOk()
                .expectHeader()
                .contentType(MediaType.APPLICATION_JSON);

        // then
        then(this.mockFlightRecorder).should().stopRecording(anyLong());
    }

    /**
     * Given no existing recording id when try to stop recording then not found is returned.
     */
    @Test
    void givenNoExistingRecordingId_whenTryToStopRecording_thenNotFoundIsReturned() {
        // given
        given(this.mockFlightRecorder.stopRecording(anyLong())).willReturn(null);

        // when
        this.webTestClient.put()
                .uri("/actuator/flightrecorder/1")
                .exchange()
                .expectStatus()
                .isNotFound();

        // then
        then(this.mockFlightRecorder).should().stopRecording(anyLong());
    }

    /**
     * Given correct params when try to delete a recording then no content is returned.
     *
     * @throws Exception the exception
     */
    @Test
    void givenCorrectParams_whenTryToDeleteARecording_thenNoContentIsReturned() throws Exception {
        // given
        given(this.mockFlightRecorder.stopRecording(anyLong()))
                .willReturn(new File(this.getClass().getResource("/recording.jfr").toURI()));

        // when
        this.webTestClient.delete()
                .uri("/actuator/flightrecorder/1")
                .exchange()
                .expectStatus()
                .isNoContent();

        // then
        then(this.mockFlightRecorder).should().stopRecording(anyLong());
        then(this.mockFlightRecorder).should().deleteRecording(anyLong());
    }

    /**
     * Given no existing record id when try to delete a recording then not found is returned.
     */
    @Test
    void givenNoExistingRecordId_whenTryToDeleteARecording_thenNotFoundIsReturned() {
        // given
        given(this.mockFlightRecorder.stopRecording(anyLong())).willReturn(null);

        // when
        this.webTestClient.delete()
                .uri("/actuator/flightrecorder/1")
                .exchange()
                .expectStatus()
                .isNotFound();

        // then
        then(this.mockFlightRecorder).should().stopRecording(anyLong());
        then(this.mockFlightRecorder).should(never()).deleteRecording(anyLong());

    }

    /**
     * Given correct params when try to delete a recording then internal server error is returned.
     */
    @Test
    void givenCorrectParams_whenTryToDeleteARecording_thenInternalServerErrorIsReturned() {
        // given
        given(this.mockFlightRecorder.stopRecording(anyLong())).willThrow(IllegalArgumentException.class);

        // when
        this.webTestClient.delete()
                .uri("/actuator/flightrecorder/1")
                .exchange()
                .expectStatus()
                .is5xxServerError();

        // then
        then(this.mockFlightRecorder).should().stopRecording(anyLong());
        // then(this.mockFlightRecorder).should(never()).deleteRecording(anyLong());
    }

    /**
     * Given correct params when try to download recording then recording is returned.
     *
     * @throws Exception the exception
     */
    @Test
    void givenCorrectParams_whenTryToDownloadRecording_thenRecordingIsReturned() throws Exception {
        // given
        given(this.mockFlightRecorder.stopRecording(anyLong()))
                .willReturn(new File(this.getClass().getResource("/recording.jfr").toURI()));

        // when
        this.webTestClient.get()
                .uri("/actuator/flightrecorder/1")
                .exchange()
                .expectHeader()
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .expectHeader()
                .value("Cache-Control", Matchers.equalTo("no-cache, no-store, must-revalidate"))
                .expectHeader()
                .value("Pragma", Matchers.equalTo("no-cache"))
                .expectHeader()
                .value("Expires", Matchers.equalTo("0"))
                .expectStatus()
                .isOk();

        // then
        then(this.mockFlightRecorder).should().stopRecording(anyLong());
    }

    /**
     * Given no existing record id when try to download recording then not found is returned.
     */
    @Test
    void givenNoExistingRecordId_whenTryToDownloadRecording_thenNotFoundIsReturned() {
        // given
        given(this.mockFlightRecorder.stopRecording(anyLong())).willReturn(null);

        // when
        this.webTestClient.get()
                .uri("/actuator/flightrecorder/1")
                .exchange()
                .expectStatus()
                .isNotFound();

        // then
        then(this.mockFlightRecorder).should().stopRecording(anyLong());
    }

}
