package de.mirkosertic.flightrecorderstarter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.mirkosertic.flightrecorderstarter.actuator.model.FlameGraph;
import de.mirkosertic.flightrecorderstarter.core.FlightRecorder;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

@RestController
@RequestMapping("${flightrecorder.calculated-static-controller-base-path}")
public class FlightRecorderStaticController {

    private final static Logger LOGGER = Logger.getLogger(FlightRecorderStaticController.class.getCanonicalName());
    public static final String DATA_JSON = "/data.json";
    public static final String RAWDATA_JSON = "/rawdata.json";


    private final ApplicationContext applicationContext;
    private final FlightRecorder flightRecorder;

    static final MediaType TEXT_CSS = new MediaType("text", "css");
    static final MediaType TEXT_JAVASCRIPT = new MediaType("text", "javascript");

    static final String D3_V4_MIN_JS = "/d3.v4.min.js";
    static final String D3_FLAMEGRAPH_MIN_JS = "/d3-flamegraph.min.js";
    static final String D3_FLAMEGRAPH_COLOR_MAPPER_MIN_JS = "/d3-flamegraph-colorMapper.min.js";
    static final String D3_FLAMEGRAPH_TOOLTIP_MIN_JS = "/d3-flamegraph-tooltip.min.js";
    static final String D3_FLAMEGRAPH_CSS = "/d3-flamegraph.css";

    static final String RAM_FLAMEGRAPH_HTML = "/rawflamegraph.html";
    static final String FLAMEGRAPH_HTML = "/flamegraph.html";
    static final String RECORDING_ID = "/{recordingId}";

    static final String CACHE_CONTROL_KEY = "Cache-Control";
    static final String CACHE_CONTROL_VALUE = "no-cache, no-store, must-revalidate";
    static final String PRAGMA_KEY = "Pragma";
    static final String PRAGMA_VALUE = "no-cache";
    static final String EXPIRES_KEY = "Expires";
    static final String EXPIRES_VALUE = "0";


    public FlightRecorderStaticController(
            final ApplicationContext applicationContext,
            final FlightRecorder flightRecorder) {
        this.applicationContext = applicationContext;
        this.flightRecorder = flightRecorder;
    }

    @GetMapping(D3_V4_MIN_JS)
    public ResponseEntity downloadRecording2() {
        return ResponseEntity.ok()
                .headers(createHttpHeaders())
                .contentType(TEXT_JAVASCRIPT)
                .body(new ClassPathResource(D3_V4_MIN_JS));
    }

    @GetMapping(D3_FLAMEGRAPH_MIN_JS)
    public ResponseEntity downloadRecording3() {
        return ResponseEntity.ok()
                .headers(createHttpHeaders())
                .contentType(TEXT_JAVASCRIPT)
                .body(new ClassPathResource(D3_FLAMEGRAPH_MIN_JS));

    }

    @GetMapping(D3_FLAMEGRAPH_COLOR_MAPPER_MIN_JS)
    public ResponseEntity downloadRecording4() {
        return ResponseEntity.ok()
                .headers(createHttpHeaders())
                .contentType(TEXT_JAVASCRIPT)
                .body(new ClassPathResource(D3_FLAMEGRAPH_COLOR_MAPPER_MIN_JS));
    }

    @GetMapping(D3_FLAMEGRAPH_TOOLTIP_MIN_JS)
    public ResponseEntity downloadRecording5() {
        return ResponseEntity.ok()
                .headers(createHttpHeaders())
                .contentType(TEXT_JAVASCRIPT)
                .body(new ClassPathResource(D3_FLAMEGRAPH_TOOLTIP_MIN_JS));
    }

    @GetMapping(D3_FLAMEGRAPH_CSS)
    public ResponseEntity downloadRecording6() {
        return ResponseEntity.ok()
                .headers(createHttpHeaders())
                .contentType(TEXT_CSS)
                .body(new ClassPathResource(D3_FLAMEGRAPH_CSS));
    }


    @GetMapping(RECORDING_ID + FLAMEGRAPH_HTML)
    public ResponseEntity downloadRecordingFlameGraph(@PathVariable final long recordingId) {

        return ResponseEntity.ok()
                .headers(createHttpHeaders())
                .contentType(MediaType.TEXT_HTML)
                .body(new ClassPathResource(FLAMEGRAPH_HTML));
    }

    @GetMapping(RECORDING_ID + RAM_FLAMEGRAPH_HTML)
    public ResponseEntity downloadRecordingRawFlameGraph(@PathVariable final long recordingId) {

        return ResponseEntity.ok()
                .headers(createHttpHeaders())
                .contentType(MediaType.TEXT_HTML)
                .body(new ClassPathResource(RAM_FLAMEGRAPH_HTML));
    }

    @GetMapping(RECORDING_ID + DATA_JSON)
    public ResponseEntity downloadRecordingJson(@PathVariable final long recordingId) {

        LOGGER.log(Level.INFO, "Closing recording with ID {0} and downloading file", recordingId);
        final File file = this.flightRecorder.stopRecording(recordingId);

        if (file == null) {
            return ResponseEntity.notFound().build();
        }
        
        final ObjectMapper mapper = new ObjectMapper();
        try {

            final String bootClass = findBootClass(this.applicationContext);
            final FlameGraph graph;
            if (bootClass == null) {
                graph = FlameGraph.from(file);
            } else {
                final int p = bootClass.lastIndexOf(".");
                final String basePackage = bootClass.substring(0, p + 1);
                graph = FlameGraph.from(file, new FlameGraph.PackageNamePrefixFrameFilter(basePackage));
            }
            final String jsonData = mapper.writeValueAsString(graph.getRoot());
            return ResponseEntity.ok()
                    .headers(createHttpHeaders())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonData);

        } catch (final Exception e) {
            LOGGER.log(Level.WARNING, "Could not create json data for flight recording", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    @GetMapping(RECORDING_ID + RAWDATA_JSON)
    public ResponseEntity downloadRecordingRawJson(@PathVariable final long recordingId) {

        LOGGER.log(Level.INFO, "Closing recording with ID {0} and downloading file", recordingId);
        final File file = this.flightRecorder.stopRecording(recordingId);

        if (file == null) {
            return ResponseEntity.notFound().build();
        }

        final ObjectMapper mapper = new ObjectMapper();
        try {
            final FlameGraph graph = FlameGraph.from(file);
            final String jsonData = mapper.writeValueAsString(graph.getRoot());
            return ResponseEntity.ok()
                    .headers(createHttpHeaders())
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(jsonData);

        } catch (final Exception e) {
            LOGGER.log(Level.WARNING, "Could not create json data for flight recording", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(e.getMessage());
        }
    }

    private HttpHeaders createHttpHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(CACHE_CONTROL_KEY, CACHE_CONTROL_VALUE);
        headers.add(PRAGMA_KEY, PRAGMA_VALUE);
        headers.add(EXPIRES_KEY, EXPIRES_VALUE);
        return headers;
    }

    String findBootClass(final ApplicationContext applicationContext) {
        final Map<String, Object> annotatedBeans = applicationContext
                .getBeansWithAnnotation(SpringBootApplication.class);

        if (annotatedBeans.isEmpty()) {
            if (applicationContext.getParent() != null) {
                return findBootClass(applicationContext.getParent());
            } else {
                return null;
            }
        } else {
            return annotatedBeans.values().toArray()[0].getClass().getName();
        }

    }
}
