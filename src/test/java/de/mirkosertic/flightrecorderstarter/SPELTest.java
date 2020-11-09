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

import io.micrometer.core.instrument.MeterRegistry;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
public class SPELTest {

    @Autowired
    MeterRegistry meterRegistry;

    @Test
    void testEvaluate() {

        final MicrometerAdapter adapter = new MicrometerAdapter(meterRegistry);
        final SpelParserConfiguration config = new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, this.getClass().getClassLoader());
        final ExpressionParser parser = new SpelExpressionParser(config);

        final StandardEvaluationContext context = new StandardEvaluationContext(adapter);

        final Expression exp = parser.parseExpression("meter('jvm.memory.used').tag('area','nonheap').tag('id','Metaspace').value('value')");
        final Optional<Double> value = exp.getValue(context, Optional.class);

        assertNotNull(value);
        assertTrue(value.isPresent());
        assertTrue(value.get() > 0);
    }
}
