package de.mirkosertic.flightrecorderstarter.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${flightrecorder.calculated-static-controller-base-path}")
public class FlightRecorderStaticController {

    static final MediaType TEXT_CSS = new MediaType("text", "css");
    static final MediaType TEXT_JAVASCRIPT = new MediaType("text", "javascript");

    static final String D3_V4_MIN_JS = "/d3.v4.min.js";
    static final String D3_FLAMEGRAPH_MIN_JS = "/d3-flamegraph.min.js";
    static final String D3_FLAMEGRAPH_COLOR_MAPPER_MIN_JS = "/d3-flamegraph-colorMapper.min.js";
    static final String D3_FLAMEGRAPH_TOOLTIP_MIN_JS = "/d3-flamegraph-tooltip.min.js";
    static final String D3_FLAMEGRAPH_CSS = "/d3-flamegraph.css";

    static final String CACHE_CONTROL_KEY = "Cache-Control";
    static final String CACHE_CONTROL_VALUE = "no-cache, no-store, must-revalidate";
    static final String PRAGMA_KEY = "Pragma";
    static final String PRAGMA_VALUE = "no-cache";
    static final String EXPIRES_KEY = "Expires";
    static final String EXPIRES_VALUE = "0";


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

    private HttpHeaders createHttpHeaders() {
        final HttpHeaders headers = new HttpHeaders();
        headers.add(CACHE_CONTROL_KEY, CACHE_CONTROL_VALUE);
        headers.add(PRAGMA_KEY, PRAGMA_VALUE);
        headers.add(EXPIRES_KEY, EXPIRES_VALUE);
        return headers;
    }
}
