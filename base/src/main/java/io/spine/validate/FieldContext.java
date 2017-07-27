/*
 * Copyright 2017, TeamDev Ltd. All rights reserved.
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

import com.google.common.base.Optional;
import com.google.protobuf.Descriptors.FieldDescriptor;
import io.spine.base.FieldPath;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.Lists.newLinkedList;
import static io.spine.util.Exceptions.newIllegalStateException;
import static java.util.Collections.singleton;

/**
 * A context of a {@link FieldDescriptor}.
 *
 * <p>The context in essence is a list of parent field descriptors,
 * which are leading to the field descriptor.
 *
 * @author Dmytro Grankin
 */
class FieldContext {

    /**
     * Parent descriptors and the target descriptor of this context at the end.
     */
    private final List<FieldDescriptor> descriptors;
    private final FieldPath fieldPath;

    private FieldContext(Iterable<FieldDescriptor> descriptors) {
        this.descriptors = newLinkedList(descriptors);
        this.fieldPath = fieldPathOf(descriptors);
    }

    /**
     * Creates descriptor context for the specified field.
     *
     * @param field the field of the context to create
     * @return the field context
     */
    static FieldContext create(FieldDescriptor field) {
        return new FieldContext(singleton(field));
    }

    /**
     * Creates empty descriptor context.
     *
     * @return the descriptor context
     */
    static FieldContext empty() {
        return new FieldContext(Collections.<FieldDescriptor>emptyList());
    }

    /**
     * Obtains {@code FieldContext} for the specified child.
     *
     * @param child the child descriptor
     * @return the child descriptor context
     */
    FieldContext forChild(FieldDescriptor child) {
        final List<FieldDescriptor> newDescriptors = newLinkedList(descriptors);
        newDescriptors.add(child);
        return new FieldContext(newDescriptors);
    }

    /**
     * Obtains target of this context
     *
     * @return the target descriptor
     */
    FieldDescriptor getTarget() {
        final int targetIndex = descriptors.size() - 1;
        if (targetIndex == -1) {
            throw newIllegalStateException("Empty context cannot have a target.");
        }

        return descriptors.get(targetIndex);
    }

    private Optional<FieldDescriptor> getTargetParent() {
        final int targetParenIndex = descriptors.size() - 2;
        final boolean parentExists = targetParenIndex > -1;
        return parentExists
                ? Optional.of(descriptors.get(targetParenIndex))
                : Optional.<FieldDescriptor>absent();
    }

    /**
     * Obtains field path for the target of the context.
     *
     * @return the field path
     */
    FieldPath getFieldPath() {
        return fieldPath;
    }

    /**
     * Determines whether this context have the same target and
     * the same parent as the specified context.
     *
     * @param other the context to check
     * @return {@code true} if this context have the same target and the same parent
     */
    boolean haveSameTargetAndParent(FieldContext other) {
        final boolean sameTarget = getTarget().equals(other.getTarget());
        if (!sameTarget) {
            return false;
        }

        final Optional<FieldDescriptor> parentFromThis = getTargetParent();
        final Optional<FieldDescriptor> parentFromOther = other.getTargetParent();
        final boolean bothHaveParents = parentFromThis.isPresent() && parentFromOther.isPresent();
        return bothHaveParents && parentFromThis.get()
                                                .equals(parentFromOther.get());
    }

    private static FieldPath fieldPathOf(Iterable<FieldDescriptor> descriptors) {
        final FieldPath.Builder builder = FieldPath.newBuilder();
        for (FieldDescriptor descriptor : descriptors) {
            final String fieldName = descriptor.getName();
            builder.addFieldName(fieldName);
        }
        return builder.build();
    }
}
