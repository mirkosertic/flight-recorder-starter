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

import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import java.lang.management.ManagementFactory;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class FlightRecorder {

    private final static Logger logger = Logger.getLogger(FlightRecorder.class.getCanonicalName());

    private static MBeanServer SERVER;
    private static ObjectName JFRNAME;
    private static MBeanOperationInfo NEW_RECORDING;
    private static MBeanOperationInfo START_RECORDING;
    private static MBeanOperationInfo STOP_RECORDING;
    private static MBeanOperationInfo CLOSE_RECORDING;
    private static MBeanOperationInfo SET_RECORDING_OPTIONS;
    static {
        try {
            SERVER = ManagementFactory.getPlatformMBeanServer();
            JFRNAME = new ObjectName("jdk.management.jfr:type=FlightRecorder");
            if (SERVER.isRegistered(JFRNAME)) {
                final MBeanInfo info = SERVER.getMBeanInfo(JFRNAME);
                for (final MBeanOperationInfo op : info.getOperations()) {
                    if ("newRecording".equals(op.getName())) {
                        logger.info("newRecording Operation found");
                        NEW_RECORDING = op;
                    }
                    if ("startRecording".equals(op.getName())) {
                        logger.info("startRecording Operation found");
                        START_RECORDING = op;
                    }
                    if ("stopRecording".equals(op.getName())) {
                        logger.info("stopRecording Operation found");
                        STOP_RECORDING = op;
                    }
                    if ("closeRecording".equals(op.getName())) {
                        logger.info("closeRecording Operation found");
                        CLOSE_RECORDING = op;
                    }
                    if ("setRecordingOptions".equals(op.getName())) {
                        logger.info("setRecordingOptions Operation found");
                        SET_RECORDING_OPTIONS = op;
                    }
                }
            } else {
                logger.log(Level.WARNING, "No FlightRecorder MBean registered");
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Could not connect to FlightRecorder JMX.", e);
        }
    }

    private static String[] getSignature(MBeanOperationInfo op) {
        MBeanParameterInfo[] pi = op.getSignature();
        String[] types = new String[pi.length];
        for (int i = 0; i < pi.length; i++) {
            types[i] = pi[i].getType();
        }
        return types;
    }

    public static long newRecording() {
        if (NEW_RECORDING != null) {
            try {
                return (long) SERVER.invoke(JFRNAME, NEW_RECORDING.getName(), new Object[] {}, getSignature(NEW_RECORDING));
            } catch (Exception e) {
                logger.log(Level.WARNING, "Cannot invoke operation", e);
            }
        }
        return -1;
    }

    public static long startRecording(final long recordingId) {
        if (START_RECORDING != null) {
            try {
                SERVER.invoke(JFRNAME, START_RECORDING.getName(), new Object[] {recordingId}, getSignature(START_RECORDING));
            } catch (Exception e) {
                logger.log(Level.WARNING, "Cannot invoke operation", e);
            }
        }
        return -1;
    }

    public static void setRecordingOptions(final long recordingId, final long duration, final String filename) {
        if (SET_RECORDING_OPTIONS != null) {
            try {
                final CompositeType rowType = new CompositeType("row", "row", new String[] {"key","value"},
                        new String[] {"key", "value"} , new OpenType[] {SimpleType.STRING, SimpleType.STRING});
                final TabularType configType = new TabularType("configuration", "configuration", rowType, new String[] { "key" });
                final TabularDataSupport configuration = new TabularDataSupport(configType);
                configuration.put(new CompositeDataSupport(rowType, Map.of("key", "duration", "value", duration + "s")));
                configuration.put(new CompositeDataSupport(rowType, Map.of("key", "destination", "value", filename)));
                SERVER.invoke(JFRNAME, SET_RECORDING_OPTIONS.getName(), new Object[] {recordingId, configuration}, getSignature(SET_RECORDING_OPTIONS));
            } catch (Exception e) {
                logger.log(Level.WARNING, "Cannot invoke operation", e);
            }
        }
    }

    public static void recordFor(final int durationInSeconds, final String fileName) {
        final long recordingId = newRecording();
        setRecordingOptions(recordingId, durationInSeconds, fileName);
        startRecording(recordingId);
    }
}