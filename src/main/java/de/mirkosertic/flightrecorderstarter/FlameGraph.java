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

import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedFrame;
import jdk.jfr.consumer.RecordedStackTrace;
import jdk.jfr.consumer.RecordingFile;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FlameGraph {

    public static FlameGraph from(final File file) throws IOException {
        try (final RecordingFile rf = new RecordingFile(file.toPath())) {
            while (rf.hasMoreEvents()) {
                final RecordedEvent event = rf.readEvent();
                if ("jdk.ExecutionSample".equals(event.getEventType().getName())) {
                    final RecordedStackTrace stackTrace = event.getStackTrace();
                    final List<RecordedFrame> frames = stackTrace.getFrames();
                    for (RecordedFrame f : frames) {
                        if (f.isJavaFrame()) {
 //                           System.out.println(f.getMethod().getType().getName() + "." + f.getMethod().getName() + ":" + f.getLineNumber() + " -> " + f.getMethod().getDescriptor());
                        }
                    }
                }
            }
        }
        return null;
    }
}
