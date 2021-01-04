package de.mirkosertic.flightrecorderstarter.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("${flightrecorder.calculated-static-controller-base-path}")
public class FlightRecorderStaticController {

    private final static MediaType TEXT_CSS = new MediaType("text","css");
    private final static MediaType TEXT_JAVASCRIPT = new MediaType("text","javascript");


    @GetMapping("/d3.v4.min.js")
    public @ResponseBody
    ResponseEntity downloadRecording2() {

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok()
            .headers(headers)
            .contentType(TEXT_JAVASCRIPT)
            .body(new ClassPathResource("/d3.v4.min.js"));
    }

    @GetMapping("/d3-flamegraph.min.js")
    public @ResponseBody ResponseEntity downloadRecording3() {

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok()
            .headers(headers)
            .contentType(TEXT_JAVASCRIPT)
            .body(new ClassPathResource("/d3-flamegraph.min.js"));
    }

    @GetMapping("/d3-flamegraph-colorMapper.min.js")
    public @ResponseBody ResponseEntity downloadRecording4() {

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok()
            .headers(headers)
            .contentType(TEXT_JAVASCRIPT)
            .body(new ClassPathResource("/d3-flamegraph-colorMapper.min.js"));
    }

    @GetMapping("/d3-flamegraph-tooltip.min.js")
    public @ResponseBody ResponseEntity downloadRecording5() {

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok()
            .headers(headers)
            .contentType(TEXT_JAVASCRIPT)
            .body(new ClassPathResource("/d3-flamegraph-tooltip.min.js"));
    }

    @GetMapping("/d3-flamegraph.css")
    public @ResponseBody ResponseEntity downloadRecording6() {

        final HttpHeaders headers = new HttpHeaders();
        headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
        headers.add("Pragma", "no-cache");
        headers.add("Expires", "0");

        return ResponseEntity.ok()
            .headers(headers)
            .contentType(TEXT_CSS)
            .body(new ClassPathResource("/d3-flamegraph.css"));
    }
}
