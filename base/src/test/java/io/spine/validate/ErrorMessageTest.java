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

import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import com.google.protobuf.Message;
import io.spine.option.OptionsProto;
import io.spine.test.validate.CustomMessageRequiredByteStringFieldValue;
import io.spine.test.validate.CustomMessageRequiredEnumFieldValue;
import io.spine.test.validate.CustomMessageRequiredMsgFieldValue;
import io.spine.test.validate.CustomMessageRequiredRepeatedMsgFieldValue;
import io.spine.test.validate.CustomMessageRequiredStringFieldValue;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static io.spine.validate.MessageValidatorTest.MESSAGE_VALIDATOR_SHOULD;
import static org.junit.jupiter.api.Assertions.assertEquals;

@DisplayName(MESSAGE_VALIDATOR_SHOULD
          + "propagate proper error message if custom message set and required")
class ErrorMessageTest extends MessageValidatorTest {

    @Test
    @DisplayName("Message field is NOT set")
    void msgNotSet() {
        CustomMessageRequiredMsgFieldValue invalidMsg =
                CustomMessageRequiredMsgFieldValue.getDefaultInstance();
        assertErrorMessage(invalidMsg);
    }

    @Test
    @DisplayName("String field is NOT set")
    void stringNotSet() {
        CustomMessageRequiredStringFieldValue invalidMsg =
                CustomMessageRequiredStringFieldValue.getDefaultInstance();
        assertErrorMessage(invalidMsg);
    }

    @Test
    @DisplayName("ByteString field is NOT set")
    void bytesNotSet() {
        CustomMessageRequiredByteStringFieldValue invalidMsg =
                CustomMessageRequiredByteStringFieldValue.getDefaultInstance();
        assertErrorMessage(invalidMsg);
    }

    @Test
    @DisplayName("repeated field is NOT set")
    void repeatedNotSet() {
        CustomMessageRequiredRepeatedMsgFieldValue invalidMsg =
                CustomMessageRequiredRepeatedMsgFieldValue.getDefaultInstance();
        assertErrorMessage(invalidMsg);
    }

    @Test
    @DisplayName("Enum field is NOT set")
    void enumNotSet() {
        CustomMessageRequiredEnumFieldValue invalidMsg =
                CustomMessageRequiredEnumFieldValue.getDefaultInstance();
        assertErrorMessage(invalidMsg);
    }

    private void assertErrorMessage(Message message) {
        assertNotValid(message);
        Descriptor descriptor = message.getDescriptorForType();
        String expectedErrorMessage = getCustomErrorMessage(descriptor);
        checkErrorMessage(expectedErrorMessage);
    }

    private void checkErrorMessage(String expectedMessage) {
        ConstraintViolation constraintViolation = firstViolation();
        assertEquals(expectedMessage, constraintViolation.getMsgFormat());
    }

    private static String getCustomErrorMessage(Descriptor descriptor) {
        Descriptors.FieldDescriptor firstFieldDescriptor = descriptor.getFields()
                                                                     .get(0);
        return firstFieldDescriptor.getOptions()
                                   .getExtension(OptionsProto.ifMissing)
                                   .getMsgFormat();
    }
}
