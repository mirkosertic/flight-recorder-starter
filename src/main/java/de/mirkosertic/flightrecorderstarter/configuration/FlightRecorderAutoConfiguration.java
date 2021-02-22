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
package de.mirkosertic.flightrecorderstarter.configuration;

import de.mirkosertic.flightrecorderstarter.core.FlightRecorder;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;

@Configuration
@ConditionalOnProperty(prefix = "flightrecorder", name = "enabled", havingValue = "true", matchIfMissing = true)
@ConditionalOnWebApplication
@PropertySource("classpath:flight-recorder.properties")
@ImportAutoConfiguration(TriggerConfiguration.class)
@Import(value = {WebMvcFlightRecorderConfiguration.class, WebFluxFlightRecorderConfiguration.class})
@EnableConfigurationProperties(FlightRecorderDynamicConfiguration.class)
public class FlightRecorderAutoConfiguration {

    @Bean
    public FlightRecorder flightRecorder(final FlightRecorderDynamicConfiguration configuration) {
        return new FlightRecorder(configuration);
    }

}