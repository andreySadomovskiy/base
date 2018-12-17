/*
 * Copyright 2018, TeamDev. All rights reserved.
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

package io.spine.js.generate.typeurl;

import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Descriptors.EnumDescriptor;
import com.google.protobuf.Descriptors.FileDescriptor;
import io.spine.js.generate.JsOutput;
import io.spine.type.TypeUrl;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static io.spine.js.generate.given.GivenProject.mainProtoSources;

@DisplayName("TypeUrlsInFile should")
class TypeUrlsInFileTest {

    private final FileDescriptor file = OuterMessage.getDescriptor()
                                                    .getFile();
    private final JsOutput output = new JsOutput();

    @Test
    @DisplayName("generate TypeUrls for messages")
    void messages() {
        TypeUrlsInFile generator = newGenerator();
        generator.generate();
        assertOutHasTypeUrl(OuterMessage.getDescriptor());
        assertOutHasTypeUrl(OuterMessage.NestedMessage.getDescriptor());
    }

    @Test
    @DisplayName("generate TypeUrls for enums")
    void enums() {
        TypeUrlsInFile generator = newGenerator();
        generator.generate();
        assertOutHasTypeUrl(TopLevelEnum.getDescriptor());
        assertOutHasTypeUrl(OuterMessage.NestedEnum.getDescriptor());
    }

    private void assertOutHasTypeUrl(Descriptor message) {
        TypeUrl typeUrl = TypeUrl.from(message);
        assertOutHasTypeUrl(typeUrl);
    }

    private void assertOutHasTypeUrl(EnumDescriptor enumDescriptor) {
        TypeUrl typeUrl = TypeUrl.from(enumDescriptor);
        assertOutHasTypeUrl(typeUrl);
    }

    private void assertOutHasTypeUrl(TypeUrl typeUrl) {
        assertThat(output.toString()).contains(typeUrl.value());
    }

    private TypeUrlsInFile newGenerator() {
        return new TypeUrlsInFile(output, file, mainProtoSources());
    }
}
