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

import com.google.common.collect.ImmutableList;
import io.spine.js.generate.resolve.JsFile.ProcessImport;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junitpioneer.jupiter.TempDirectory;
import org.junitpioneer.jupiter.TempDirectory.TempDir;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static com.google.common.truth.Truth.assertThat;
import static io.spine.js.generate.resolve.given.Given.importWithPath;

@ExtendWith(TempDirectory.class)
@DisplayName("JavaScript file should")
class JsFileTest {

    private static final String RESOLVED_IMPORT_PATH = "resolved";
    private static final ProcessImport PROCESS_IMPORT_FUNCTION =
            statement -> statement.replacePath(RESOLVED_IMPORT_PATH);

    private Path filePath;

    @BeforeEach
    void setUp(@TempDir Path tempDir) {
        filePath = tempDir.resolve("test-file.js");
    }

    @Test
    @DisplayName("process imports")
    void processImports() throws IOException {
        ImportStatement firstImport = importStatement("first.js");
        ImportStatement secondImport = importStatement("second.js");
        String comment = "// Just a comment";
        writeFile(firstImport.text(),
                  comment,
                  secondImport.text());
        resolveImports();
        List<String> expectedLines = ImmutableList.of(
                expectedImport(firstImport),
                comment,
                expectedImport(secondImport)
        );
        List<String> updatedLines = Files.readAllLines(filePath);
        assertThat(updatedLines).isEqualTo(expectedLines);
    }

    private void resolveImports() {
        JsFile file = new JsFile(filePath);
        file.processImports(importStatement -> true,
                            PROCESS_IMPORT_FUNCTION);
    }

    private void writeFile(String... lines) throws IOException {
        List<String> linesList = Arrays.asList(lines);
        Files.write(filePath, linesList);
    }

    private ImportStatement importStatement(String importedFile) {
        return importWithPath(importedFile, filePath.toFile());
    }

    private static String expectedImport(ImportStatement originalImport) {
        return PROCESS_IMPORT_FUNCTION.apply(originalImport)
                                      .text();
    }
}
