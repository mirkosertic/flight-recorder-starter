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

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class FlightRecorderStarterApplicationTests {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getRecordingWhenFinished() throws Exception {
		final MvcResult result = mockMvc.perform(put("/actuator/flightrecorder")
						.contentType(MediaType.APPLICATION_JSON)
						.content("{\"duration\": \"5\",\"timeUnit\":\"SECONDS\"}"))
				.andExpect(status().isOk())
				.andReturn();

		Thread.sleep(10_000);

		mockMvc.perform(get("/actuator/flightrecorder/" + result.getResponse().getContentAsString()))
				.andExpect(status().isOk());
	}

	@Test
	void getRecordingWhenNotFinished() throws Exception {
		final MvcResult result = mockMvc.perform(put("/actuator/flightrecorder")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"duration\": \"5\",\"timeUnit\":\"SECONDS\"}"))
				.andExpect(status().isOk())
				.andReturn();

		Thread.sleep(1_000);

		mockMvc.perform(get("/actuator/flightrecorder/" + result.getResponse().getContentAsString()))
				.andExpect(status().isOk());
	}
}
