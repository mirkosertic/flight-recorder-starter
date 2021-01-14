package de.mirkosertic.flightrecorderstarter.core;

import jdk.jfr.Recording;

class RecordingSession {

    private final Recording recording;
    private final String description;

    public RecordingSession(final Recording recording, final String description) {
        this.recording = recording;
        this.description = description;
    }

    public Recording getRecording() {
        return this.recording;
    }

    public String getDescription() {
        return this.description;
    }
}
