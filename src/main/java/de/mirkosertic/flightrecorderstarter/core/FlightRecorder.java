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

import de.mirkosertic.flightrecorderstarter.actuator.model.FlightRecorderPublicSession;
import de.mirkosertic.flightrecorderstarter.configuration.FlightRecorderDynamicConfiguration;
import jdk.jfr.Configuration;
import jdk.jfr.Recording;
import jdk.jfr.RecordingState;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

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

    public long newRecording(final StartRecordingCommand command) {
        final Recording recording = new Recording(getConfigurationSettings(Configuration.getConfigurations(), command.getCustomSettings()));
        recording.setName("Spring Boot Starter Flight Recording");
        synchronized (this.recordings) {
            this.recordings.put(recording.getId(), new RecordingSession(recording, command.getDescription()));
        }
        return recording.getId();
    }

    Map<String, String> getConfigurationSettings(final List<Configuration> configs, final Map<String, String> customSettings) {
        final Map<String, String> settings = new HashMap<>();

        final String chosenConfiguration = this.configuration.getJfrCustomConfig() != null ? this.configuration.getJfrCustomConfig() : "profile";

        for (final Configuration config : configs) {
            LOGGER.log(Level.INFO, "Found configuration {0}" , config.getName());
            if (config.getName().contains(chosenConfiguration)) {
                LOGGER.log(Level.INFO, "Using configuration {0}" , config.getName());
                settings.putAll(config.getSettings());
            }
        }

        //Override settings with customSettings
        if (customSettings != null) {
            settings.putAll(customSettings);
        }

        return settings;
    }

    public void startRecording(final long recordingId, final Duration delayDuration) {
        synchronized (this.recordings) {
            final RecordingSession recordingSession = this.recordings.get(recordingId);
            if (recordingSession != null) {
                if (delayDuration == null) {
                    recordingSession.getRecording().start();
                } else {
                    recordingSession.getRecording().scheduleStart(delayDuration);
                }
            } else {
                LOGGER.log(Level.WARNING, "No recording with id {0} found" , recordingId);
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

    public void setRecordingOptions(final long recordingId, final StartRecordingCommand command, final File filename)
            throws IOException {
        synchronized (this.recordings) {
            final RecordingSession recordingSession = this.recordings.get(recordingId);
            if (recordingSession != null) {
                final Recording recording = recordingSession.getRecording();
                recording.setDuration(Duration.of(command.getDuration(), command.getTimeUnit()));
                recording.setDestination(filename.toPath());
                recording.setToDisk(true);
                if (command.getMaxAgeDuration() != null && command.getMaxAgeUnit() != null) {
                    recording.setMaxAge(Duration.of(command.getMaxAgeDuration(), command.getMaxAgeUnit()));
                }
                if (command.getMaxSize() != null) {
                    recording.setMaxSize(command.getMaxSize());
                }
            } else {
                LOGGER.log(Level.WARNING, "No recording with id {0} found" , recordingId);
            }
        }
    }

    public long startRecordingFor(final StartRecordingCommand command) throws IOException {
        synchronized (this.recordings) {
            final long recordingId = newRecording(command);

            File basePath = null;
            if (this.configuration.getJfrBasePath() != null) {
                basePath = Path.of(this.configuration.getJfrBasePath()).toFile();
            }

            final File tempFile = File.createTempFile("recording" , ".jfr" , basePath);

            LOGGER.log(Level.INFO, "Recording {0} to temp file {1}" , new Object[]{recordingId, tempFile});

            tempFile.deleteOnExit();
            setRecordingOptions(recordingId, command, tempFile);

            Duration delay = null;
            if (command.getDelayDuration() != null && command.getDelayUnit() != null) {
                delay = Duration.of(command.getDelayDuration(), command.getDelayUnit());
            }

            startRecording(recordingId, delay);

            return recordingId;
        }
    }

    @Scheduled(fixedDelayString = "${flightrecorder.recording-cleanup-interval}")
    public void cleanupOldRecordings() {
        synchronized (this.recordings) {

            Set<Long> deletableRecordings;
            if (this.configuration.getRecordingCleanupType() == FlightRecorderDynamicConfiguration.CleanupType.TTL) {
                deletableRecordings = getDeletableRecordingsByTTL();
            } else if (this.configuration.getRecordingCleanupType() == FlightRecorderDynamicConfiguration.CleanupType.COUNT) {
                deletableRecordings = getDeletableRecordingsByCount();
            } else { // can only happen in tests if cleanupType is not set
                throw new IllegalArgumentException(String.format("Unknown CleanupType '%s'. Deletion failed.", this.configuration.getRecordingCleanupType()));
            }

            deletableRecordings.forEach(this::deleteRecording);
        }
    }

    /**
     * Return finished recordings with startTime older than (Instant.now() - TTL)
     *
     * @return Set of recording IDs to be deleted
     */
    protected Set<Long> getDeletableRecordingsByTTL() {
        final Set<Long> deletableRecordings = new HashSet<>();
        final Instant deadline = Instant.now()
                .minus(this.configuration.getOldRecordingsTTL(), this.configuration.getOldRecordingsTTLTimeUnit());
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
                deletableRecordings.add(entry.getKey());
            }
        }
        LOGGER.log(Level.FINE, "Found {0} finished recording(s) to be deleted based on TTL ({1} {2}).",
                new Object[]{deletableRecordings.size(), this.configuration.getOldRecordingsTTL(), this.configuration.getOldRecordingsTTLTimeUnit()});

        return deletableRecordings;
    }

    /**
     * If the total number of recordings is above the threshold
     * defined in {@code flightrecorder.old-recordings-max},
     * returns the oldest recordings that are above the threshold & finished.
     *
     * @return Set of recording IDs to be deleted
     */
    protected Set<Long> getDeletableRecordingsByCount() {
        final int maxRecordings = this.configuration.getOldRecordingsMax();
        if (this.recordings.size() <= maxRecordings) {
            return Collections.emptySet();
        }

        List<Map.Entry<Long, RecordingSession>> recordingsAboveThreshold = this.recordings.entrySet().stream()
                .sorted(Comparator.comparing(recs -> recs.getValue().getRecording().getStartTime()))
                .collect(Collectors.toList())
                .subList(0, this.recordings.size() - maxRecordings);

        final Set<Long> deletableRecordings = new HashSet<>();
        for (final Map.Entry<Long, RecordingSession> entry : recordingsAboveThreshold) {
            final Recording recording = entry.getValue().getRecording();
            if ((recording.getState() == RecordingState.STOPPED || recording.getState() == RecordingState.CLOSED)) {
                try {
                    if (recording.getState() == RecordingState.STOPPED) {
                        recording.close();
                    }
                } catch (final Exception e) {
                    LOGGER.log(Level.INFO, "Cannot close recording {0}", new Object[]{recording.getId()});
                }
                deletableRecordings.add(entry.getKey());
            }
        }
        LOGGER.log(Level.FINE, "Found {0} finished recording(s) to be deleted based on COUNT threshold ({1} recordings).",
                new Object[]{deletableRecordings.size(), maxRecordings});

        return deletableRecordings;
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
                LOGGER.log(Level.WARNING, "No recording with id {0} found" , recordingId);
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
                final FlightRecorderPublicSession publicSession = getFlightRecorderPublicSession(session);
                result.add(publicSession);
            }
        }
        result.sort(Comparator.comparingLong(FlightRecorderPublicSession::getId));
        return result;
    }

    public FlightRecorderPublicSession getById(final Long recordingId) {
        synchronized (this.recordings) {
            final RecordingSession session = this.recordings.get(recordingId);
            if (session != null) {
                return getFlightRecorderPublicSession(session);
            } else {
                return null;
            }
        }
    }

    FlightRecorderPublicSession getFlightRecorderPublicSession(final RecordingSession session) {
        final FlightRecorderPublicSession publicSession = new FlightRecorderPublicSession();
        publicSession.setId(session.getRecording().getId());
        publicSession.setStatus(session.getRecording().getState().name());
        publicSession
                .setStartedAt(LocalDateTime.ofInstant(session.getRecording().getStartTime(), ZoneId.systemDefault()));
        if (session.getRecording().getState() == RecordingState.CLOSED
                || session.getRecording().getState() == RecordingState.STOPPED) {
            publicSession
                    .setFinishedAt(LocalDateTime.ofInstant(session.getRecording().getStopTime(), ZoneId.systemDefault()));
        }
        publicSession.setDescription(session.getDescription());
        return publicSession;
    }
}