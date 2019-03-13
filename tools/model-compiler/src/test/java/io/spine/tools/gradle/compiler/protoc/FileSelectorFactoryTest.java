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

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

@DisplayName("FileSelectorFactory should")
final class FileSelectorFactoryTest {

    private final FileSelectorFactory factory = FileSelectorFactory.INSTANCE;

    @DisplayName("create")
    @Nested
    class Create {

        @DisplayName("prefix pattern")
        @Test
        void prefix() {
            assertNotNull(factory.startsWith("io/spine/test_"));
        }

        @DisplayName("postfix pattern")
        @Test
        void postfix() {
            assertNotNull(factory.endsWith("test.proto"));
        }

        @DisplayName("matches pattern")
        @Test
        void regex() {
            assertNotNull(factory.matches(".*/spine/.*"));
        }
    }

    @DisplayName("not allow null values for")
    @Nested
    class NotAllowNull {

        @DisplayName("prefix pattern")
        @Test
        void prefix() {
            assertThrows(NullPointerException.class, () -> factory.startsWith(null));
        }

        @DisplayName("postfix pattern")
        @Test
        void postfix() {
            assertThrows(NullPointerException.class, () -> factory.endsWith(null));
        }

        @DisplayName("matches pattern")
        @Test
        void regex() {
            assertThrows(NullPointerException.class, () -> factory.matches(null));
        }
    }
}