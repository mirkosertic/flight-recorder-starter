package de.mirkosertic.flightrecorderstarter.core;


import de.mirkosertic.flightrecorderstarter.configuration.FlightRecorderDynamicConfiguration;
import jdk.jfr.Configuration;
import jdk.jfr.RecordingState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.system.SystemProperties;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
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
    void resetMocks() {
        reset(this.spyRecordings, this.mockConfiguration);
    }


    @Test
    void givenCommandWithDelayConfigured_whenANewRecordingHasExecuted_ThenTheRecordingIsDelayed()
            throws IOException {
        //Given
        given(this.mockConfiguration.getJfrBasePath()).willReturn(null);

        final FlightRecorder flightRecorder = new FlightRecorder(this.mockConfiguration, this.spyRecordings);

        //When
        final StartRecordingCommand command = new StartRecordingCommand();
        command.setDuration(10L);
        command.setTimeUnit(ChronoUnit.MILLIS);
        command.setDescription("dummyDescription");
        command.setDelayDuration(10L);
        command.setDelayUnit(ChronoUnit.SECONDS);
        final long recordingId = flightRecorder.startRecordingFor(command);

        //Then
        final RecordingSession recordingSession = this.spyRecordings.get(recordingId);
        assertThat(recordingSession).isNotNull();
        assertThat(recordingSession.getRecording().getState()).isEqualTo(RecordingState.DELAYED);
    }

    @Test
    void givenCommandWithDelayDurationNotConfigured_whenANewRecordingHasExecuted_ThenTheRecordingIsNotDelayed()
            throws IOException {
        //Given
        given(this.mockConfiguration.getJfrBasePath()).willReturn(null);

        final FlightRecorder flightRecorder = new FlightRecorder(this.mockConfiguration, this.spyRecordings);

        //When
        final StartRecordingCommand command = new StartRecordingCommand();
        command.setDuration(10L);
        command.setTimeUnit(ChronoUnit.MILLIS);
        command.setDescription("dummyDescription");
        command.setDelayUnit(ChronoUnit.SECONDS);
        final long recordingId = flightRecorder.startRecordingFor(command);

        //Then
        final RecordingSession recordingSession = this.spyRecordings.get(recordingId);
        assertThat(recordingSession).isNotNull();
        assertThat(recordingSession.getRecording().getState()).isNotEqualTo(RecordingState.DELAYED);
    }

    @Test
    void givenCommandWithDelayTimeUnitNotConfigured_whenANewRecordingHasExecuted_ThenTheRecordingIsNotDelayed()
            throws IOException {
        //Given
        given(this.mockConfiguration.getJfrBasePath()).willReturn(null);

        final FlightRecorder flightRecorder = new FlightRecorder(this.mockConfiguration, this.spyRecordings);

        //When
        final StartRecordingCommand command = new StartRecordingCommand();
        command.setDuration(10L);
        command.setTimeUnit(ChronoUnit.MILLIS);
        command.setDescription("dummyDescription");
        command.setDelayDuration(10L);
        final long recordingId = flightRecorder.startRecordingFor(command);

        //Then
        final RecordingSession recordingSession = this.spyRecordings.get(recordingId);
        assertThat(recordingSession).isNotNull();
        assertThat(recordingSession.getRecording().getState()).isNotEqualTo(RecordingState.DELAYED);
    }


    @Test
    void givenCommandWithMaxAgeConfigured_whenANewRecordingHasExecuted_ThenTheRecordingHasConfiguredThatMaxAge()
            throws IOException {
        //Given
        given(this.mockConfiguration.getJfrBasePath()).willReturn(null);

        final FlightRecorder flightRecorder = new FlightRecorder(this.mockConfiguration, this.spyRecordings);

        //When
        final StartRecordingCommand command = new StartRecordingCommand();
        command.setDuration(10L);
        command.setTimeUnit(ChronoUnit.MILLIS);
        command.setDescription("dummyDescription");
        command.setMaxAgeDuration(1L);
        command.setMaxAgeUnit(ChronoUnit.MILLIS);
        final long recordingId = flightRecorder.startRecordingFor(command);

        //Then
        final RecordingSession recordingSession = this.spyRecordings.get(recordingId);
        assertThat(recordingSession).isNotNull();
        assertThat(recordingSession.getRecording().getMaxAge()).isEqualTo(Duration.ofMillis(1));

    }

    @Test
    void givenCommandWithMaxAgeDurationNotConfigured_whenANewRecordingHasExecuted_ThenTheRecordingHasNotConfiguredThatMaxAge()
            throws IOException {
        //Given
        given(this.mockConfiguration.getJfrBasePath()).willReturn(null);

        final FlightRecorder flightRecorder = new FlightRecorder(this.mockConfiguration, this.spyRecordings);

        //When
        final StartRecordingCommand command = new StartRecordingCommand();
        command.setDuration(10L);
        command.setTimeUnit(ChronoUnit.MILLIS);
        command.setDescription("dummyDescription");
        command.setMaxAgeUnit(ChronoUnit.MILLIS);
        final long recordingId = flightRecorder.startRecordingFor(command);

        //Then
        final RecordingSession recordingSession = this.spyRecordings.get(recordingId);
        assertThat(recordingSession).isNotNull();
        assertThat(recordingSession.getRecording().getMaxAge()).isNull();
    }

    @Test
    void givenCommandWithMaxAgeTimeUnitNotConfigured_whenANewRecordingHasExecuted_ThenTheRecordingHasNotConfiguredThatMaxAge()
            throws IOException {
        //Given
        given(this.mockConfiguration.getJfrBasePath()).willReturn(null);

        final FlightRecorder flightRecorder = new FlightRecorder(this.mockConfiguration, this.spyRecordings);

        //When
        final StartRecordingCommand command = new StartRecordingCommand();
        command.setDuration(10L);
        command.setTimeUnit(ChronoUnit.MILLIS);
        command.setDescription("dummyDescription");
        command.setMaxAgeDuration(1L);
        final long recordingId = flightRecorder.startRecordingFor(command);

        //Then
        final RecordingSession recordingSession = this.spyRecordings.get(recordingId);
        assertThat(recordingSession).isNotNull();
        assertThat(recordingSession.getRecording().getMaxAge()).isNull();
    }


    @Test
    void givenCommandWithMaxSizeConfigured_whenANewRecordingHasExecuted_ThenTheRecordingHasConfiguredThatMaxSize()
            throws IOException {
        //Given
        given(this.mockConfiguration.getJfrBasePath()).willReturn(null);

        final FlightRecorder flightRecorder = new FlightRecorder(this.mockConfiguration, this.spyRecordings);

        //When
        final StartRecordingCommand command = new StartRecordingCommand();
        command.setDuration(10L);
        command.setTimeUnit(ChronoUnit.MILLIS);
        command.setDescription("dummyDescription");
        command.setMaxSize(100L);
        final long recordingId = flightRecorder.startRecordingFor(command);

        //Then
        final RecordingSession recordingSession = this.spyRecordings.get(recordingId);
        assertThat(recordingSession).isNotNull();
        assertThat(recordingSession.getRecording().getMaxSize()).isEqualTo(100L);

    }

    @Test
    void givenCommandWithMaxSizeNotConfigured_whenANewRecordingHasExecuted_ThenTheRecordingHasNotConfiguredThatMaxSize()
            throws IOException {
        //Given
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
        assertThat(recordingSession.getRecording().getMaxSize()).isEqualTo(0L);
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
        final Map<String, String> mapSettings = flightRecorder.getConfigurationSettings(mockConfigurationList, null);

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
        final Map<String, String> mapSettings = flightRecorder.getConfigurationSettings(mockConfigurationList, null);

        //Then
        assertThat(mapSettings.get("settingsKey")).isEqualTo("profileValue");
        assertThat(mapSettings.get("settingsKeyForOverride")).isEqualTo("profileValueForOverride");

    }


    @Test
    void givenNonConfiguredCustomConfigurationProfileButCustomSettings_whenSettingsAreChosen_thenProfileSettingsAreUedAndCustomSettingsOverrideProperties() {
        //Given
        final List<Configuration> mockConfigurationList = generateMockConfigurationList();

        given(this.mockConfiguration.getJfrCustomConfig()).willReturn(null);

        final FlightRecorder flightRecorder = new FlightRecorder(this.mockConfiguration);

        final Map<String, String> customSettings = new HashMap();
        customSettings.put("settingsKeyForOverride", "customSettingProfileValueOverriden");

        //When
        final Map<String, String> mapSettings = flightRecorder.getConfigurationSettings(mockConfigurationList, customSettings);

        //Then
        assertThat(mapSettings.get("settingsKey")).isEqualTo("profileValue");
        assertThat(mapSettings.get("settingsKeyForOverride")).isEqualTo("customSettingProfileValueOverriden");


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
        profileSettings.put("settingsKeyForOverride", "profileValueForOverride");
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