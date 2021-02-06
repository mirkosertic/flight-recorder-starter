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
package de.mirkosertic.flightrecorderstarter.actuator;

import de.mirkosertic.flightrecorderstarter.actuator.model.FlightRecorderPublicSession;
import de.mirkosertic.flightrecorderstarter.core.FlightRecorder;
import de.mirkosertic.flightrecorderstarter.core.StartRecordingCommand;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.File;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestControllerEndpoint(id = "flightrecorder")
public class FlightRecorderEndpoint {

    private final static Logger LOGGER = Logger.getLogger(FlightRecorder.class.getCanonicalName());


    private final FlightRecorder flightRecorder;

    public FlightRecorderEndpoint(
            final FlightRecorder flightRecorder) {
        this.flightRecorder = flightRecorder;
    }


    @GetMapping("/")
    public ResponseEntity allSessions() {
        try {
            LOGGER.log(Level.INFO, "Retrieving all known recording sessions");
            final List<FlightRecorderPublicSession> sessions = this.flightRecorder.sessions();
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(sessions);
        } catch (final Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PostMapping("/")
    public ResponseEntity startRecording(@RequestBody final StartRecordingCommand command) {
        if (command.getDuration() == null || command.getTimeUnit() == null) {
            return ResponseEntity.badRequest().body("Duration and TimeUnit cannot be null");
        }
        try {
            LOGGER.log(Level.INFO, "Trying to start recording for {0} {1}" ,
                    new Object[]{command.getDuration(), command.getTimeUnit()});
            final long recordingId = this.flightRecorder
                    .startRecordingFor(command);
            LOGGER.log(Level.INFO, "Created recording with ID {0}" , recordingId);
            return ResponseEntity
                    .created(ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}").build(recordingId)).build();
        } catch (final Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }

    @PutMapping("/{recordingId}")
    public ResponseEntity stopRecording(@PathVariable final long recordingId) {
        LOGGER.log(Level.INFO, "Stopping recording with ID {0}" , recordingId);
        final File file = this.flightRecorder.stopRecording(recordingId);
        if (file != null) {
            return ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(this.flightRecorder.getById(recordingId));
        }
        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{recordingId}")
    public ResponseEntity deleteRecording(@PathVariable final long recordingId) {
        try {
            LOGGER.log(Level.INFO, "Deleting recording with ID {0}" , recordingId);

            final File file = this.flightRecorder.stopRecording(recordingId);
            if (file != null) {
                this.flightRecorder.deleteRecording(recordingId);

                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.notFound().build();


        } catch (final Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
        }
    }


    @GetMapping("/{recordingId}")
    public ResponseEntity downloadRecording(@PathVariable final long recordingId) {
        LOGGER.log(Level.INFO, "Closing recording with ID {0} and downloading file" , recordingId);
        final File file = this.flightRecorder.stopRecording(recordingId);
        if (file != null) {
            final HttpHeaders headers = new HttpHeaders();
            headers
                    .add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=flightrecording_" + recordingId + ".jfr");
            headers.add("Cache-Control" , "no-cache, no-store, must-revalidate");
            headers.add("Pragma" , "no-cache");
            headers.add("Expires" , "0");

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(new FileSystemResource(file));
        }
        return ResponseEntity.notFound().build();
    }
}