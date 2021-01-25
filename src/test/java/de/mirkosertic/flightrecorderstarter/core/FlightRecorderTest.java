package de.mirkosertic.flightrecorderstarter.core;


import de.mirkosertic.flightrecorderstarter.configuration.FlightRecorderDynamicConfiguration;
import jdk.jfr.Configuration;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.system.SystemProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

class FlightRecorderTest {

    private final Map<Long, RecordingSession> spyRecordings;
    private final FlightRecorderDynamicConfiguration mockConfiguration;

    public FlightRecorderTest() {
        this.spyRecordings = spy(new HashMap<>());
        this.mockConfiguration = mock(FlightRecorderDynamicConfiguration.class);
    }

    @BeforeEach
    void restMocks() {
        reset(this.spyRecordings, this.mockConfiguration);
    }

    @Test
    void givenBasePathConfigured_whenANewRecordingHasFinished_ThenTheFileIsStoredAtConfiguredBasePath()
            throws IOException {
        //Given
        final Path temporalPath = Files.createTempDirectory("myCustomPath");

        given(this.mockConfiguration.getJfrBasePath()).willReturn(temporalPath.toFile().getAbsolutePath());

        final FlightRecorder flightRecorder = new FlightRecorder(this.mockConfiguration, this.spyRecordings);

        //When
        final StartRecordingCommand command = new StartRecordingCommand();
        command.setDuration(10L);
        command.setTimeUnit(ChronoUnit.MILLIS);
        command.setDescription("dummyDescription");
        final long recordingId = flightRecorder.startRecordingFor(command);

        //Then
        final RecordingSession recordingSession = this.spyRecordings.get(recordingId);
        assertThat(recordingSession).isNotNull();
        assertThat(recordingSession.getRecording().getDestination().getParent()).isEqualTo(temporalPath);
    }

    @Test
    void givenBasePathNotConfigured_whenANewRecordingHasFinished_ThenTheFileIsStoredAtDefaultBasePath()
            throws IOException {
        //Given
        final Path defaultTemporalPath = Path.of(SystemProperties.get("java.io.tmpdir"));

        given(this.mockConfiguration.getJfrBasePath()).willReturn(null);

        final FlightRecorder flightRecorder = new FlightRecorder(this.mockConfiguration, this.spyRecordings);

        //When
        final StartRecordingCommand command = new StartRecordingCommand();
        command.setDuration(10L);
        command.setTimeUnit(ChronoUnit.MILLIS);
        command.setDescription("dummyDescription");
        final long recordingId = flightRecorder.startRecordingFor(command);

        //Then
        final RecordingSession recordingSession = this.spyRecordings.get(recordingId);
        assertThat(recordingSession).isNotNull();
        assertThat(recordingSession.getRecording().getDestination().getParent()).isEqualTo(defaultTemporalPath);
    }


    @Test
    void givenConfiguredCustomConfigurationProfile_whenSettingsAreChosen_thenCustomSettingsAreUed() {
        //Given
        final List<Configuration> mockConfigurationList = generateMockConfigurationList();

        given(this.mockConfiguration.getJfrCustomConfig()).willReturn("customjfc");

        final FlightRecorder flightRecorder = new FlightRecorder(this.mockConfiguration);

        //When
        final Map<String, String> mapSettings = flightRecorder.getConfigurationSettings(mockConfigurationList);

        //Then
        assertThat(mapSettings.get("settingsKey")).isEqualTo("customValue");


    }

    @Test
    void givenNonConfiguredCustomConfigurationProfile_whenSettingsAreChosen_thenProfileSettingsAreUed() {
        //Given
        final List<Configuration> mockConfigurationList = generateMockConfigurationList();

        given(this.mockConfiguration.getJfrCustomConfig()).willReturn(null);

        final FlightRecorder flightRecorder = new FlightRecorder(this.mockConfiguration);

        //When
        final Map<String, String> mapSettings = flightRecorder.getConfigurationSettings(mockConfigurationList);

        //Then
        assertThat(mapSettings.get("settingsKey")).isEqualTo("profileValue");


    }


    List<Configuration> generateMockConfigurationList() {
        final List<Configuration> mockConfigurationList = new ArrayList<>();
        final Configuration defaultConfiguration = mock(Configuration.class);
        given(defaultConfiguration.getName()).willReturn("default");
        final Map<String, String> defaultSettings = new HashMap<>();
        defaultSettings.put("settingsKey", "defaultValue");
        given(defaultConfiguration.getSettings()).willReturn(defaultSettings);
        mockConfigurationList.add(defaultConfiguration);

        final Configuration profileConfiguration = mock(Configuration.class);
        given(profileConfiguration.getName()).willReturn("profile");
        final Map<String, String> profileSettings = new HashMap<>();
        profileSettings.put("settingsKey", "profileValue");
        given(profileConfiguration.getSettings()).willReturn(profileSettings);
        mockConfigurationList.add(profileConfiguration);

        final Configuration customConfiguration = mock(Configuration.class);
        given(customConfiguration.getName()).willReturn("customjfc");
        final Map<String, String> customSettings = new HashMap<>();
        customSettings.put("settingsKey", "customValue");
        given(customConfiguration.getSettings()).willReturn(customSettings);
        mockConfigurationList.add(customConfiguration);

        return mockConfigurationList;
    }
}