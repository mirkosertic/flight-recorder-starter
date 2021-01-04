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

import de.mirkosertic.flightrecorderstarter.fixtures.FlightRecorderStarterApplication;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.io.IOException;
import java.time.Duration;
import java.time.temporal.ChronoUnit;
import java.util.List;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@SpringBootTest(classes = FlightRecorderStarterApplication.class)
class TriggerCheckerTest {

    @Autowired
    BeanFactory beanFactory;

    @Autowired
    MicrometerAdapter micrometerAdapter;

    @MockBean
    FlightRecorder flightRecorder;

    @Test
    void checkTwiceButOnlyOneRecording() throws IOException {

        final FlightRecorderDynamicConfiguration configuration = new FlightRecorderDynamicConfiguration();
        configuration.setEnabled(true);

        final StartRecordingCommand startRecordingCommand = new StartRecordingCommand();
        startRecordingCommand.setDuration(10);
        startRecordingCommand.setTimeUnit(ChronoUnit.SECONDS);

        final Trigger trigger = new Trigger();
        trigger.setStartRecordingCommand(startRecordingCommand);
        trigger.setExpression("meter('jvm.memory.used').tag('area','nonheap').tag('id','Metaspace').measurement('value') > 100");

        configuration.setTrigger(List.of(trigger));

        final TriggerChecker checker = new TriggerChecker(beanFactory, configuration, flightRecorder, micrometerAdapter);

        when(flightRecorder.startRecordingFor(eq(Duration.of(10L, ChronoUnit.SECONDS)), eq(trigger.getExpression()))).thenReturn(20L);
        when(flightRecorder.isRecordingStopped(eq(20L))).thenReturn(false);
        checker.check();
        checker.check();

        verify(flightRecorder, times(1)).isRecordingStopped(eq(20L));
        verify(flightRecorder, times(1)).startRecordingFor(eq(Duration.of(10, ChronoUnit.SECONDS)), eq(trigger.getExpression()));
    }

    @Test
    void checkTwice() throws IOException {

        final FlightRecorderDynamicConfiguration configuration = new FlightRecorderDynamicConfiguration();
        configuration.setEnabled(true);

        final StartRecordingCommand startRecordingCommand = new StartRecordingCommand();
        startRecordingCommand.setDuration(10);
        startRecordingCommand.setTimeUnit(ChronoUnit.SECONDS);

        final Trigger trigger = new Trigger();
        trigger.setStartRecordingCommand(startRecordingCommand);
        trigger.setExpression("meter('jvm.memory.used').tag('area','nonheap').tag('id','Metaspace').measurement('value') > 100");

        configuration.setTrigger(List.of(trigger));

        final TriggerChecker checker = new TriggerChecker(beanFactory, configuration, flightRecorder, micrometerAdapter);

        when(flightRecorder.startRecordingFor(eq(Duration.of(10L, ChronoUnit.SECONDS)), eq(trigger.getExpression()))).thenReturn(20L);
        when(flightRecorder.isRecordingStopped(eq(20L))).thenReturn(true);
        checker.check();
        checker.check();

        verify(flightRecorder, times(1)).isRecordingStopped(eq(20L));
        verify(flightRecorder, times(2)).startRecordingFor(eq(Duration.of(10, ChronoUnit.SECONDS)), eq(trigger.getExpression()));
    }

    @Test
    void checkTwiceDisabled() {

        final FlightRecorderDynamicConfiguration configuration = new FlightRecorderDynamicConfiguration();
        configuration.setEnabled(false);

        final StartRecordingCommand startRecordingCommand = new StartRecordingCommand();
        startRecordingCommand.setDuration(10);
        startRecordingCommand.setTimeUnit(ChronoUnit.SECONDS);

        final Trigger trigger = new Trigger();
        trigger.setStartRecordingCommand(startRecordingCommand);
        trigger.setExpression("meter('jvm.memory.used').tag('area','nonheap').tag('id','Metaspace').measurement('value') > 100");

        configuration.setTrigger(List.of(trigger));

        final TriggerChecker checker = new TriggerChecker(beanFactory, configuration, flightRecorder, micrometerAdapter);

        checker.check();
        checker.check();

        verifyNoInteractions(flightRecorder);
    }

    @Test
    void checkEnabledEmptytriggers() {

        final FlightRecorderDynamicConfiguration configuration = new FlightRecorderDynamicConfiguration();
        configuration.setEnabled(true);

        final StartRecordingCommand startRecordingCommand = new StartRecordingCommand();
        startRecordingCommand.setDuration(10);
        startRecordingCommand.setTimeUnit(ChronoUnit.SECONDS);

        final TriggerChecker checker = new TriggerChecker(beanFactory, configuration, flightRecorder, micrometerAdapter);

        checker.check();
        checker.check();

        verifyNoInteractions(flightRecorder);
    }
}