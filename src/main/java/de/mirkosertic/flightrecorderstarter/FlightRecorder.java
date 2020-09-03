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

import jdk.jfr.Configuration;
import jdk.jfr.Recording;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public final class FlightRecorder {

    private final static Logger LOGGER = Logger.getLogger(FlightRecorder.class.getCanonicalName());

    private final Map<Long, Recording> recordings;

    public FlightRecorder() {
        recordings = new HashMap<>();
    }

    public long newRecording() {
        final List<Configuration> configs = Configuration.getConfigurations();
        final Map<String, String> settings = new HashMap<>();
        for (final Configuration config : configs) {
            LOGGER.log(Level.INFO, "Found configuration {0}", config.getName());
            if (config.getName().contains("profile")) {
                LOGGER.log(Level.INFO, "Using configuration {0}", config.getName());
                settings.putAll(config.getSettings());
            }
        }
        final Recording recording = new Recording(settings);
        recording.setName("Spring Boot Starter Flight Recording");
        recordings.put(recording.getId(), recording);
        return recording.getId();
    }

    public void startRecording(final long recordingId) {
        final Recording recording = recordings.get(recordingId);
        if (recording != null) {
            recording.start();
        } else {
            LOGGER.log(Level.WARNING, "No recording with id {0} found", recordingId);
        }
    }

    public File stopRecording(final long recordingId) {
        final Recording recording = recordings.get(recordingId);
        if (recording != null) {
            recording.stop();
            return recording.getDestination().toFile();
        } else {
            LOGGER.log(Level.WARNING, "No recording with id {0} found", recordingId);
            return null;
        }
    }

    public void setRecordingOptions(final long recordingId, final Duration duration, final File filename) throws IOException {
        final Recording recording = recordings.get(recordingId);
        if (recording != null) {
            recording.setDuration(duration);
            recording.setDestination(filename.toPath());
            recording.setToDisk(true);
        } else {
            LOGGER.log(Level.WARNING, "No recording with id {0} found", recordingId);
        }
    }

    public long startRecordingFor(final Duration duration) throws IOException {
        final long recordingId = newRecording();
        final File tempFile = File.createTempFile("recording",".jfr");

        LOGGER.log(Level.INFO, "Recording {0} to temp file {1}", new Object[] {recordingId, tempFile});

        tempFile.deleteOnExit();
        setRecordingOptions(recordingId, duration, tempFile);
        startRecording(recordingId);

        return recordingId;
    }
}