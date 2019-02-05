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

package io.spine.js.generate.parse;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableCollection;
import com.google.common.collect.ImmutableList;
import com.google.protobuf.Descriptors.FileDescriptor;
import io.spine.code.js.Directory;
import io.spine.code.js.FileName;
import io.spine.code.proto.FileDescriptors;
import io.spine.code.proto.FileSet;
import io.spine.code.proto.MessageType;
import io.spine.code.proto.TypeSet;
import io.spine.js.generate.GenerationTask;
import io.spine.js.generate.output.CodeLines;
import io.spine.js.generate.output.FileWriter;
import io.spine.js.generate.output.snippet.Comment;
import io.spine.js.generate.output.snippet.Import;

import java.util.Collection;

import static com.google.common.base.Preconditions.checkNotNull;
import static io.spine.js.generate.output.CodeLine.emptyLine;

/**
 * This class writes the {@linkplain GeneratedParser code} for
 * parsing of messages generated by Protobuf JS compiler.
 */
public final class GenerateKnownTypeParsers extends GenerationTask {

    /**
     * The name of the import of parsers registry.
     *
     * <p>Visible so the other generators such as a
     * {@linkplain io.spine.js.generate.field.FieldGenerator field} can use the import.
     */
    public static final String TYPE_PARSERS_IMPORT_NAME = "TypeParsers";
    /** The name of the {@code object-parser.js} import. */
    static final String ABSTRACT_PARSER_IMPORT_NAME = "ObjectParser";
    /**
     * The relative path from the Protobuf root directory to the folder
     * containing sources related to parsing.
     *
     * <p>The path depends on the Spine Web layout.
     */
    private static final String IMPORT_PATH_PREFIX = "../client/parser/";
    @VisibleForTesting
    static final String TYPE_PARSERS_FILE = IMPORT_PATH_PREFIX + "type-parsers.js";
    @VisibleForTesting
    static final String OBJECT_PARSER_FILE = IMPORT_PATH_PREFIX + "object-parser.js";

    private GenerateKnownTypeParsers(Directory generatedRoot) {
        super(generatedRoot);
    }

    public static GenerateKnownTypeParsers createFor(Directory generatedRoot) {
        checkNotNull(generatedRoot);
        return new GenerateKnownTypeParsers(generatedRoot);
    }

    /**
     * Obtains types, which require parsers to be generated.
     *
     * <p>The types with the <a href="https://developers.google.com/protocol-buffers/docs/proto3#json">
     * special JSON mapping</a> should be skipped.
     * Parsers for the types are provided by the Spine Web.
     */
    public static ImmutableCollection<MessageType> targetTypes(FileDescriptor file) {
        if (FileDescriptors.isGoogle(file)) {
            return ImmutableList.of();
        }
        return TypeSet.onlyMessages(file);
    }

    @Override
    protected void generateFor(FileSet fileSet) {
        for (FileDescriptor file : fileSet.files()) {
            generateFor(file);
        }
    }

    private void generateFor(FileDescriptor file) {
        if (targetTypes(file).isEmpty()) {
            return;
        }
        CodeLines code = codeFor(file);
        FileWriter writer = FileWriter.createFor(generatedRoot(), file);
        writer.append(code);
    }

    @VisibleForTesting
    static CodeLines codeFor(FileDescriptor file) {
        ImmutableCollection<MessageType> types = targetTypes(file);
        FileName fileName = FileName.from(file);
        CodeLines lines = new CodeLines();
        lines.append(emptyLine());
        lines.append(Comment.generatedBySpine());
        lines.append(emptyLine());
        lines.append(imports(fileName));
        lines.append(parses(types));
        return lines;
    }

    /**
     * Generates imports required by the code for parsing of messages.
     *
     * @param targetFile
     *         the file to generate imports for
     */
    private static CodeLines imports(FileName targetFile) {
        String abstractParserImport = defaultImport(OBJECT_PARSER_FILE, targetFile)
                .namedAs(ABSTRACT_PARSER_IMPORT_NAME);
        String parsersImport = defaultImport(TYPE_PARSERS_FILE, targetFile)
                .namedAs(TYPE_PARSERS_IMPORT_NAME);
        CodeLines lines = new CodeLines();
        lines.append(abstractParserImport);
        lines.append(parsersImport);
        return lines;
    }

    private static Import defaultImport(String importedFile, FileName targetFile) {
        String pathRelativeToTarget = targetFile.pathToRoot() + importedFile;
        return Import.library(pathRelativeToTarget)
                     .toDefault();
    }

    /**
     * Obtains the code with parsers for specified types.
     *
     * @param messageTypes
     *         all messages in a file to generate parser for
     */
    private static CodeLines parses(Collection<MessageType> messageTypes) {
        CodeLines snippet = new CodeLines();
        for (MessageType message : messageTypes) {
            GeneratedParser parser = new GeneratedParser(message.descriptor());
            snippet.append(emptyLine());
            snippet.append(parser);
        }
        return snippet;
    }
}
