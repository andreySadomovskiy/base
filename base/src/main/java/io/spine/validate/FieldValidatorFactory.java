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

package io.spine.validate;

import com.google.protobuf.Descriptors.FieldDescriptor.JavaType;

import static com.google.common.base.Preconditions.checkNotNull;
import static java.lang.String.format;

/**
 * Creates {@link FieldValidator}s.
 */
class FieldValidatorFactory {

    private FieldValidatorFactory() {
        // Prevent instantiation of this utility class.
    }

    /**
     * Creates a new validator instance according to the field type and validates the field.
     *
     * <p>The target field of the resulting validator is represented with a linear data structure,
     * i.e. not a map.
     *
     * @param fieldValue
     *         a value of the field to validate
     * @param fieldType
     *         the required field type
     * @param strict
     *         if {@code true} validators would always assume that the field is
     */
    private static FieldValidator<?> createForLinear(FieldValue fieldValue,
                                                     JavaType fieldType,
                                                     boolean strict) {
        checkNotNull(fieldType);
        switch (fieldType) {
            case MESSAGE:
                return new MessageFieldValidator(fieldValue, strict);
            case INT:
                return new IntegerFieldValidator(fieldValue);
            case LONG:
                return new LongFieldValidator(fieldValue);
            case FLOAT:
                return new FloatFieldValidator(fieldValue);
            case DOUBLE:
                return new DoubleFieldValidator(fieldValue);
            case STRING:
                return new StringFieldValidator(fieldValue, strict);
            case BYTE_STRING:
                return new ByteStringFieldValidator(fieldValue);
            case BOOLEAN:
                return new BooleanFieldValidator(fieldValue);
            case ENUM:
                return new EnumFieldValidator(fieldValue);
            default:
                throw fieldTypeIsNotSupported(fieldType);
        }
    }

    static FieldValidator<?> create(FieldValue fieldValue) {
        return createForLinear(fieldValue, fieldValue.javaType(), false);
    }

    static FieldValidator<?> createStrict(FieldValue fieldValue) {
        return createForLinear(fieldValue, fieldValue.javaType(), true);
    }

    private static IllegalArgumentException fieldTypeIsNotSupported(JavaType type) {
        String msg = format("The field type is not supported for validation: %s", type);
        throw new IllegalArgumentException(msg);
    }
}
