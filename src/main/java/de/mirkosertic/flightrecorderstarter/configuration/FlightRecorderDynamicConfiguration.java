/*
 * Copyright 2020 Mirko Sertic
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package de.mirkosertic.flightrecorderstarter.configuration;

import de.mirkosertic.flightrecorderstarter.trigger.Trigger;
import org.springframework.boot.context.properties.ConfigurationProperties;

import java.time.temporal.ChronoUnit;
import java.util.List;

@ConfigurationProperties(prefix = "flightrecorder")
public class FlightRecorderDynamicConfiguration {

    public enum CleanupType {
        TTL,
        COUNT
    }

    private boolean enabled = true;
    private CleanupType recordingCleanupType;
    private long oldRecordingsTTL;
    private ChronoUnit oldRecordingsTTLTimeUnit;
    private int oldRecordingsMax;
    private String jfrBasePath;
    private String jfrCustomConfig;

    private List<Trigger> trigger;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public CleanupType getRecordingCleanupType() {
        return recordingCleanupType;
    }

    public void setRecordingCleanupType(String recordingCleanupType) {
        this.recordingCleanupType = CleanupType.valueOf(recordingCleanupType);
    }

    public long getOldRecordingsTTL() {
        return this.oldRecordingsTTL;
    }

    public void setOldRecordingsTTL(final long oldRecordingsTTL) {
        this.oldRecordingsTTL = oldRecordingsTTL;
    }

    public ChronoUnit getOldRecordingsTTLTimeUnit() {
        return this.oldRecordingsTTLTimeUnit;
    }

    public void setOldRecordingsTTLTimeUnit(final ChronoUnit oldRecordingsTTLTimeUnit) {
        this.oldRecordingsTTLTimeUnit = oldRecordingsTTLTimeUnit;
    }

    public int getOldRecordingsMax() {
        return this.oldRecordingsMax;
    }

    public void setOldRecordingsMax(int oldRecordingsMax) {
        this.oldRecordingsMax = oldRecordingsMax;
    }

    public List<Trigger> getTrigger() {
        return this.trigger;
    }

    public void setTrigger(final List<Trigger> trigger) {
        this.trigger = trigger;
    }

    public String getJfrBasePath() {
        return this.jfrBasePath;
    }

    public void setJfrBasePath(final String jfrBasePath) {
        this.jfrBasePath = jfrBasePath;
    }

    public String getJfrCustomConfig() {
        return this.jfrCustomConfig;
    }

    public void setJfrCustomConfig(final String jfrCustomConfig) {
        this.jfrCustomConfig = jfrCustomConfig;
    }
}
