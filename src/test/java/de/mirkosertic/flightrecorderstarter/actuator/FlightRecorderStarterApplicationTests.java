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
import de.mirkosertic.flightrecorderstarter.fixtures.FlightRecorderStarterApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import tools.jackson.databind.ObjectMapper;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(classes = FlightRecorderStarterApplication.class)
@AutoConfigureMockMvc
class FlightRecorderStarterApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void getRecordingAndCheckStatus() throws Exception {
        this.mockMvc.perform(post("/actuator/flightrecorder")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"duration\": \"3\",\"timeUnit\":\"Seconds\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        Thread.sleep(1_000);

        final MvcResult status = this.mockMvc.perform(get("/actuator/flightrecorder")).andExpect(status().isOk())
                .andReturn();
        final FlightRecorderPublicSession[] sessions = this.objectMapper
                .readValue(status.getResponse().getContentAsString(), FlightRecorderPublicSession[].class);

        assertTrue(sessions.length > 0);

        this.mockMvc.perform(get("/actuator/flightrecorder/ui/d3.v4.min.js")).andExpect(status().isOk());
    }

    @Test
    void getRecordingWhenFinished() throws Exception {
        final MvcResult result = this.mockMvc.perform(post("/actuator/flightrecorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"duration\": \"3\",\"timeUnit\":\"Seconds\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        Thread.sleep(5_000);

        final String redirectTo = result.getResponse().getHeader("Location");
        final String recordingId = redirectTo.substring(redirectTo.lastIndexOf("/") + 1);

        this.mockMvc.perform(get("/actuator/flightrecorder/" + recordingId))
                .andExpect(status().isOk());
    }

    @Test
    void getRecordingWhenNotFinished() throws Exception {
        final MvcResult result = this.mockMvc.perform(post("/actuator/flightrecorder")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"duration\": \"3\",\"timeUnit\":\"Seconds\"}"))
                .andExpect(status().isCreated())
                .andReturn();

        Thread.sleep(1_000);

        final String redirectTo = result.getResponse().getHeader("Location");
        final String recordingId = redirectTo.substring(redirectTo.lastIndexOf("/") + 1);

        this.mockMvc.perform(get("/actuator/flightrecorder/" + recordingId))
                .andExpect(status().isOk());
    }


}
