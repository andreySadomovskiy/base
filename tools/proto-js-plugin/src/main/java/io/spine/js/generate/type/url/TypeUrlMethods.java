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

package io.spine.js.generate.type.url;

import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Descriptors.FileDescriptor;
import io.spine.code.js.Directory;
import io.spine.code.js.MethodReference;
import io.spine.code.js.TypeName;
import io.spine.code.proto.Type;
import io.spine.code.proto.TypeSet;
import io.spine.js.generate.Snippet;
import io.spine.js.generate.output.CodeLine;
import io.spine.js.generate.output.CodeLines;
import io.spine.js.generate.output.FileWriter;
import io.spine.js.generate.output.snippet.Comment;
import io.spine.js.generate.output.snippet.Method;
import io.spine.js.generate.output.snippet.Return;
import io.spine.type.TypeUrl;

import static io.spine.js.generate.output.CodeLine.emptyLine;

/**
 * Generates a method to obtain a {@code TypeUrl} for each type in a file.
 *
 * <p>The class handles messages and enums of any nesting level.
 */
final class TypeUrlMethods implements Snippet {

    private static final String METHOD_NAME = "typeUrl";

    private final FileDescriptor file;
    private final Directory generatedRoot;

    TypeUrlMethods(FileDescriptor file, Directory generatedRoot) {
        this.file = file;
        this.generatedRoot = generatedRoot;
    }

    @Override
    public CodeLines value() {
        CodeLines output = new CodeLines();
        TypeSet types = TypeSet.messagesAndEnums(file);
        for (Type type : types.types()) {
            Snippet method = typeUrlMethod(type);
            output.append(Comment.generatedBySpine());
            output.append(method);
            output.append(emptyLine());
        }
        return output;
    }

    /**
     * Appends the snippet to the file.
     */
    void appendToFile() {
        CodeLines lines = value();
        FileWriter writer = FileWriter.createFor(generatedRoot, file);
        writer.append(lines);
    }

    @VisibleForTesting
    static Method typeUrlMethod(Type type) {
        TypeName typeName = TypeName.from(type.descriptor());
        String methodName = MethodReference.onType(typeName, METHOD_NAME)
                                           .value();
        CodeLine returnStatement = returnTypeUrl(type);
        Method method = Method
                .newBuilder(methodName)
                .appendToBody(returnStatement)
                .build();
        return method;
    }

    private static CodeLine returnTypeUrl(Type type) {
        TypeUrl typeUrl = type.url();
        return Return.stringLiteral(typeUrl.value());
    }
}
