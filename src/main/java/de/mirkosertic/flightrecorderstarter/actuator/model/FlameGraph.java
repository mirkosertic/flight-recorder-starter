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
package de.mirkosertic.flightrecorderstarter.actuator.model;

import jdk.jfr.consumer.RecordedEvent;
import jdk.jfr.consumer.RecordedFrame;
import jdk.jfr.consumer.RecordedStackTrace;
import jdk.jfr.consumer.RecordingFile;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class FlameGraph {

    public static class Node {
        private String name;
        private int value;
        private final Map<String, Node> children;

        private Node() {
            this.children = new HashMap<>();
        }

        public Node(final String name) {
            this.name = name;
            this.value = 0;
            this.children = new HashMap<>();
        }

        public Node childByName(final String nameOf) {
            final Node node = this.children.computeIfAbsent(nameOf, Node::new);
            node.value++;
            return node;
        }

        public String getName() {
            return this.name;
        }

        public int getValue() {
            return this.value;
        }

        public List<Node> getChildren() {
            final List<Node> nodes = new ArrayList<>(this.children.values());
            nodes.sort(Comparator.comparingInt(o -> o.value));
            return nodes;
        }
    }

    @FunctionalInterface
    public interface FrameFilter {

        boolean includes(final String className);

    }

    public static class PackageNamePrefixFrameFilter implements FrameFilter {
        private final String prefix;

        public PackageNamePrefixFrameFilter(final String prefix) {
            this.prefix = prefix;
        }

        @Override
        public boolean includes(final String className) {
            return className.startsWith(this.prefix)
                    && !className.contains("$$FastClassBySpringCGLIB$$")
                    && !className.contains("$$EnhancerBySpringCGLIB$$");
        }
    }

    public final static FrameFilter ALL = className -> true;

    private final Node root;

    private FlameGraph(final Node root) {
        this.root = root;
    }

    public static FlameGraph from(final File file, final FrameFilter frameFilter) throws IOException {
        final Node rootNode = new Node("Recording");
        try (final RecordingFile rf = new RecordingFile(file.toPath())) {
            while (rf.hasMoreEvents()) {
                final RecordedEvent event = rf.readEvent();
                if ("jdk.ExecutionSample".equals(event.getEventType().getName())) {
                    final RecordedStackTrace stackTrace = event.getStackTrace();
                    final List<RecordedFrame> frames = stackTrace.getFrames();

                    Node currentNode = rootNode;
                    for (int i = frames.size() - 1; i >= 0; i--) {
                        final RecordedFrame frame = frames.get(i);
                        if (frame.isJavaFrame() && frameFilter.includes(frame.getMethod().getType().getName())) {
                            currentNode = currentNode.childByName(nameOf(frame));
                        }
                    }
                }
            }
        }
        return new FlameGraph(rootNode);
    }

    public static FlameGraph from(final File file) throws IOException {
        return from(file, ALL);
    }

    public Node getRoot() {
        return this.root;
    }

    private static String nameOf(final RecordedFrame frame) {
        final String className = frame.getMethod().getType().getName();
        final String methodName = frame.getMethod().getName();
        final String descriptor = frame.getMethod().getDescriptor();
        final int lineNumber = frame.getLineNumber();

        return className + "." + methodName;
    }
}
