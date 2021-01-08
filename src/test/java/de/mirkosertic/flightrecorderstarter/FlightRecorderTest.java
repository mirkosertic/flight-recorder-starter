package de.mirkosertic.flightrecorderstarter;


import jdk.jfr.Recording;
import jdk.jfr.RecordingState;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.BDDMockito.*;


class FlightRecorderTest {


    @Test
    void givenNoneRecordingSessions_whenCleanUpProcessIsExecuted_thenNothingHappened() {
        //  + no recordings -> deletionservice is not invoked

        //given
        final Map<Long, RecordingSession> recordings = spy(new HashMap<>());
        final FlightRecorderDynamicConfiguration configuration = new FlightRecorderDynamicConfiguration();
        configuration.setOldRecordingsTTL(1);
        configuration.setOldRecordingsTTLTimeUnit(ChronoUnit.SECONDS);
        final FlightRecorder flightRecorder = new FlightRecorder(configuration, recordings);


        //when
        flightRecorder.cleanupOldRecordings();

        //then

        then(recordings).should(never()).remove(any());
        assertThat(recordings.size()).isEqualTo(0);

    }

    @Test
    void givenSingleRecordingSessionClosed_whenCleanUpProcessIsExecuted_thenThatRecordingSessionIsDeleted() {
        //  + one recording candidate closed -> delete candidate

        //given
        final long idTest = 1L;
        final Map<Long, RecordingSession> recordings = spy(new HashMap<>());
        final Recording mockRecording = mock(Recording.class);
        final RecordingSession recordingSession = new RecordingSession(mockRecording, "");
        recordings.put(idTest, recordingSession);

        given(mockRecording.getState()).willReturn(RecordingState.CLOSED);
        given(mockRecording.getStartTime()).willReturn(Instant.now().minusSeconds(10));

        final File mockFile = mock(File.class);
        final Path mockPath = mock(Path.class);
        given(mockRecording.getDestination()).willReturn(mockPath);
        given(mockPath.toFile()).willReturn(mockFile);
        given(mockFile.delete()).willReturn(true);


        final FlightRecorderDynamicConfiguration configuration = new FlightRecorderDynamicConfiguration();
        configuration.setOldRecordingsTTL(1);
        configuration.setOldRecordingsTTLTimeUnit(ChronoUnit.SECONDS);
        final FlightRecorder flightRecorder = new FlightRecorder(configuration, recordings);


        //when
        flightRecorder.cleanupOldRecordings();

        //then
        then(recordings).should().remove(idTest);
        assertThat(recordings.size()).isEqualTo(0);

    }

    @Test
    void givenSingleRecordingSessionStopped_whenCleanUpProcessIsExecuted_thenThatRecordingSessionIsDeleted() {
        //  + one recording candidate stopped -> close candidate and delete candidate


        //given
        final long idTest = 1L;
        final Map<Long, RecordingSession> recordings = spy(new HashMap<>());
        final Recording mockRecording = mock(Recording.class);
        final RecordingSession recordingSession = new RecordingSession(mockRecording, "");
        recordings.put(idTest, recordingSession);

        given(mockRecording.getState()).willReturn(RecordingState.STOPPED);
        given(mockRecording.getStartTime()).willReturn(Instant.now().minusSeconds(10));

        final File mockFile = mock(File.class);
        final Path mockPath = mock(Path.class);
        given(mockRecording.getDestination()).willReturn(mockPath);
        given(mockPath.toFile()).willReturn(mockFile);
        given(mockFile.delete()).willReturn(true);

        final FlightRecorderDynamicConfiguration configuration = new FlightRecorderDynamicConfiguration();
        configuration.setOldRecordingsTTL(1);
        configuration.setOldRecordingsTTLTimeUnit(ChronoUnit.SECONDS);
        final FlightRecorder flightRecorder = new FlightRecorder(configuration, recordings);

        //when
        flightRecorder.cleanupOldRecordings();

        //then
        then(mockRecording).should(atLeastOnce()).close();
        then(recordings).should().remove(idTest);
        assertThat(recordings.size()).isEqualTo(0);

    }


    @Test
    void givenSingleRecordingSessionClosedNoCandidate_whenCleanUpProcessIsExecuted_thenThatRecordingSessionIsNotDeleted() {
        //  + one recording no candidate (starttime) -> no delete recording


        //given
        final long idTest = 1L;
        final Map<Long, RecordingSession> recordings = spy(new HashMap<>());
        final Recording mockRecording = mock(Recording.class);
        final RecordingSession recordingSession = new RecordingSession(mockRecording, "");
        recordings.put(idTest, recordingSession);

        given(mockRecording.getState()).willReturn(RecordingState.CLOSED);
        given(mockRecording.getStartTime()).willReturn(Instant.now().minusSeconds(1));

        final FlightRecorderDynamicConfiguration configuration = new FlightRecorderDynamicConfiguration();
        configuration.setOldRecordingsTTL(10);
        configuration.setOldRecordingsTTLTimeUnit(ChronoUnit.SECONDS);
        final FlightRecorder flightRecorder = new FlightRecorder(configuration, recordings);


        //when
        flightRecorder.cleanupOldRecordings();

        //then
        then(recordings).should(never()).remove(idTest);
        assertThat(recordings.size()).isEqualTo(1);

    }


    @Test
    void givenSingleRecordingSessionRunningNoCandidate_whenCleanUpProcessIsExecuted_thenThatRecordingSessionIsNotDeleted() {
        //  + one recording no candidate (status running) -> no delete recording


        //given
        final long idTest = 1L;
        final Map<Long, RecordingSession> recordings = spy(new HashMap<>());
        final Recording mockRecording = mock(Recording.class);
        final RecordingSession recordingSession = new RecordingSession(mockRecording, "");
        recordings.put(idTest, recordingSession);

        given(mockRecording.getState()).willReturn(RecordingState.RUNNING);
        given(mockRecording.getStartTime()).willReturn(Instant.now().minusSeconds(10));

        final FlightRecorderDynamicConfiguration configuration = new FlightRecorderDynamicConfiguration();
        configuration.setOldRecordingsTTL(1);
        configuration.setOldRecordingsTTLTimeUnit(ChronoUnit.SECONDS);
        final FlightRecorder flightRecorder = new FlightRecorder(configuration, recordings);


        //when
        flightRecorder.cleanupOldRecordings();

        //then
        then(recordings).should(never()).remove(idTest);
        assertThat(recordings.size()).isEqualTo(1);

    }

    @Test
    void givenOneCandidateOfTwoRecordingSession_whenCleanUpProcessIsExecuted_thenJustOnlyOneRecordingSessionIsDeleted() {
        //  + two recording, one candidate one no -> delete only the candidate

        //given
        final long idTest1 = 1L;
        final long idTest2 = 2L;
        final Map<Long, RecordingSession> recordings = spy(new HashMap<>());
        final Recording mockRecording1 = mock(Recording.class);
        final Recording mockRecording2 = mock(Recording.class);

        final RecordingSession recordingSession1 = new RecordingSession(mockRecording1, "");
        recordings.put(idTest1, recordingSession1);

        final RecordingSession recordingSession2 = new RecordingSession(mockRecording2, "");
        recordings.put(idTest2, recordingSession2);

        given(mockRecording1.getState()).willReturn(RecordingState.RUNNING);
        given(mockRecording1.getStartTime()).willReturn(Instant.now().minusSeconds(10));

        given(mockRecording2.getState()).willReturn(RecordingState.CLOSED);
        given(mockRecording2.getStartTime()).willReturn(Instant.now().minusSeconds(10));


        final File mockFile = mock(File.class);
        final Path mockPath = mock(Path.class);
        given(mockRecording2.getDestination()).willReturn(mockPath);
        given(mockPath.toFile()).willReturn(mockFile);
        given(mockFile.delete()).willReturn(true);


        final FlightRecorderDynamicConfiguration configuration = new FlightRecorderDynamicConfiguration();
        configuration.setOldRecordingsTTL(1);
        configuration.setOldRecordingsTTLTimeUnit(ChronoUnit.SECONDS);
        final FlightRecorder flightRecorder = new FlightRecorder(configuration, recordings);


        //when
        flightRecorder.cleanupOldRecordings();

        //then
        then(recordings).should(never()).remove(idTest1);
        then(recordings).should().remove(idTest2);
        assertThat(recordings.size()).isEqualTo(1);


    }

    @Test
    void givenTwoRecordingSessionCandidate_whenCleanUpProcessIsExecuted_thenAllRecordingSessionIsDeleted() {
        //  + two recording candidates -> delete all the candidates

        //given
        final long idTest1 = 1L;
        final long idTest2 = 2L;
        final Map<Long, RecordingSession> recordings = spy(new HashMap<>());
        final Recording mockRecording1 = mock(Recording.class);
        final Recording mockRecording2 = mock(Recording.class);

        final RecordingSession recordingSession1 = new RecordingSession(mockRecording1, "");
        recordings.put(idTest1, recordingSession1);

        final RecordingSession recordingSession2 = new RecordingSession(mockRecording2, "");
        recordings.put(idTest2, recordingSession2);

        given(mockRecording1.getState()).willReturn(RecordingState.CLOSED);
        given(mockRecording1.getStartTime()).willReturn(Instant.now().minusSeconds(10));

        given(mockRecording2.getState()).willReturn(RecordingState.CLOSED);
        given(mockRecording2.getStartTime()).willReturn(Instant.now().minusSeconds(10));

        final File mockFile = mock(File.class);
        final Path mockPath = mock(Path.class);
        given(mockPath.toFile()).willReturn(mockFile);
        given(mockFile.delete()).willReturn(true);
        given(mockRecording1.getDestination()).willReturn(mockPath);
        given(mockRecording2.getDestination()).willReturn(mockPath);


        final FlightRecorderDynamicConfiguration configuration = new FlightRecorderDynamicConfiguration();
        configuration.setOldRecordingsTTL(1);
        configuration.setOldRecordingsTTLTimeUnit(ChronoUnit.SECONDS);
        final FlightRecorder flightRecorder = new FlightRecorder(configuration, recordings);


        //when
        flightRecorder.cleanupOldRecordings();

        //then
        then(recordings).should().remove(idTest1);
        then(recordings).should().remove(idTest2);
        assertThat(recordings.size()).isEqualTo(0);


    }


    @Test
    void givenNoneRecordingSessions_whenTryToDelete_thenNothingHappened() {
        //  + id not found -> check doesn't happened


        //given
        final Map<Long, RecordingSession> recordings = spy(new HashMap<>());
        final FlightRecorder flightRecorder = spy(new FlightRecorder(new FlightRecorderDynamicConfiguration(), recordings));
        final long idTest = 1L;

        //when
        flightRecorder.deleteRecording(idTest);

        //then
        then(recordings).should().remove(idTest);
        assertThat(recordings.size()).isEqualTo(0);
    }

    @Test
    void givenARecordingSessionsRunning_whenTryToDelete_thenTheRecordingIsStoppedClosedAndNotInTheSystemNeitherMemory() throws IOException {
        //  + running -> check recording are not in the system neither in memory map


        //given
        final Map<Long, RecordingSession> recordings = spy(new HashMap<>());
        final long idTest = 1L;
        final RecordingSession mockRecordingSession = mock(RecordingSession.class);
        final Recording mockRecording = mock(Recording.class);

        //create destination file
        final Path tempFile = Files.createTempFile("testing", "jfr");
        //Mock recording destination file
        given(mockRecording.getDestination()).willReturn(tempFile);
        //Mock recording State
        given(mockRecording.getState()).willReturn(RecordingState.RUNNING);

        given(mockRecordingSession.getRecording()).willReturn(mockRecording);

        recordings.put(idTest, mockRecordingSession);
        final FlightRecorder flightRecorder = spy(new FlightRecorder(new FlightRecorderDynamicConfiguration(), recordings));

        //when
        flightRecorder.deleteRecording(idTest);

        //then
        then(mockRecording).should().stop();
        then(mockRecording).should().close();
        then(recordings).should().remove(idTest);
        assertThat(recordings.size()).isEqualTo(0);
        assertFalse(tempFile.toFile().exists());
    }

    @Test
    void givenARecordingSessionsRunning_whenTryToDelete_thenTheRecordingIsClosedAndNotInTheSystemNeitherMemory() throws IOException {
        //  + stopped -> check recording are not in the system neither in memory map


        //given
        final Map<Long, RecordingSession> recordings = spy(new HashMap<>());
        final long idTest = 1L;
        final RecordingSession mockRecordingSession = mock(RecordingSession.class);
        final Recording mockRecording = mock(Recording.class);

        //create destination file
        final Path tempFile = Files.createTempFile("testing", "jfr");
        //Mock recording destination file
        given(mockRecording.getDestination()).willReturn(tempFile);
        //Mock recording State
        given(mockRecording.getState()).willReturn(RecordingState.STOPPED);

        given(mockRecordingSession.getRecording()).willReturn(mockRecording);

        recordings.put(idTest, mockRecordingSession);
        final FlightRecorder flightRecorder = spy(new FlightRecorder(new FlightRecorderDynamicConfiguration(), recordings));

        //when
        flightRecorder.deleteRecording(idTest);

        //then
        then(mockRecording).should(never()).stop();
        then(mockRecording).should().close();
        then(recordings).should().remove(idTest);
        assertThat(recordings.size()).isEqualTo(0);
        assertFalse(tempFile.toFile().exists());
    }

    @Test
    void givenARecordingSessionsRunning_whenTryToDelete_thenTheRecordingIsNotInTheSystemNeitherMemory() throws IOException {
        //  + closed -> check recording are not in the system neither in memory map


        //given
        final Map<Long, RecordingSession> recordings = spy(new HashMap<>());
        final long idTest = 1L;
        final RecordingSession mockRecordingSession = mock(RecordingSession.class);
        final Recording mockRecording = mock(Recording.class);

        //create destination file
        final Path tempFile = Files.createTempFile("testing", "jfr");
        //Mock recording destination file
        given(mockRecording.getDestination()).willReturn(tempFile);
        //Mock recording State
        given(mockRecording.getState()).willReturn(RecordingState.CLOSED);

        given(mockRecordingSession.getRecording()).willReturn(mockRecording);

        recordings.put(idTest, mockRecordingSession);
        final FlightRecorder flightRecorder = spy(new FlightRecorder(new FlightRecorderDynamicConfiguration(), recordings));

        //when
        flightRecorder.deleteRecording(idTest);

        //then
        then(mockRecording).should(never()).stop();
        then(mockRecording).should(never()).close();
        then(recordings).should().remove(idTest);
        assertThat(recordings.size()).isEqualTo(0);
        assertFalse(tempFile.toFile().exists());
    }


}