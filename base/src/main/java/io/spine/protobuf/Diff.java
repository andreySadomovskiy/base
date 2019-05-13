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

package io.spine.protobuf;

import com.google.common.base.Objects;
import com.google.common.collect.ImmutableSet;
import com.google.protobuf.Descriptors.FieldDescriptor;
import com.google.protobuf.Message;
import io.spine.code.proto.FieldDeclaration;

import java.util.Map;
import java.util.Set;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.collect.ImmutableSet.toImmutableSet;
import static com.google.common.collect.Sets.symmetricDifference;
import static java.lang.String.format;
import static java.util.stream.Collectors.toSet;

/**
 * Difference between two messages of the same type.
 */
public final class Diff {

    private final ImmutableSet<FieldDeclaration> changedFields;

    private Diff(ImmutableSet<FieldDeclaration> changedFields) {
        this.changedFields = changedFields;
    }

    /**
     * Calculates the difference between the given two messages.
     *
     * @param previous
     *         the previous state of the message
     * @param current
     *         the current state of the message
     * @param <M>
     *         the type of the messages
     * @return difference between the messages
     * @throws IllegalArgumentException
     *         if the types of the messages are not the same
     */
    public static <M extends Message> Diff between(M previous, M current) {
        checkNotNull(previous);
        checkNotNull(current);
        checkArgument(previous.getClass()
                              .equals(current.getClass()));
        ImmutableSet<FieldDeclaration> fields =
                symmetricDifference(decompose(previous), decompose(current))
                        .stream()
                        .map(tuple -> tuple.declaration)
                        .collect(toImmutableSet());
        return new Diff(fields);
    }

    private static Set<FieldTuple> decompose(Message message) {
        Map<FieldDescriptor, Object> fieldMap = message.getAllFields();
        return fieldMap
                .entrySet()
                .stream()
                .map(entry -> new FieldTuple(
                        new FieldDeclaration(entry.getKey()), entry.getValue()
                ))
                .collect(toSet());
    }

    /**
     * Checks if the given field is present in the diff or not.
     *
     * @param field
     *         the field declaration to find
     * @return {@code true} if the field has different values in the two given messages,
     *         {@code false} otherwise
     */
    public boolean changed(FieldDeclaration field) {
        return changedFields.contains(field);
    }

    private static final class FieldTuple {

        private final FieldDeclaration declaration;
        private final Object value;

        private FieldTuple(FieldDeclaration declaration, Object value) {
            this.declaration = checkNotNull(declaration);
            this.value = checkNotNull(value);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof FieldTuple)) {
                return false;
            }
            FieldTuple tuple = (FieldTuple) o;
            return Objects.equal(declaration, tuple.declaration) &&
                    Objects.equal(value, tuple.value);
        }

        @Override
        public int hashCode() {
            return Objects.hashCode(declaration, value);
        }

        @Override
        public String toString() {
            return format("%s %s = %d",
                          declaration.typeName(),
                          declaration.name(),
                          declaration.fieldNumber());
        }
    }
}