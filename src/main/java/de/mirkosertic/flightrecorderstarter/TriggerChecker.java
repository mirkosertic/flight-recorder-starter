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
import org.springframework.beans.factory.BeanFactory;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Duration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TriggerChecker {

    private final static Logger LOGGER = Logger.getLogger(TriggerChecker.class.getCanonicalName());

    public static final long UNKNOWN_RECORDING_ID = -1L;

    private static class TriggerSPEL {

        private final Trigger trigger;
        private final Expression expression;

        public TriggerSPEL(final Trigger trigger, final Expression expression) {
            this.trigger = trigger;
            this.expression = expression;
        }
    }

    private final FlightRecorderDynamicConfiguration dynamicConfiguration;
    private final FlightRecorder flightRecorder;
    private final StandardEvaluationContext evaluationContext;
    private final Map<TriggerSPEL, Long> latestRecordings;

    public TriggerChecker(final BeanFactory beanFactory,
                          final FlightRecorderDynamicConfiguration dynamicConfiguration,
                          final FlightRecorder flightRecorder,
                          final MicrometerAdapter micrometerAdapter) {
        this.dynamicConfiguration = dynamicConfiguration;
        this.flightRecorder = flightRecorder;
        this.latestRecordings = new HashMap<>();
        this.evaluationContext = new StandardEvaluationContext(micrometerAdapter);
        this.evaluationContext.setBeanResolver(new BeanFactoryResolver(beanFactory));

        final SpelParserConfiguration config = new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE,
                this.getClass().getClassLoader());
        final ExpressionParser parser = new SpelExpressionParser(config);

        if (dynamicConfiguration.getTrigger() != null) {
            for (final Trigger trigger : dynamicConfiguration.getTrigger()) {
                LOGGER.log(Level.INFO, "Registering trigger {0}", trigger.getExpression());
                final Expression expression = parser.parseExpression(trigger.getExpression());
                this.latestRecordings.put(new TriggerSPEL(trigger, expression), UNKNOWN_RECORDING_ID);
            }
        }
    }

    @Scheduled(fixedDelayString = "${flightrecorder.triggerCheckInterval:10000}")
    public void check() {
        if (this.dynamicConfiguration.isEnabled()) {
            final Set<TriggerSPEL> triggers = new HashSet<>(this.latestRecordings.keySet());
            for (final TriggerSPEL triggerSPEL : triggers) {
                final long latestRecordingId = this.latestRecordings.get(triggerSPEL);
                if (latestRecordingId == UNKNOWN_RECORDING_ID || this.flightRecorder.isRecordingStopped(latestRecordingId)) {
                    try {
                        final Boolean checkResult = triggerSPEL.expression.getValue(this.evaluationContext, Boolean.class);
                        if (checkResult != null && checkResult) {
                            // Triggered
                            if (latestRecordingId != UNKNOWN_RECORDING_ID) {
                                this.flightRecorder.stopRecording(latestRecordingId);
                            }
                            final StartRecordingCommand startRecordingCommand = triggerSPEL.trigger
                                    .getStartRecordingCommand();
                            final long newRecordingId = this.flightRecorder.startRecordingFor(
                                    Duration.of(startRecordingCommand.getDuration(), startRecordingCommand.getTimeUnit()),
                                    triggerSPEL.trigger.getExpression());

                            this.latestRecordings.put(triggerSPEL, newRecordingId);
                        }
                    } catch (final Exception e) {
                        LOGGER.log(Level.WARNING, "Error evaluating trigger {0} : {1}",
                                new Object[]{triggerSPEL.trigger.getExpression(), e.getMessage()});
                    }
                }
            }
        }
    }
}
