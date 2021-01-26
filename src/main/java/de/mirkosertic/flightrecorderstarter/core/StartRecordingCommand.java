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
package de.mirkosertic.flightrecorderstarter.core;

import java.time.temporal.ChronoUnit;
import java.util.Map;

public class StartRecordingCommand {

    private Long duration;
    private ChronoUnit timeUnit;
    private String description;
    private Long maxAgeDuration;
    private ChronoUnit maxAgeUnit;
    private Long delayDuration;
    private ChronoUnit delayUnit;
    private Long maxSize;

    private Map<String, String> customSettings;

    public Long getDuration() {
        return this.duration;
    }

    public void setDuration(final Long duration) {
        this.duration = duration;
    }

    public ChronoUnit getTimeUnit() {
        return this.timeUnit;
    }

    public void setTimeUnit(final ChronoUnit timeUnit) {
        this.timeUnit = timeUnit;
    }

    public String getDescription() {
        return this.description;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public Long getMaxAgeDuration() {
        return this.maxAgeDuration;
    }

    public void setMaxAgeDuration(final Long maxAgeDuration) {
        this.maxAgeDuration = maxAgeDuration;
    }

    public ChronoUnit getMaxAgeUnit() {
        return this.maxAgeUnit;
    }

    public void setMaxAgeUnit(final ChronoUnit maxAgeUnit) {
        this.maxAgeUnit = maxAgeUnit;
    }

    public Long getMaxSize() {
        return this.maxSize;
    }

    public void setMaxSize(final Long maxSize) {
        this.maxSize = maxSize;
    }

    public Long getDelayDuration() {
        return this.delayDuration;
    }

    public void setDelayDuration(final Long delayDuration) {
        this.delayDuration = delayDuration;
    }

    public ChronoUnit getDelayUnit() {
        return this.delayUnit;
    }

    public void setDelayUnit(final ChronoUnit delayUnit) {
        this.delayUnit = delayUnit;
    }

    public Map<String, String> getCustomSettings() {
        return this.customSettings;
    }

    public void setCustomSettings(final Map<String, String> customSettings) {
        this.customSettings = customSettings;
    }
}