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
package de.mirkosertic.flightrecorderstarter.trigger;

import de.mirkosertic.flightrecorderstarter.fixtures.FlightRecorderStarterApplication;
import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = FlightRecorderStarterApplication.class)
class MicrometerAdapterTest {

    @Autowired
    MeterRegistry meterRegistry;

    @Test
    void queryNotExistingMetric() {
        final MicrometerAdapter adapter = new MicrometerAdapter(this.meterRegistry);
        final double result = adapter.meter("abc.jvm.memory.used")
                .tag("area" , "abc.nonheap")
                .tag("id" , "abc.Metaspace")
                .measurement("value");

        assertEquals(0d, result);
    }

    @Test
    void queryMemoryUsed() {
        final MicrometerAdapter adapter = new MicrometerAdapter(this.meterRegistry);
        final double result = adapter.meter("jvm.memory.used")
                .tag("area" , "nonheap")
                .tag("id" , "Metaspace")
                .measurement("value");

        assertTrue(result > 0);
    }
}