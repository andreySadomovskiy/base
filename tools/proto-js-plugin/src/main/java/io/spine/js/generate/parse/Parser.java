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

package io.spine.js.generate.parse;

import com.google.common.annotations.VisibleForTesting;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Descriptors.Descriptor;
import io.spine.code.js.MethodReference;
import io.spine.code.js.TypeName;
import io.spine.js.generate.Snippet;
import io.spine.js.generate.field.FieldGenerator;
import io.spine.js.generate.field.FieldGenerators;
import io.spine.js.generate.output.CodeLines;
import io.spine.js.generate.output.snippet.Method;
import io.spine.js.generate.output.snippet.Return;
import io.spine.js.generate.output.snippet.VariableDeclaration;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.js.generate.output.CodeLine.emptyLine;
import static java.lang.String.format;

/**
 * A parser of a generated Protobuf message.
 *
 * <p>This parser should be generated for all messages except standard ones
 * like {@code Any}, {@code int32}, {@code Timestamp}. The parsers for these
 * standard types are manually created and require no code generation.
 *
 * <p>Code provided by the class is in {@code ES5} standard
 * since Protobuf compiler generates Javascript in {@code ES5}.
 *
 * <p>The class is effectively {@code final} and is left non-{@code final} only for testing
 * purposes.
 */
@SuppressWarnings("DuplicateStringLiteralInspection" /* Used in a different context. */)
public class Parser implements Snippet {

    /**
     * The name of the {@code fromObject} method return value.
     *
     * <p>This value represents the generated JS message whose fields are parsed and set from the
     * JS object.
     */
    public static final String MESSAGE = "msg";
    /** The parameter name of the {@code fromObject} method. */
    public static final String FROM_OBJECT_ARG = "obj";
    /** The name of the abstract parser to extend from. */
    static final String ABSTRACT_PARSER = "ObjectParser";
    /** The name of the method declared on an abstract parser. */
    static final String PARSE_METHOD = "fromObject";

    /** The message to generate the parser for. */
    private final Descriptor message;
    private final TypeName messageName;

    Parser(Descriptor message) {
        checkNotNull(message);
        this.message = message;
        this.messageName = TypeName.from(message);
    }

    @Override
    public CodeLines value() {
        CodeLines lines = new CodeLines();
        lines.append(constructor());
        lines.append(initPrototype());
        lines.append(initConstructor());
        lines.append(fromObjectMethod());
        return lines;
    }

    /**
     * Obtains the type of the parser to be generated.
     */
    TypeName typeName() {
        return TypeName.of(messageName + "Parser");
    }

    private Method constructor() {
        MethodReference reference = MethodReference.constructor(typeName());
        String callSuper = format("%s.call(this);", superClass());
        return Method
                .newBuilder(reference)
                .appendToBody(callSuper)
                .build();
    }

    private String initPrototype() {
        String result = format("%s = Object.create(%s.prototype);",
                               prototypeReference(), superClass());
        return result;
    }

    private String initConstructor() {
        MethodReference reference = MethodReference.onPrototype(typeName(), "constructor");
        String result = format("%s = %s;", reference, typeName());
        return result;
    }

    /**
     * Generates the {@code fromObject} method, going through the JS object fields iteratively,
     * adding the code to parse them and assign to the JS message.
     *
     * <p>If the object is {@code null}, the returned value will be {@code null}.
     */
    @VisibleForTesting
    CodeLines fromObjectMethod() {
        String methodName = MethodReference.onPrototype(typeName(), PARSE_METHOD)
                                           .value();
        CodeLines lines = new CodeLines();
        lines.enterMethod(methodName, FROM_OBJECT_ARG);
        checkParsedObject(lines);
        lines.append(emptyLine());
        lines.append(initializedMessageInstance(messageName));
        handleMessageFields(lines, message);
        lines.append(Return.value(MESSAGE));
        lines.exitMethod();
        return lines;
    }

    /**
     * Adds the code checking that {@code fromObject} argument is not null.
     */
    private static void checkParsedObject(CodeLines output) {
        output.ifNull(FROM_OBJECT_ARG);
        output.append(Return.nullReference());
        output.exitBlock();
    }

    private static VariableDeclaration initializedMessageInstance(TypeName typeName) {
        return VariableDeclaration.newInstance(MESSAGE, typeName);
    }

    /**
     * Adds the code necessary to parse and set the message fields.
     */
    @VisibleForTesting
    static void handleMessageFields(CodeLines output, Descriptor message) {
        for (Descriptors.FieldDescriptor field : message.getFields()) {
            output.append(emptyLine());
            FieldGenerator generator = FieldGenerators.createFor(field, output);
            generator.generate();
        }
    }

    /**
     * Obtains the reference to the prototype of the parser.
     */
    private String prototypeReference() {
        return typeName() + ".prototype";
    }

    private static String superClass() {
        return ABSTRACT_PARSER;
    }
}
