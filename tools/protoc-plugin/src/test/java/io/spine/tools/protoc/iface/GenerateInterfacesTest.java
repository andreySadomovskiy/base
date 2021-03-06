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

package io.spine.tools.protoc.iface;

import io.spine.tools.protoc.ConfigByPattern;
import io.spine.tools.protoc.FilePattern;
import io.spine.tools.protoc.FilePatterns;
import io.spine.tools.protoc.given.TestInterface;
import io.spine.tools.protoc.method.OuterMessage;
import io.spine.type.MessageType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static com.google.common.truth.Truth.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("GenerateInterfaces should")
final class GenerateInterfacesTest {

    @DisplayName("throw NullPointerException if")
    @Nested
    class ThrowNpe {

        @DisplayName("is created with `null` arguments")
        @Test
        void isCreatedWithNullArguments() {
            assertThrows(NullPointerException.class, () ->
                    new GenerateInterfaces(null));
        }

        @DisplayName("`null` MessageType is supplied")
        @Test
        void nullMessageTypeIsSupplied() {
            ConfigByPattern config = newTaskConfig("test")
                    .setPattern(FilePatterns.filePrefix("non-default"))
                    .build();
            GenerateInterfaces generateMethods = new GenerateInterfaces(config);
            assertThrows(NullPointerException.class, () -> generateMethods.generateFor(null));
        }
    }

    @DisplayName("throw IllegalStateException if FilePattern is not set")
    @Test
    void throwIllegalStateExceptionIfFilePatternIsNotSet() {
        assertThrows(IllegalStateException.class, () ->
                newTask(newTaskConfig("not-empty-name").build()));
    }

    @DisplayName("throw IllegalArgumentException if interface name is")
    @ParameterizedTest(name = "\"{0}\"")
    @ValueSource(strings = {"", "  "})
    void throwIllegalArgumentException(String interfaceName) {
        assertThrows(IllegalArgumentException.class, () ->
                newTask(newTaskConfig(interfaceName).build()));
    }

    @DisplayName("generate empty result if")
    @Nested
    class GenerateEmptyResult {

        @DisplayName("message does not match pattern")
        @Test
        void messageDoesNotMatchPattern() {
            assertEmptyResult(TestInterface.class.getName(), FilePatterns.fileRegex("wrong"));
        }

        @DisplayName("message is not top level")
        @Test
        void messageIsNotTopLevel() {
            assertEmptyResult(TestInterface.class.getName(),
                              FilePatterns.fileSuffix("inner_messages.proto"),
                              new MessageType(OuterMessage.InnerMessage.getDescriptor()));
        }

        private void assertEmptyResult(String interfaceName, FilePattern filePattern) {
            assertEmptyResult(interfaceName, filePattern,
                              new MessageType(ProjectCreated.getDescriptor()));
        }

        private void
        assertEmptyResult(String interfaceName, FilePattern filePattern, MessageType type) {
            ConfigByPattern config = newTaskConfig(interfaceName)
                    .setPattern(filePattern)
                    .build();
            assertThat(newTask(config).generateFor(type))
                    .isEmpty();
        }
    }

    @DisplayName("implement interface")
    @Test
    void implementInterface() {
        ConfigByPattern config = newTaskConfig(TestInterface.class.getName())
                .setPattern(FilePatterns.fileSuffix("test_events.proto"))
                .build();
        assertThat(newTask(config).generateFor(new MessageType(ProjectCreated.getDescriptor())))
                .isNotEmpty();
    }

    private static GenerateInterfaces newTask(ConfigByPattern config) {
        return new GenerateInterfaces(config);
    }

    private static ConfigByPattern.Builder newTaskConfig(String interfaceName) {
        return ConfigByPattern.newBuilder()
                              .setValue(interfaceName);
    }
}
