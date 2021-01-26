package de.mirkosertic.flightrecorderstarter.actuator;

import de.mirkosertic.flightrecorderstarter.core.FlightRecorder;
import de.mirkosertic.flightrecorderstarter.core.StartRecordingCommand;
import org.junit.jupiter.api.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;

class FlightRecorderEndpointTest {

    private final FlightRecorder mockFlightRecorder = mock(FlightRecorder.class);
    private final ApplicationContext mockAppContext = mock(ApplicationContext.class);

    @Test
    void givenDurationIncorrectParam_whenTryToCreateRecording_then400ErrorCodeIsReturned() throws Exception {
        //Given
        final FlightRecorderEndpoint fre = new FlightRecorderEndpoint(this.mockAppContext, this.mockFlightRecorder);
        given(this.mockFlightRecorder.startRecordingFor(any())).willReturn(1L);
        final StartRecordingCommand command = new StartRecordingCommand();
        command.setTimeUnit(ChronoUnit.SECONDS);

        //When
        final ResponseEntity response = fre.startRecording(command);

        //Then
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        then(this.mockFlightRecorder).should(never()).startRecordingFor(any());
    }

    @Test
    void givenTimeUnitIncorrectParam_whenTryToCreateRecording_then400ErrorCodeIsReturned() throws Exception {
        //Given
        final FlightRecorderEndpoint fre = new FlightRecorderEndpoint(this.mockAppContext, this.mockFlightRecorder);
        given(this.mockFlightRecorder.startRecordingFor(any())).willReturn(1L);
        final StartRecordingCommand command = new StartRecordingCommand();
        command.setDuration(10L);

        //When
        final ResponseEntity response = fre.startRecording(command);

        //Then
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        then(this.mockFlightRecorder).should(never()).startRecordingFor(any());
    }

    @Test
    void givenCorrectParamsAndFailureAtCore_whenTryToCreateRecording_then400ErrorCodeIsReturned() throws Exception {
        //Given
        final FlightRecorderEndpoint fre = new FlightRecorderEndpoint(this.mockAppContext, this.mockFlightRecorder);
        given(this.mockFlightRecorder.startRecordingFor(any())).willThrow(IllegalArgumentException.class);

        final StartRecordingCommand command = new StartRecordingCommand();
        command.setDuration(10L);

        //When
        final ResponseEntity response = fre.startRecording(command);

        //Then
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.BAD_REQUEST);
        then(this.mockFlightRecorder).should(never()).startRecordingFor(any());
    }

    @Test
    void givenCorrectParams_whenTryToCreateRecording_thenRecordingIdIsReturned() throws Exception {
        //Given
        final FlightRecorderEndpoint fre = new FlightRecorderEndpoint(this.mockAppContext, this.mockFlightRecorder);
        final StartRecordingCommand command = new StartRecordingCommand();
        command.setDuration(10L);
        command.setTimeUnit(ChronoUnit.SECONDS);

        given(this.mockFlightRecorder.startRecordingFor(command)).willReturn(1L);


        //When
        final ResponseEntity response = fre.startRecording(command);

        //Then
        assertThat(response.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(response.getBody()).isEqualTo("1");
        then(this.mockFlightRecorder).should().startRecordingFor(command);
    }
}