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

package io.spine.tools.protoc;

import io.spine.code.java.ClassName;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.validate.Validate.checkNotDefault;

/**
 * An utility for working with {@link UuidConfig} and {@link ConfigByPattern} code generation task
 * configurations..
 */
public final class ProtocTaskConfigs {

    /** Prevents instantiation of this utility class. */
    private ProtocTaskConfigs() {
    }

    /**
     * Creates a new {@link UuidConfig} instance from the supplied {@code className}.
     *
     * @throws NullPointerException
     *         if the class name is {@code null}
     */
    public static UuidConfig uuidConfig(ClassName className) {
        checkNotNull(className);
        return UuidConfig
                .newBuilder()
                .setValue(className.value())
                .build();
    }

    /**
     * Creates a new {@link ConfigByPattern} instance from the supplied {@code className} and
     * {@code pattern}.
     *
     * @throws NullPointerException
     *         if the class name or pattern is {@code null}
     */
    public static ConfigByPattern
    byPatternConfig(ClassName className, FilePattern pattern) {
        checkNotNull(className);
        checkNotDefault(pattern);
        return ConfigByPattern
                .newBuilder()
                .setValue(className.value())
                .setPattern(pattern)
                .build();
    }
}
