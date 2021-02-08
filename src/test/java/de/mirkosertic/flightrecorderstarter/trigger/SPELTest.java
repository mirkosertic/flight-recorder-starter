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
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.expression.BeanFactoryResolver;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.SpelCompilerMode;
import org.springframework.expression.spel.SpelParserConfiguration;
import org.springframework.expression.spel.standard.SpelExpressionParser;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(classes = FlightRecorderStarterApplication.class)
public class SPELTest {

    @Autowired
    BeanFactory beanFactory;

    @Autowired
    MeterRegistry meterRegistry;

    @Test
    void testEvaluate() {

        final MicrometerAdapter adapter = new MicrometerAdapter(this.meterRegistry);
        final SpelParserConfiguration config = new SpelParserConfiguration(SpelCompilerMode.IMMEDIATE, this.getClass().getClassLoader());
        final ExpressionParser parser = new SpelExpressionParser(config);

        final StandardEvaluationContext context = new StandardEvaluationContext(adapter);
        context.setBeanResolver(new BeanFactoryResolver(this.beanFactory));

        final Expression exp = parser.parseExpression("meter('jvm.memory.used').tag('area','nonheap').tag('id','Metaspace').measurement('value')");
        final Double value = exp.getValue(context, Double.class);

        assertNotNull(value);
        assertTrue(value > 0);
    }
}
