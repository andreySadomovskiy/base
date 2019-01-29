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

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import com.google.protobuf.Descriptors.FileDescriptor;
import io.spine.code.js.Directory;
import io.spine.code.js.FileName;
import io.spine.code.js.FileReference;
import io.spine.code.proto.FileSet;
import io.spine.js.generate.GenerationTask;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * A task to resolve imports in generated files.
 *
 * <p>Supports only {@code CommonJs} imports.
 *
 * <p>The task should be performed last among {@linkplain GenerationTask generation tasks}
 * to ensure that imports won't be modified after execution of this task.
 */
public final class ResolveImports extends GenerationTask {

    /**
     * The relative path from the test sources directory to the main sources directory.
     *
     * <p>Depends on the structure of Spine Web project.
     */
    private static final String TEST_PROTO_RELATIVE_TO_MAIN = "../main/";

    private final List<ExternalModule> modules;

    public ResolveImports(Directory generatedRoot, List<ExternalModule> modules) {
        super(generatedRoot);
        this.modules = ImmutableList.copyOf(modules);
    }

    @Override
    protected void generateFor(FileSet fileSet) {
        for (FileDescriptor file : fileSet.files()) {
            FileName fileName = FileName.from(file);
            Path filePath = generatedRoot().resolve(fileName);
            resolveInFile(filePath);
        }
    }

    @VisibleForTesting
    void resolveInFile(Path filePath) {
        JsFile file = new JsFile(filePath);
        file.processImports(new UnresolvedRelativeImport(), this::resolveRelativeImports);
    }

    /**
     * Attempts to resolve a relative import.
     */
    private ImportStatement resolveRelativeImports(ImportStatement resolvable) {
        Optional<ImportStatement> mainSourceImport = resolveInMainSources(resolvable);
        if (mainSourceImport.isPresent()) {
            return mainSourceImport.get();
        }
        FileReference fileReference = resolvable.path();
        for (ExternalModule module : modules) {
            if (module.provides(fileReference)) {
                FileReference fileInModule = module.fileInModule(fileReference);
                return resolvable.replacePath(fileInModule.value());
            }
        }
        return resolvable;
    }

    /**
     * Attempts to resolve a relative import among main sources.
     */
    private static Optional<ImportStatement> resolveInMainSources(ImportStatement resolvable) {
        String fileReference = resolvable.path()
                                         .value();
        String delimiter = FileReference.currentDirectory();
        int insertionIndex = fileReference.lastIndexOf(delimiter) + delimiter.length();
        String updatedReference = fileReference.substring(0, insertionIndex)
                + TEST_PROTO_RELATIVE_TO_MAIN
                + fileReference.substring(insertionIndex);
        ImportStatement updatedImport = resolvable.replacePath(updatedReference);
        return updatedImport.importedFileExists()
               ? Optional.of(updatedImport)
               : Optional.empty();
    }

    /**
     * A predicate to match an import of a file that cannot be found on a file system.
     */
    private static final class UnresolvedRelativeImport implements Predicate<ImportStatement> {

        @CanIgnoreReturnValue
        @Override
        public boolean apply(@Nullable ImportStatement statement) {
            checkNotNull(statement);
            FileReference fileReference = statement.path();
            return fileReference.isRelative() && !statement.importedFileExists();
        }
    }
}
