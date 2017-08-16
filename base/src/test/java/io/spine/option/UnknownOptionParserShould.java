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

package io.spine.option;

import com.google.protobuf.BoolValue;
import com.google.protobuf.DescriptorProtos.DescriptorProto;
import com.google.protobuf.DescriptorProtos.MessageOptions;
import com.google.protobuf.GeneratedMessage.GeneratedExtension;
import com.google.protobuf.StringValue;
import org.junit.Test;

import java.util.Collection;

import static io.spine.option.OptionsProto.enrichment;
import static java.util.Collections.singleton;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * @author Dmytro Grankin
 */
public class UnknownOptionParserShould {

    private final AnUnknownOptionParser parser = new AnUnknownOptionParser(enrichment);

    @Test
    public void return_empty_collection_if_option_is_not_present() {
        final DescriptorProto definitionWithoutOption = StringValue.getDescriptor()
                                                                   .toProto();
        final Collection<String> result = parser.parseUnknownOption(definitionWithoutOption);
        assertTrue(result.isEmpty());
    }

    @Test
    public void parse_unknown_option_if_option_is_present() {
        final DescriptorProto descriptor = AnUnknownOptionParser.MESSAGE_WITH_OPTION;
        final Collection<String> result = parser.parseUnknownOption(descriptor);
        assertFalse(result.isEmpty());
    }

    private static class AnUnknownOptionParser extends UnknownOptionParser<MessageOptions,
                                                                           DescriptorProto,
                                                                           String> {

        /**
         * If this descriptor is used to parse an unknown option,
         * {@link #UNKNOWN_OPTION_VALUE} will be returned.
         */
        private static final DescriptorProto MESSAGE_WITH_OPTION = BoolValue.getDescriptor()
                                                                            .toProto();
        private static final String UNKNOWN_OPTION_VALUE = "An unknown option.";

        private AnUnknownOptionParser(GeneratedExtension<MessageOptions, String> option) {
            super(option);
        }

        @SuppressWarnings("ReturnOfNull") // Contract of the method.
        @Override
        protected String getUnknownOptionValue(DescriptorProto descriptor, int optionNumber) {
            return descriptor.equals(MESSAGE_WITH_OPTION)
                    ? UNKNOWN_OPTION_VALUE
                    : null;
        }

        @Override
        public Collection<String> parse(String optionValue) {
            return singleton(UNKNOWN_OPTION_VALUE);
        }
    }
}