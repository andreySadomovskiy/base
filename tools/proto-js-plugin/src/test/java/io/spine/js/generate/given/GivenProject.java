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

package io.spine.js.generate.given;

import io.spine.code.fs.js.DefaultJsProject;
import io.spine.code.fs.js.Directory;
import io.spine.code.proto.FileSet;
import io.spine.tools.gradle.testing.GradleProject;

import java.io.File;
import java.nio.file.Path;
import java.util.List;

import static com.google.common.io.Files.createTempDir;
import static io.spine.code.proto.FileDescriptors.KNOWN_TYPES;
import static io.spine.tools.gradle.TaskName.build;
import static java.util.Collections.singletonList;

public final class GivenProject {

    private static final String TASK_PROTO = "task.proto";
    private static final String PROJECT_NAME = "proto-js-plugin-test";
    private static final List<String> PROTO_FILES = singletonList(TASK_PROTO);

    /** Prevents instantiation of this utility class. */
    private GivenProject() {
    }

    public static FileSet mainFileSet() {
        Path mainDescriptorsDir = project().buildRoot()
                                           .descriptors()
                                           .mainDescriptors();
        Path descriptorSetFile = mainDescriptorsDir.resolve(KNOWN_TYPES);
        return FileSet.parse(descriptorSetFile.toFile());
    }

    public static Directory mainProtoSources() {
        return project().proto()
                        .mainJs();
    }

    public static DefaultJsProject project() {
        File projectDir = createTempDir();
        compileProject(projectDir);
        DefaultJsProject project = DefaultJsProject.at(projectDir);
        return project;
    }

    @SuppressWarnings("ResultOfMethodCallIgnored") // Method annotated with `@CanIgnoreReturnValue`.
    private static void compileProject(File projectDir) {
        GradleProject gradleProject = GradleProject
                .newBuilder()
                .setProjectName(PROJECT_NAME)
                .setProjectFolder(projectDir)
                .addProtoFiles(PROTO_FILES)
                .build();
        gradleProject.executeTask(build);
    }
}
