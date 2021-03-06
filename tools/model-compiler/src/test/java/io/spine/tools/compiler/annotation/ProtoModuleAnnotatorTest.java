/*
 * Copyright 2019, TeamDev. All rights reserved.
 *
 * Redistribution and use in source and/or binary forms, with or without
 * modification, must retain the above copyright notice and the following
 * disclaimer.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package io.spine.tools.compiler.annotation;

import com.google.common.collect.ImmutableSet;
import io.spine.annotation.Internal;
import io.spine.code.java.ClassName;
import io.spine.tools.compiler.annotation.given.FakeAnnotator;
import org.checkerframework.checker.regex.qual.Regex;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.tools.compiler.annotation.ClassNamePattern.compile;
import static io.spine.tools.compiler.annotation.MethodPattern.exactly;
import static io.spine.tools.compiler.annotation.ModuleAnnotator.translate;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("ModuleAnnotator should")
class ProtoModuleAnnotatorTest {

    private static final ClassName ANNOTATION = ClassName.of(Internal.class);
    private static final ApiOption OPTION = ApiOption.internal();

    @Test
    @DisplayName("annotate by Protobuf option")
    void annotateByOption() {
        checkAnnotateByOption(OPTION);
    }

    @Test
    @DisplayName("annotate by Protobuf option which does not support fields")
    void annotateByNonFieldOption() {
        checkAnnotateByOption(ApiOption.spi());
    }

    @Test
    @DisplayName("annotate by class name pattern")
    void annotateByClassPattern() {
        @Regex String classNamePattern = ".+OrBuilder";
        FakeAnnotator.Factory factory = new FakeAnnotator.Factory();
        ModuleAnnotator annotator = ModuleAnnotator
                .newBuilder()
                .setInternalPatterns(ImmutableSet.of(classNamePattern))
                .setAnnotatorFactory(factory)
                .setInternalAnnotation(ANNOTATION)
                .build();
        annotator.annotate();
        assertEquals(ANNOTATION, factory.getAnnotationName());
        assertEquals(compile(classNamePattern), factory.getClassNamePattern());
    }

    @Test
    @DisplayName("annotate by method name pattern")
    void annotateByMethodPattern() {
        String methodName = "setInternalValue";
        FakeAnnotator.Factory factory = new FakeAnnotator.Factory();
        ModuleAnnotator annotator = ModuleAnnotator
                .newBuilder()
                .setInternalMethodNames(ImmutableSet.of(methodName))
                .setAnnotatorFactory(factory)
                .setInternalAnnotation(ANNOTATION)
                .build();
        annotator.annotate();
        assertEquals(ANNOTATION, factory.getAnnotationName());
        assertEquals(ImmutableSet.of(exactly(methodName)), factory.getMethodPatterns());
    }

    private static void checkAnnotateByOption(ApiOption option) {
        FakeAnnotator.Factory factory = new FakeAnnotator.Factory();
        ModuleAnnotator.Job optionJob = translate(option).as(ANNOTATION);
        ModuleAnnotator annotator = ModuleAnnotator
                .newBuilder()
                .add(optionJob)
                .setAnnotatorFactory(factory)
                .setInternalAnnotation(ANNOTATION)
                .build();
        annotator.annotate();
        assertEquals(ANNOTATION, factory.getAnnotationName());
        assertEquals(option, factory.getOption());
    }
}
