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

package io.spine.test.options;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Extension;
import com.google.protobuf.ExtensionRegistry;
import io.spine.code.proto.OptionExtensionRegistry;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.google.common.truth.Truth.assertThat;
import static io.spine.option.OptionsProto.required;
import static io.spine.test.options.BytesDirectionOptionProto.direction;

@DisplayName("OptionExtensionRegistry should")
class OptionsRegistryTest {

    @Test
    @DisplayName("contain custom options")
    void custom() {
        assertContains(direction);
    }

    @Test
    @DisplayName("contain standard options")
    void standard() {
        assertContains(required);
    }

    private static void assertContains(Extension<?, ?> option) {
        ExtensionRegistry registry = OptionExtensionRegistry.instance();
        Descriptors.FieldDescriptor descriptor = option.getDescriptor();
        String name = descriptor.getFullName();
        ExtensionRegistry.ExtensionInfo registeredExtension = registry
                .findImmutableExtensionByName(name);
        assertThat(registeredExtension).isNotNull();
        assertThat(registeredExtension.descriptor).isEqualTo(descriptor);
    }
}
