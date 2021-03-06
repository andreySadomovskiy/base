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

package io.spine.tools.protoc.method;

import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File;
import io.spine.code.fs.java.SourceFile;
import io.spine.type.MessageType;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName("MessageMethod should")
final class MessageMethodTest {

    private static final String INSERTION_POINT_FORMAT = "class_scope:%s";

    @DisplayName("create valid compiler output")
    @Test
    void createValidCompilerOutput() {
        String methodBody = "public void test(){}";
        GeneratedMethod method = new GeneratedMethod(methodBody);
        MessageType type = new MessageType(EnhancedMessage.getDescriptor());
        MessageMethod result = MessageMethod.from(method, type);
        File file = result.asFile();

        assertEquals(methodBody, file.getContent());
        assertEquals(insertionPoint(type), file.getInsertionPoint());
        assertEquals(sourceName(type), file.getName());
    }

    private static String sourceName(MessageType type) {
        return SourceFile.forType(type)
                         .toString()
                         .replace('\\', '/');
    }

    private static String insertionPoint(MessageType type) {
        return String.format(INSERTION_POINT_FORMAT, type.name());
    }
}
