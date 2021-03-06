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

package io.spine.js.generate.resolve;

import com.google.common.base.Charsets;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static com.google.common.base.Preconditions.checkArgument;
import static io.spine.util.Exceptions.illegalStateWithCauseOf;
import static java.nio.file.StandardOpenOption.TRUNCATE_EXISTING;
import static java.util.stream.Collectors.toList;

/**
 * A JavaScript file present on a file system.
 */
final class JsFile {

    private static final String EXTENSION = ".js";

    private final Path path;

    JsFile(Path path) {
        checkArgument(path.toString()
                          .endsWith(EXTENSION), "A JavaScript file is expected.");
        checkArgument(path.toFile()
                          .exists(), "File %s doesn't exist", path);
        this.path = path;
    }

    /**
     * Processes import statements in this file.
     *
     * <p>Rewrites the file using the updated imports.
     *
     * @param importFilter
     *         the predicate to filter out imports to be processed
     * @param processFunction
     *         the function processing an import
     */
    void processImports(Predicate<ImportStatement> importFilter,
                        ProcessImport processFunction) {
        try (Stream<String> lines = Files.lines(path)) {
            List<String> updatedLines = lines
                    .map(line -> processLine(line, importFilter, processFunction))
                    .collect(toList());
            rewriteFile(updatedLines);
        } catch (IOException e) {
            throw illegalStateWithCauseOf(e);
        }
    }

    private String processLine(String line,
                               Predicate<ImportStatement> importFilter,
                               ProcessImport processFunction) {
        File file = path.toFile();
        if (ImportStatement.hasImport(line)) {
            ImportStatement importStatement = new ImportStatement(line, file);
            boolean matchesFilter = importFilter.test(importStatement);
            if (matchesFilter) {
                return processFunction.apply(importStatement)
                                      .text();
            }
        }
        return line;
    }

    private void rewriteFile(Iterable<String> lines) {
        try {
            Files.write(path, lines, Charsets.UTF_8, TRUNCATE_EXISTING);
        } catch (IOException e) {
            throw illegalStateWithCauseOf(e);
        }
    }

    /**
     * A function processing an import statement.
     */
    interface ProcessImport extends Function<ImportStatement, ImportStatement> {
    }
}
