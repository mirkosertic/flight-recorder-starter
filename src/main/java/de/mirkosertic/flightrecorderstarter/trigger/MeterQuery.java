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

import io.micrometer.core.instrument.Measurement;
import io.micrometer.core.instrument.Meter;
import io.micrometer.core.instrument.search.MeterNotFoundException;
import io.micrometer.core.instrument.search.RequiredSearch;

import java.util.logging.Level;
import java.util.logging.Logger;

public class MeterQuery {

    private final static Logger LOGGER = Logger.getLogger(MeterQuery.class.getCanonicalName());

    private final RequiredSearch requiredSearch;

    public MeterQuery(final RequiredSearch requiredSearch) {
        this.requiredSearch = requiredSearch;
    }

    public MeterQuery tag(final String tagKey, final String tagValue) {
        this.requiredSearch.tag(tagKey, tagValue);
        return this;
    }

    public MeterQuery tag(final String tagKey) {
        this.requiredSearch.tagKeys(tagKey);
        return this;
    }

    public double measurement(final String statisticsName) {
        try {
            final Meter meter = this.requiredSearch.meter();
            for (final Measurement m : meter.measure()) {
                if (m.getStatistic().getTagValueRepresentation().equals(statisticsName)) {
                    return m.getValue();
                }
            }
            LOGGER.log(Level.FINE, "No measurement with name {} found", statisticsName);
            return 0d;
        } catch (final MeterNotFoundException e) {
            LOGGER.log(Level.FINE, "No meter found", e);
            return 0d;
        }
    }
}
