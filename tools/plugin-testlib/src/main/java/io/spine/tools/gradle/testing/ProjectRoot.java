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

package io.spine.tools.gradle.testing;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.google.common.base.Preconditions.checkState;
import static java.nio.file.Files.exists;

/**
 * Finds a root of a project by presence of the {@link #VERSION_GRADLE_NAME version.gradle} file.
 *
 * <p>Starts from the current directory, climbing up, until the file is found. By convention
 * a project should have only one {@link #VERSION_GRADLE_NAME version.gradle} file, which is
 * placed in the root directory of the project.
 */
enum ProjectRoot {

    INSTANCE;

    private static final String VERSION_GRADLE_NAME = "version.gradle";

    static ProjectRoot instance() {
        return INSTANCE;
    }

    /**
     * Obtains a root directory of the project.
     *
     * @throws IllegalStateException
     *         if the {@link #VERSION_GRADLE_NAME version.gradle} file is not found
     */
    Path toPath() {
        Path workingFolderPath = Paths.get(".")
                                      .toAbsolutePath();
        Path extGradleDirPath = workingFolderPath;
        while (extGradleDirPath != null
                && !exists(extGradleDirPath.resolve(VERSION_GRADLE_NAME))) {
            extGradleDirPath = extGradleDirPath.getParent();
        }
        checkState(extGradleDirPath != null,
                   "%s file not found in %s or parent directories.",
                   VERSION_GRADLE_NAME,
                   workingFolderPath);
        return extGradleDirPath;
    }

    /**
     * Obtains root directory of the project.
     *
     * @throws IllegalStateException
     *         if the {@link #VERSION_GRADLE_NAME version.gradle} file is not found
     * @see #toPath()
     */
    File toFile() {
        return toPath().toFile();
    }
}
