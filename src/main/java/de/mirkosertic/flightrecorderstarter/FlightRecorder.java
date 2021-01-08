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
import jdk.jfr.RecordingState;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlightRecorder {

    private final static Logger LOGGER = Logger.getLogger(FlightRecorder.class.getCanonicalName());

    private final Map<Long, RecordingSession> recordings;
    private final FlightRecorderDynamicConfiguration configuration;

    public FlightRecorder(final FlightRecorderDynamicConfiguration configuration) {
        this(configuration, new HashMap<>());
    }

    FlightRecorder(final FlightRecorderDynamicConfiguration configuration,
                   final Map<Long, RecordingSession> recordings) {
        this.configuration = configuration;
        this.recordings = recordings;
    }

    public long newRecording(final String description) {
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
        synchronized (this.recordings) {
            this.recordings.put(recording.getId(), new RecordingSession(recording, description));
        }
        return recording.getId();
    }

    public void startRecording(final long recordingId) {
        synchronized (this.recordings) {
            final RecordingSession recordingSession = this.recordings.get(recordingId);
            if (recordingSession != null) {
                recordingSession.getRecording().start();
            } else {
                LOGGER.log(Level.WARNING, "No recording with id {0} found", recordingId);
            }
        }
    }

    public File stopRecording(final long recordingId) {
        synchronized (this.recordings) {
            final RecordingSession recordingSession = this.recordings.get(recordingId);
            if (recordingSession != null) {
                final Recording recording = recordingSession.getRecording();
                if (recording.getState() == RecordingState.RUNNING) {
                    recording.stop();
                }
                return recording.getDestination().toFile();
            } else {
                LOGGER.log(Level.WARNING, "No recording with id {0} found", recordingId);
                return null;
            }
        }
    }

    public void setRecordingOptions(final long recordingId, final Duration duration, final File filename)
            throws IOException {
        synchronized (this.recordings) {
            final RecordingSession recordingSession = this.recordings.get(recordingId);
            if (recordingSession != null) {
                final Recording recording = recordingSession.getRecording();
                recording.setDuration(duration);
                recording.setDestination(filename.toPath());
                recording.setToDisk(true);
            } else {
                LOGGER.log(Level.WARNING, "No recording with id {0} found", recordingId);
            }
        }
    }

    public long startRecordingFor(final Duration duration, final String description) throws IOException {
        synchronized (this.recordings) {
            final long recordingId = newRecording(description);
            final File tempFile = File.createTempFile("recording", ".jfr");

            LOGGER.log(Level.INFO, "Recording {0} to temp file {1}", new Object[]{recordingId, tempFile});

            tempFile.deleteOnExit();
            setRecordingOptions(recordingId, duration, tempFile);
            startRecording(recordingId);

            return recordingId;
        }
    }

    @Scheduled(fixedDelayString = "${flightrecorder.recordingCleanupInterval}")
    public void cleanupOldRecordings() {
        synchronized (this.recordings) {
            final Instant deadline = Instant.now()
                    .minus(this.configuration.getOldRecordingsTTL(), this.configuration.getOldRecordingsTTLTimeUnit());
            final Set<Long> deletedRecordings = new HashSet<>();
            for (final Map.Entry<Long, RecordingSession> entry : this.recordings.entrySet()) {
                final Recording recording = entry.getValue().getRecording();
                if ((recording.getState() == RecordingState.STOPPED || recording.getState() == RecordingState.CLOSED) &&
                        recording.getStartTime().isBefore(deadline)) {
                    try {
                        if (recording.getState() == RecordingState.STOPPED) {
                            recording.close();
                        }
                    } catch (final Exception e) {
                        LOGGER.log(Level.INFO, "Cannot close recording {0}", new Object[]{recording.getId()});
                    }
                    deletedRecordings.add(entry.getKey());
                }
            }
            deletedRecordings.forEach(this::deleteRecording);


        }
    }

    public void deleteRecording(final long recordingId) {
        synchronized (this.recordings) {
            final RecordingSession recordingSession = this.recordings.remove(recordingId);
            if (recordingSession != null) {
                final Recording recording = recordingSession.getRecording();
                if (recording.getState() == RecordingState.RUNNING) {
                    recording.stop();
                    recording.close();
                } else if (recording.getState() == RecordingState.STOPPED) {
                    recording.close();
                }

                recordingSession.getRecording().getDestination().toFile().delete();

            } else {
                LOGGER.log(Level.WARNING, "No recording with id {0} found", recordingId);
            }
        }
    }


    public boolean isRecordingStopped(final long recordingId) {
        synchronized (this.recordings) {
            final RecordingSession recordingSession = this.recordings.get(recordingId);
            if (recordingSession == null) {
                return true;
            }
            final Recording recording = recordingSession.getRecording();
            return recording.getState() == RecordingState.CLOSED || recording.getState() == RecordingState.STOPPED;
        }
    }

    public List<FlightRecorderPublicSession> sessions() {
        final List<FlightRecorderPublicSession> result = new ArrayList<>();
        synchronized (this.recordings) {
            for (final RecordingSession session : this.recordings.values()) {
                final FlightRecorderPublicSession publicSession = new FlightRecorderPublicSession();
                publicSession.setId(session.getRecording().getId());
                publicSession.setStatus(session.getRecording().getState().name());
                publicSession
                        .setStartedAt(LocalDateTime.ofInstant(session.getRecording().getStartTime(), ZoneId.systemDefault()));
                if (session.getRecording().getState() == RecordingState.CLOSED
                        || session.getRecording().getState() == RecordingState.STOPPED) {
                    publicSession
                            .setFinishedAt(
                                    LocalDateTime.ofInstant(session.getRecording().getStopTime(), ZoneId.systemDefault()));
                }
                publicSession.setDescription(session.getDescription());
                result.add(publicSession);
            }
        }
        result.sort(Comparator.comparingLong(FlightRecorderPublicSession::getId));
        return result;
    }
}