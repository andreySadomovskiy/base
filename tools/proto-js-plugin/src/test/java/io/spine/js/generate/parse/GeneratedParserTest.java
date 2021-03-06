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

package io.spine.js.generate.parse;

import com.google.protobuf.Any;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.FieldDescriptor;
import io.spine.code.gen.js.TypeName;
import io.spine.js.generate.output.CodeLines;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static io.spine.js.generate.given.Generators.assertContains;
import static io.spine.js.generate.parse.GeneratedParser.FROM_OBJECT_ARG;
import static io.spine.js.generate.parse.GeneratedParser.PARSE_METHOD;
import static java.lang.System.lineSeparator;

@DisplayName("GeneratedParser should")
class GeneratedParserTest {

    private final Descriptor message = Any.getDescriptor();
    private final GeneratedParser parser = new GeneratedParser(message);

    @Test
    @DisplayName("generate `fromObject` method for message")
    void generateFromObject() {
        CodeLines snippet = parser.fromObjectMethod();
        String expectedName = expectedParserName(message) + ".prototype." + PARSE_METHOD;
        String methodDeclaration = expectedName + " = function(" + FROM_OBJECT_ARG;
        assertContains(snippet, methodDeclaration);
    }

    @Test
    @DisplayName("check parsed object for null in `fromObject` method")
    void checkJsObjectForNull() {
        CodeLines snippet = parser.fromObjectMethod();
        String check = "if (" + FROM_OBJECT_ARG + " === null) {";
        assertContains(snippet, check);
    }

    @Test
    @DisplayName("handle message fields in `fromObject` method")
    void handleMessageFields() {
        CodeLines lines = parser.fromObjectMethod();
        for (FieldDescriptor fieldDescriptor : message.getFields()) {
            assertContains(lines, fieldDescriptor.getJsonName());
        }
    }

    @Test
    @DisplayName("generate whole snippet")
    void generateWholeSnippet() {
        CodeLines lines = parser.value();
        assertCtorDeclaration(lines, message);
        assertPrototypeInitialization(lines, message);
        assertCtorInitialization(lines, message);
        assertParseMethod(lines, message);
    }

    @Test
    @DisplayName("allow to call the parse object method")
    void callParseObjectMethod() {
        String call = GeneratedParser.parseMethodCall("someParser", "{}");
        String expected = "someParser.fromObject({})";
        assertThat(call).isEqualTo(expected);
    }

    private static void assertCtorDeclaration(CodeLines lines, Descriptor message) {
        String expected = expectedParserName(message) + " = function() {" + lineSeparator()
                + "  ObjectParser.call(this);" + lineSeparator()
                + "};";
        assertThat(lines.toString()).contains(expected);
    }

    private static void assertPrototypeInitialization(CodeLines lines, Descriptor message) {
        assertThat(lines.toString()).contains(
                expectedParserName(message) + ".prototype = Object.create(ObjectParser.prototype);"
        );
    }

    private static void assertCtorInitialization(CodeLines lines, Descriptor message) {
        TypeName expectedName = expectedParserName(message);
        assertThat(lines.toString()).contains(
                expectedName + ".prototype.constructor = " + expectedName + ';'
        );
    }

    private static void assertParseMethod(CodeLines lines, Descriptor message) {
        String expected = new GeneratedParser(message).fromObjectMethod()
                                                      .toString();
        assertThat(lines.toString()).contains(expected);
    }

    private static TypeName expectedParserName(Descriptor message) {
        return TypeName.ofParser(message);
    }
}
