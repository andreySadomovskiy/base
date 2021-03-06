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

package io.spine.validate.given;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.ByteString;
import com.google.protobuf.ProtocolStringList;
import com.google.protobuf.StringValue;
import io.spine.base.FieldPath;
import io.spine.validate.ConstraintViolation;

import static io.spine.base.Identifier.newUuid;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class MessageValidatorTestEnv {

    public static final double EQUAL_MIN = 16.5;
    public static final double GREATER_THAN_MIN = EQUAL_MIN + 5;
    public static final double LESS_THAN_MIN = EQUAL_MIN - 5;

    public static final double EQUAL_MAX = 64.5;
    public static final double GREATER_THAN_MAX = EQUAL_MAX + 5;

    public static final double INT_DIGIT_COUNT_GREATER_THAN_MAX = 123.5;
    public static final double INT_DIGIT_COUNT_EQUAL_MAX = 12.5;

    public static final String VALUE = "value";
    public static final String EMAIL = "email";
    public static final String OUTER_MSG_FIELD = "outer_msg_field";
    public static final String LESS_THAN_MIN_MSG = "Number must be greater than or equal to 16.5.";
    public static final String GREATER_MAX_MSG = "Number must be less than or equal to 64.5.";
    public static final String MATCH_REGEXP_MSG = "String must match the regular expression '%s'.";

    public static final double INT_DIGIT_COUNT_LESS_THAN_MAX = 1.5;
    public static final double LESS_THAN_MAX = EQUAL_MAX - 5;

    /** Prevent instantiation of this test environment. */
    private MessageValidatorTestEnv() {
    }

    public static void assertFieldPathIs(ConstraintViolation violation, String... expectedFields) {
        FieldPath path = violation.getFieldPath();
        ProtocolStringList actualFields = path.getFieldNameList();
        assertEquals(expectedFields.length, actualFields.size());
        assertEquals(ImmutableList.copyOf(expectedFields), ImmutableList.copyOf(actualFields));
    }

    public static StringValue newStringValue() {
        return StringValue.of(newUuid());
    }

    public static ByteString newByteString() {
        ByteString bytes = ByteString.copyFromUtf8(newUuid());
        return bytes;
    }
}
