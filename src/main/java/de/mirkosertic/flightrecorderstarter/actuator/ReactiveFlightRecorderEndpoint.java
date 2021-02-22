package de.mirkosertic.flightrecorderstarter.actuator;

import de.mirkosertic.flightrecorderstarter.actuator.model.FlightRecorderPublicSession;
import de.mirkosertic.flightrecorderstarter.core.FlightRecorder;
import de.mirkosertic.flightrecorderstarter.core.StartRecordingCommand;
import org.springframework.boot.actuate.endpoint.annotation.Selector;
import org.springframework.boot.actuate.endpoint.web.annotation.RestControllerEndpoint;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.util.UriComponentsBuilder;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The type Reactive flight recorder endpoint.
 */
@RestControllerEndpoint(id = "flightrecorder")
public class ReactiveFlightRecorderEndpoint {

    private final static Logger LOGGER = Logger.getLogger(ReactiveFlightRecorderEndpoint.class.getCanonicalName());


    private final FlightRecorder flightRecorder;

    /**
     * Instantiates a new Reactive flight recorder endpoint.
     *
     * @param flightRecorder the flight recorder
     */
    public ReactiveFlightRecorderEndpoint(
            final FlightRecorder flightRecorder) {
        this.flightRecorder = flightRecorder;
    }


    /**
     * All sessions flux.
     *
     * @return the flux
     */
    @GetMapping("/")
    public Flux<FlightRecorderPublicSession> allSessions() {
        return Flux.defer(() -> {
            LOGGER.info("Retrieving all known recording sessions");
            return Flux.fromIterable(this.flightRecorder.sessions());
        }).doOnError(e -> ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage()));
    }

    /**
     * Start recording mono.
     *
     * @param commandInput      the command input
     * @param serverWebExchange the server web exchange
     * @return the mono
     */
    @PostMapping("/")
    public Mono<ResponseEntity<?>> startRecording(@RequestBody final Mono<StartRecordingCommand> commandInput,
                                                  final ServerWebExchange serverWebExchange) {

        return commandInput.filter(command -> command.getDuration() != null && command.getTimeUnit() != null)
                .doOnNext(c -> LOGGER.log(Level.INFO, "Trying to start recording for {0} {1}",
                        new Object[]{c.getDuration(), c.getTimeUnit()}))
                .map(command -> {
                    Long recordingId = null;
                    try {
                        recordingId = this.flightRecorder.startRecordingFor(command);
                    } catch (final IOException ioe) {

                        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(ioe.getMessage());
                    }

                    LOGGER.log(Level.INFO, "Created recording with ID {0}", recordingId);
                    return ResponseEntity
                            .created(UriComponentsBuilder.fromUri(serverWebExchange.getRequest().getURI())
                                    .path("/{id}")
                                    .build(recordingId))
                            .build();
                })
                .switchIfEmpty(Mono.just(ResponseEntity.badRequest().body("Duration and TimeUnit cannot be null")));
    }

    /**
     * Stop recording mono.
     *
     * @param recordingId the recording id param
     * @return the mono
     */
    @PutMapping("/{recordingId}")
    public Mono<ResponseEntity<?>> stopRecording(@Selector @PathVariable final Long recordingId) {
        return Mono.just(recordingId).map(recordingIdMap -> {
            LOGGER.log(Level.INFO, "Stopping recording with ID {0}", recordingIdMap);
            final File file = this.flightRecorder.stopRecording(recordingIdMap);
            if (file != null) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(this.flightRecorder.getById(recordingIdMap));
            }
            return ResponseEntity.notFound().build();
        });

    }

    /**
     * Delete recording mono.
     *
     * @param recordingId the recording id param
     * @return the mono
     */
    @DeleteMapping("/{recordingId}")
    public Mono<ResponseEntity<?>> deleteRecording(@Selector @PathVariable final Long recordingId) {
        return Mono.just(recordingId).map(recordingIdMap -> {
            try {
                LOGGER.log(Level.INFO, "Deleting recording with ID {0}", recordingIdMap);

                final File file = this.flightRecorder.stopRecording(recordingIdMap);
                if (file != null) {
                    this.flightRecorder.deleteRecording(recordingIdMap);

                    return ResponseEntity.noContent().build();
                }
                return ResponseEntity.notFound().build();


            } catch (final Exception e) {
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
            }
        });

    }

    /**
     * Download recording mono.
     *
     * @param recordingId the recording id param
     * @return the mono
     */
    @GetMapping("/{recordingId}")
    public Mono<ResponseEntity<?>> downloadRecording(
            @Selector @PathVariable final Long recordingId) {
        return Mono.just(recordingId)
                .map(recordingIdMap -> {
                    LOGGER.log(Level.INFO, "Closing recording with ID {0} and downloading file", recordingIdMap);
                    final File file = this.flightRecorder.stopRecording(recordingIdMap);
                    if (file != null) {
                        // final HttpHeaders headers = new HttpHeaders();

                        return ResponseEntity.ok()
                                .headers(headers -> {
                                    headers
                                            .add(HttpHeaders.CONTENT_DISPOSITION,
                                                    "attachment; filename=flightrecording_" + recordingIdMap +
                                                            ".jfr");
                                    headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
                                    headers.add("Pragma", "no-cache");
                                    headers.add("Expires", "0");
                                })
                                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                                .body(new FileSystemResource(file));
                    }
                    return ResponseEntity.notFound().build();
                });

    }

}
