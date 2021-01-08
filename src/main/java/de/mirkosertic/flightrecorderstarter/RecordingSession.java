package de.mirkosertic.flightrecorderstarter;

import jdk.jfr.Recording;

class RecordingSession {

    //FIXME this class should be deleted. It's only created temporally to avoid blocks.
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
