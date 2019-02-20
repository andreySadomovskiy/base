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

package io.spine.tools.protoc.messageinterface;

import com.google.common.collect.ImmutableList;
import com.google.protobuf.compiler.PluginProtos.CodeGeneratorResponse.File;
import io.spine.code.proto.MessageType;
import io.spine.code.proto.Type;
import io.spine.tools.protoc.CompilerOutput;
import io.spine.tools.protoc.GeneratedInterfacesConfig;
import io.spine.tools.protoc.SpineProtoGenerator;
import io.spine.tools.protoc.SpineProtocConfig;

import java.util.Collection;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * The {@link SpineProtoGenerator} implementation generating the specific interfaces implemented by
 * some message types.
 *
 * <p>The generator produces two types of {@link File CodeGeneratorResponse.File} instances
 * representing:
 * <ul>
 *     <li>the interfaces derived from {@link com.google.protobuf.Message}
 *     <li>the insertion entries to the existing messages (see
 *         {@link File#getInsertionPoint() CodeGeneratorResponse.File.insertionPoint}).
 * </ul>
 */
public final class MessageInterfaceGenerator extends SpineProtoGenerator {

    private final PatternScanner patternScanner;

    /** Prevents singleton class instantiation. */
    private MessageInterfaceGenerator(GeneratedInterfacesConfig parameter) {
        super();
        this.patternScanner = new PatternScanner(parameter);
    }

    /**
     * Retrieves the single instance of the {@code MessageInterfaceGenerator} type.
     */
    public static SpineProtoGenerator instance(SpineProtocConfig parameter) {
        checkNotNull(parameter);
        return new MessageInterfaceGenerator(parameter.getGeneratedInterfaces());
    }

    /**
     * {@inheritDoc}
     *
     * <p>The {@code MessageInterfaceGenerator} implementation performs the message processing
     * as follows:
     * <ol>
     *     <li>Checks the message declaration matches any built-in interface contract. If it does,
     *         the message insertion point with the appropriate interface name is generated.
     *     <li>Checks the message has {@code (is)} option. If it does, the interface name is
     *         extracted from it and both the interface and the message insertion point are
     *         generated.
     *     <li>Checks the message file has {@code (every_is)} option. If it does, the interface
     *         name is extracted from it and both the interface and the message insertion point are
     *         generated.
     *     <li>Otherwise, no compiler response is generated for this message type.
     * </ol>
     */
    @Override
    protected Collection<CompilerOutput> processType(Type<?, ?> type) {
        return type instanceof MessageType
               ? processMessageType((MessageType) type)
               : ImmutableList.of();
    }

    private ImmutableList<CompilerOutput> processMessageType(MessageType type) {
        ImmutableList.Builder<CompilerOutput> result = ImmutableList.builder();

        Optional<CompilerOutput> matched = patternScanner.scan(type);
        matched.ifPresent(result::add);

        Collection<CompilerOutput> fromMsgOption = MessageAndInterface.scanMsgOption(type);
        result.addAll(fromMsgOption);

        if (fromMsgOption.isEmpty()) {
            Collection<CompilerOutput> fromFileOption = MessageAndInterface.scanFileOption(type);
            result.addAll(fromFileOption);
        }
        return result.build();
    }
}
