package de.mirkosertic.flightrecorderstarter;


import jdk.jfr.Configuration;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class FlightRecorderTest {

    @Test
    void givenConfiguredCustomConfigurationProfile_whenSettingsAreChosen_thenCustomSettingsAreUed() {
        //TODO move this test to de.mirkosertic.flightrecorderstarter.core.FlightRecorderTest when the https://github.com/mirkosertic/flight-recorder-starter/pull/15/ is merged

        //Given
        final List<Configuration> mockConfigurationList = generateMockConfigurationList();

        final FlightRecorderDynamicConfiguration mockConfiguration = mock(FlightRecorderDynamicConfiguration.class);
        given(mockConfiguration.getJfrCustomConfig()).willReturn("customjfc");

        final FlightRecorder flightRecorder = new FlightRecorder(mockConfiguration);

        //When
        final Map<String, String> mapSettings = flightRecorder.getConfigurationSettings(mockConfigurationList);

        //Then
        assertThat(mapSettings.get("settingsKey")).isEqualTo("customValue");


    }

    @Test
    void givenNonConfiguredCustomConfigurationProfile_whenSettingsAreChosen_thenProfileSettingsAreUed() {
        //TODO move this test to de.mirkosertic.flightrecorderstarter.core.FlightRecorderTest when the https://github.com/mirkosertic/flight-recorder-starter/pull/15/ is merged

        //Given
        final List<Configuration> mockConfigurationList = generateMockConfigurationList();

        final FlightRecorderDynamicConfiguration mockConfiguration = mock(FlightRecorderDynamicConfiguration.class);
        given(mockConfiguration.getJfrCustomConfig()).willReturn(null);

        final FlightRecorder flightRecorder = new FlightRecorder(mockConfiguration);

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