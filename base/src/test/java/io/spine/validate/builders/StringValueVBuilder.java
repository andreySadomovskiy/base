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
package io.spine.validate.builders;

import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.protobuf.StringValue;
import io.spine.validate.AbstractValidatingBuilder;

/**
 * A test environment validating builder for {@link StringValue} messages.
 */
public final class StringValueVBuilder
        extends AbstractValidatingBuilder<StringValue, StringValue.Builder> {

    /** Prevents instantiation from the outside. */
    private StringValueVBuilder() {
        super();
    }

    public static StringValueVBuilder newBuilder() {
        return new StringValueVBuilder();
    }

    @CanIgnoreReturnValue
    public StringValueVBuilder setValue(String value) {
        getMessageBuilder().setValue(value);
        return this;
    }

    /**
     * Simply calls {@link #internalBuild()}.
     *
     * @apiNote This method is provided only to make sure that the {@link #internalBuild()} which
     * is used by generated VBuilders is not accidentally made private.
     */
    @SuppressWarnings("unused")
    public StringValue callInternalBuild() {
        return internalBuild();
    }
}
