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

package io.spine.tools.gradle.compiler.protoc;

import io.spine.tools.protoc.AddMethods;
import io.spine.tools.protoc.ConfigByPattern;
import io.spine.tools.protoc.FilePattern;
import io.spine.tools.protoc.UuidConfig;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Predicate;

import static com.google.common.truth.Truth.assertThat;
import static io.spine.tools.gradle.compiler.protoc.MessageSelectorFactory.prefix;
import static io.spine.tools.gradle.compiler.protoc.MessageSelectorFactory.regex;
import static io.spine.tools.gradle.compiler.protoc.MessageSelectorFactory.suffix;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DisplayName("GeneratedMethods should")
final class GeneratedMethodsTest {

    @DisplayName("convert to proper Protoc configuration")
    @Test
    void convertToProperProtocConfiguration() {
        String testMethodFactory = "io.spine.test.MethodFactory";
        GeneratedMethods methods = new GeneratedMethods();
        MessageSelectorFactory messages = methods.messages();
        methods.applyFactory(testMethodFactory, messages.uuid());
        methods.applyFactory(testMethodFactory, messages.inFiles(suffix("_test.proto")));
        AddMethods config = methods.asProtocConfig();

        assertEquals(testMethodFactory, config.getUuidFactory()
                                              .getValue());
        assertEquals(testMethodFactory, config.getFactoryByPattern(0)
                                              .getValue());
    }

    @DisplayName("add multiple file patterns")
    @Test
    void addMultipleFilePatterns() {
        String pattern = "testPattern";
        String interfaceName = "io.spine.test.TestInterface";

        GeneratedMethods defaults = new GeneratedMethods();
        MessageSelectorFactory messages = defaults.messages();
        defaults.applyFactory(interfaceName, messages.inFiles(suffix(pattern)));
        defaults.applyFactory(interfaceName, messages.inFiles(prefix(pattern)));
        defaults.applyFactory(interfaceName, messages.inFiles(regex(pattern)));

        assertTrue(hasSuffixConfig(pattern, interfaceName, defaults.asProtocConfig()));
        assertTrue(hasPrefixConfig(pattern, interfaceName, defaults.asProtocConfig()));
        assertTrue(hasRegexConfig(pattern, interfaceName, defaults.asProtocConfig()));
    }

    private static boolean
    hasSuffixConfig(String suffix, String factoryName, AddMethods config) {
        return hasConfig(config, factoryName, pattern -> suffix.equals(pattern.getSuffix()));
    }

    private static boolean
    hasPrefixConfig(String prefix, String factoryName, AddMethods config) {
        return hasConfig(config, factoryName, pattern -> prefix.equals(pattern.getPrefix()));
    }

    private static boolean
    hasRegexConfig(String regex, String factoryName, AddMethods config) {
        return hasConfig(config, factoryName, pattern -> regex.equals(pattern.getRegex()));
    }

    private static boolean hasConfig(AddMethods config,
                                     String factoryName,
                                     Predicate<? super FilePattern> patternPredicate) {
        return config
                .getFactoryByPatternList()
                .stream()
                .filter(byPattern -> factoryName.equals(byPattern.getValue()))
                .map(ConfigByPattern::getPattern)
                .anyMatch(patternPredicate);
    }
}
