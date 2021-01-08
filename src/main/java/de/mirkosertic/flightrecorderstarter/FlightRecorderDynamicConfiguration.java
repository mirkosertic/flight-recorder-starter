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
package de.mirkosertic.flightrecorderstarter;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.time.temporal.ChronoUnit;
import java.util.List;

@Configuration
@ConfigurationProperties(prefix = "flightrecorder")
public class FlightRecorderDynamicConfiguration {

    private boolean enabled = true;
    private long oldRecordingsTTL;
    private ChronoUnit oldRecordingsTTLTimeUnit;
    private List<Trigger> trigger;

    public boolean isEnabled() {
        return this.enabled;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
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

    public List<Trigger> getTrigger() {
        return this.trigger;
    }

    public void setTrigger(final List<Trigger> trigger) {
        this.trigger = trigger;
    }

}
