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

import io.spine.annotation.Internal;
import io.spine.code.java.ClassName;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.checkerframework.checker.signature.qual.FullyQualifiedName;

import java.util.Optional;

/**
 * Configuration of a generated interface for a certain target.
 *
 * @see GeneratedInterfaces#filePattern()
 * @see GeneratedInterfaces#uuidMessage()
 * @see GeneratedInterfaces#enrichmentMessage()
 */
public interface GeneratedInterfaceConfig extends ProtocConfig {

    /**
     * For the given target, marks the target with the interface with the given fully qualified
     * name.
     *
     * <p>The interface itself is not generated and the user should define it manually.
     *
     * @param interfaceName
     *         the FQN of the interface
     */
    void markWith(@FullyQualifiedName String interfaceName);

    /**
     * Returns current interface name associated with the configuration.
     */
    @Internal
    @Nullable ClassName interfaceName();

    @Internal
    default String safeName() {
        return Optional.ofNullable(interfaceName())
                       .map(ClassName::value)
                       .orElse("");
    }
}
