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

package io.spine.validate;

import com.google.protobuf.Descriptors.FieldDescriptor;
import io.spine.code.proto.Option;
import io.spine.option.MaxOption;
import io.spine.option.OptionsProto;

import java.util.Optional;

/**
 * An option that defines a maximum value for a numeric field.
 */
final class Max<V extends Number> extends FieldValidatingOption<MaxOption, V> {

    private Max() {
        super(OptionsProto.max);
    }

    /** Returns a new instance of this option. */
    static <V extends Number> Max<V> create() {
        return new Max<>();
    }

    @Override
    public Optional<MaxOption> valueFrom(FieldValue<V> bearer) {
        FieldDescriptor descriptor = bearer.declaration()
                                           .descriptor();
        boolean explicitlySet = Option.from(descriptor, optionExtension())
                                      .isExplicitlySet();
        return explicitlySet
               ? Optional.of(bearer.valueOf(optionExtension()))
               : Optional.empty();
    }

    @Override
    boolean isDefault(FieldValue<V> value) {
        return !optionValue(value).isExplicitlySet();
    }

    @Override
    Constraint<FieldValue<V>> constraint() {
        return new MaxConstraint<>();
    }
}
