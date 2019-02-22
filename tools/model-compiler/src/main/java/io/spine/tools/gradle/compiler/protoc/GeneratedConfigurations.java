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

import com.google.common.collect.ImmutableList;
import com.google.protobuf.Message;
import io.spine.annotation.Internal;

/**
 * Abstract base for Gradle extension configurations related to Spine Protoc plugin.
 *
 * @param <M>
 *         actual configuration type
 * @param <K>
 *         Protobuf configuration type
 * @see GeneratedInterfaces
 */
abstract class GeneratedConfigurations<M extends Message,
        F extends FilePatternFactory<M, ?>,
        K extends Message> {

    private final F filePatternFactory;

    GeneratedConfigurations(F filePatternFactory) {
        this.filePatternFactory = filePatternFactory;
    }

    /**
     * Obtains current pattern configurations.
     */
    ImmutableList<FilePattern<M>> patternConfigurations() {
        return ImmutableList.copyOf(filePatternFactory.patterns());
    }

    /**
     * Configures code generation for messages declared in files matching a given pattern.
     *
     * <p>Sample usage is:
     * <pre>
     *     {@code
     *     filePattern().endsWith("events.proto")
     *     }
     * </pre>
     *
     * @return a configuration object for Proto files matching the pattern
     */
    public F filePattern() {
        return filePatternFactory;
    }

    /**
     * Converts this config into a Protobuf configuration.
     */
    @Internal
    public abstract K asProtocConfig();
}
