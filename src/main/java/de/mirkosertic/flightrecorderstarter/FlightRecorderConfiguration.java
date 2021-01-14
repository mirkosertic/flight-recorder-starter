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

import de.mirkosertic.flightrecorderstarter.core.FlightRecorder;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@ConditionalOnProperty(prefix = "flightrecorder", name = "enabled", havingValue = "true", matchIfMissing = true)
@Configuration
public class FlightRecorderConfiguration {

    @Bean
    public FlightRecorder flightRecorder(final FlightRecorderDynamicConfiguration configuration) {
        return new FlightRecorder(configuration);
    }

    @Bean
    public FlightRecorderEndpoint flightRecorderEndpoint(final ApplicationContext applicationContext,
                                                         final FlightRecorder flightRecorder) {
        return new FlightRecorderEndpoint(applicationContext, flightRecorder);
    }

    @Bean
    public MicrometerAdapter micrometerAdapter(final MeterRegistry meterRegistry) {
        return new MicrometerAdapter(meterRegistry);
    }

    @Bean
    public TriggerChecker triggerChecker(final BeanFactory beanFactory,
                                         final FlightRecorderDynamicConfiguration dynamicConfiguration,
                                         final FlightRecorder flightRecorder,
                                         final MicrometerAdapter micrometerAdapter) {
        return new TriggerChecker(beanFactory, dynamicConfiguration, flightRecorder, micrometerAdapter);
    }
}